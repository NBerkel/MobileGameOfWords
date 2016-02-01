package com.niels.geooulu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link gameplay.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link gameplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class gameplay extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    TextView currentGameWord;
    TextView wordProgress;
    ImageView leftCircleImageView;
    ImageView rightCircleImageView;
    List<String> words;
    List<String> wordsUserRating;
    boolean answerProvided = false;
    int currentWord = 0;
    JSONArray wordJsonRatings = new JSONArray();

    private OnFragmentInteractionListener mListener;

    public gameplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment gameplay.
     */
    // TODO: Rename and change types and number of parameters
    public static gameplay newInstance(String param1, String param2) {
        gameplay fragment = new gameplay();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_gameplay, container, false);
        currentGameWord = (TextView) view.findViewById(R.id.currentGameWord);
        wordProgress = (TextView) view.findViewById(R.id.wordProgress);
        leftCircleImageView = (ImageView) view.findViewById(R.id.leftCircleImageView);
        rightCircleImageView = (ImageView) view.findViewById(R.id.rightCircleImageView);

        words = new ArrayList<>();
        wordsUserRating = new ArrayList<>();

        rightCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relevantBtnPressed();
            }
        });
        leftCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irrelevantBtnPressed();
            }
        });

        //TODO randomly determine position of irrelevant / relevant button?

        try {
            getGameWords();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getContext());
        Tracker mTracker;
        mTracker = analytics.newTracker(R.xml.global_tracker);
        mTracker.setScreenName("Image~" + "gameplay_nongamified");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return view;
    }

    private void getGameWords() throws JSONException {
        String nickname = HomeScreen.getNickname();
        String gps_zone = GameplayStats.getGPSZone();

        final JSONObject dataJSON = new JSONObject();
        dataJSON.put("nickname", nickname);
        dataJSON.put("gps_zone", gps_zone);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = MainActivity.getIP() + "getwords.php";

        // Collect word list
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setGameWords(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GeoOulu", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setGameWords(String response) {
        try {
            JSONArray strJson = new JSONArray(response);
            if (strJson.length() >= 5) {
                for (int i = 0; i < strJson.length(); i++) {
                    JSONObject object = strJson.getJSONObject(i);

                    JSONObject word = new JSONObject();
                    word.put("word", object.getString("word"));
                    word.put("number_of_times_voted_relevant", object.getString("number_of_times_voted_relevant"));
                    word.put("number_of_times_voted_irrelevant", object.getString("number_of_times_voted_irrelevant"));
                    words.add(object.getString("word"));
                }
                startGame();
            } else {
                // Return home
                Fragment fragment = new GameplayNoWords();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void irrelevantBtnPressed() {
        if (words.size() != 0) {
            SendLog sendLog = new SendLog();
            try {
                sendLog.UpdateLogDB(words.get(currentWord), false, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            answerProvided = true;
            wordsUserRating.add("irrelevant");
            currentWord++;
            nextWord();
        }
    }

    private void relevantBtnPressed() {
        if (words.size() != 0) {
            SendLog sendLog = new SendLog();
            try {
                sendLog.UpdateLogDB(words.get(currentWord), true, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            answerProvided = true;
            wordsUserRating.add("relevant");
            currentWord++;
            nextWord();
        }
    }

    private void startGame() {
        nextWord();
    }

    private void nextWord() {
        if (currentWord < words.size()) {
            answerProvided = false;
            Log.d("Niels", "nextWord called");
            Log.d("Niels", String.valueOf(words.size()));
            Log.d("Niels", String.valueOf(currentWord));
            currentGameWord.setText(words.get(currentWord));

            wordProgress.setText("Word " + (currentWord + 1) + "/" + words.size());
        } else {
            finishGame();
        }
    }

    private void finishGame() {
        Fragment fragment = new finishGame();

        try {
            transferWordRatings();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void transferWordRatings() throws JSONException {
        for (int i = 0; i < words.size(); i++) {
            JSONObject wordAnswer = new JSONObject();

            wordAnswer.put("word", words.get(i));
            wordAnswer.put("rating", wordsUserRating.get(i));
            wordAnswer.put("gps_zone", GameplayStats.getGPSZone());

            wordJsonRatings.put(wordAnswer);
        }
        String wordRatingsString = wordJsonRatings.toString();

        CalculateScore calculateScore = new CalculateScore(getContext());
        calculateScore.CalculateScore(wordRatingsString);

        sendVolley(wordRatingsString);
    }

    private void sendVolley(final String wordRatingsString) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = MainActivity.getIP() + "updatewordlist.php";

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GeoOulu", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("GeoOulu", "Error: " + error.getMessage());
                Log.d("GeoOulu", "" + error.getMessage() + "," + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                Log.d("Niels", wordRatingsString);
                params.put("json", wordRatingsString);
                return params;
            }

            /** Passing some request headers * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        // Adding request to request queue
        queue.add(sr);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}