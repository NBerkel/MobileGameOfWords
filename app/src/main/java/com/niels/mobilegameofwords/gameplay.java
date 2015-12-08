package com.niels.mobilegameofwords;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    Button finishBtn;
    ImageView leftCircleImageView;
    ImageView rightCircleImageView;
    List<String> words;
    List<String> wordsUserRating;
    Animations anim = new Animations();
    boolean answerProvided = false;
    int currentWord = 0;
    Animation fromAtoB;
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(answerProvided == false) {
                //add "none" to wordlist (instead of the usual relevant/irrelevant vote)
                wordsUserRating.add("none");
            }
            currentWord++;
            Log.d("Niels", "animation end");
            nextWord();
        }
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_gameplay, container, false);
        currentGameWord = (TextView) view.findViewById(R.id.currentGameWord);
        wordProgress = (TextView) view.findViewById(R.id.wordProgress);
        finishBtn = (Button) view.findViewById(R.id.finishBtn);
        leftCircleImageView = (ImageView) view.findViewById(R.id.leftCircleImageView);
        rightCircleImageView = (ImageView) view.findViewById(R.id.rightCircleImageView);

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

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGame();
            }
        });

        //TODO randomly determine position of irrelevant / relevant button?

        getGameWords();

        return view;
    }

    private void getGameWords() {
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
                Log.d("GameOfWords", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setGameWords(String response) {
        words = new ArrayList<>();
        wordsUserRating = new ArrayList<>();

        try {
            JSONArray strJson = new JSONArray(response);
            for (int i = 0; i < strJson.length(); i++) {
                JSONObject object = strJson.getJSONObject(i);

                JSONObject word = new JSONObject();
                word.put("word", object.getString("word"));
                word.put("number_of_times_voted_relevant", object.getString("number_of_times_voted_relevant"));
                word.put("number_of_times_voted_irrelevant", object.getString("number_of_times_voted_irrelevant"));
                words.add(object.getString("word"));
            }
            startGame();
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO block person from starting game?
        }
    }

    private void irrelevantBtnPressed() {
        answerProvided = true;
        wordsUserRating.add("irrelevant");
        anim.scaleView(leftCircleImageView, 1f, 1.2f, 400);
    }

    private void relevantBtnPressed() {
        answerProvided = true;
        wordsUserRating.add("relevant");
        anim.scaleView(rightCircleImageView, 1f, 1.2f, 400);
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
            moveText();
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

        CalculateScore scorer = new CalculateScore(this.getContext());
        int score = scorer.getAchievedScore();
        Log.d("NIELS", String.valueOf(score));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    JSONArray wordJsonRatings= new JSONArray();

    private void transferWordRatings() throws JSONException {
        for (int i = 0; i < words.size(); i++) {
            JSONObject wordAnswer = new JSONObject();

            wordAnswer.put("word",words.get(i));
            wordAnswer.put("rating",wordsUserRating.get(i));

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
                Log.d("GameOfWords", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("GameOfWords", "Error: " + error.getMessage());
                Log.d("GameOfWords", "" + error.getMessage() + "," + error.toString());
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
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Adding request to request queue
        queue.add(sr);
    }

    private void moveText() {
        float to_x = currentGameWord.getX();
        float to_y = currentGameWord.getY() - 900;

        float x_from = currentGameWord.getX();
        float y_from = currentGameWord.getY();

        Animation moveText = anim.fromAtoB(0, 0, to_x - x_from, to_y - y_from, animationListener, 5000);

        currentGameWord.startAnimation(moveText);
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
        public void onFragmentInteraction(Uri uri);
    }

    public class Animations {
        public Animation fromAtoB(float fromX, float fromY, float toX, float toY, Animation.AnimationListener l, int speed) {
            fromAtoB = new TranslateAnimation(fromX, toX, fromY, toY);
            fromAtoB.setDuration(speed);
            fromAtoB.setInterpolator(new DecelerateInterpolator());
            if (l != null)
                fromAtoB.setAnimationListener(l);
            return fromAtoB;
        }

        public void scaleView(final View v, final float startScale, final float endScale, final int speed) {
            final Animation anim = new ScaleAnimation(
                    startScale, endScale, // Start and end values for the X axis scaling
                    startScale, endScale, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(speed);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Reverse animation
                    Animation reverseAnim = new ScaleAnimation(
                            endScale, startScale, // Start and end values for the X axis scaling
                            endScale, startScale, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                    reverseAnim.setFillAfter(true); // Needed to keep the result of the animation
                    reverseAnim.setInterpolator(new DecelerateInterpolator());
                    reverseAnim.setDuration(speed / 2);
                    v.startAnimation(reverseAnim);

                    // Stop current word animation and go to next word.
                    currentGameWord.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            v.startAnimation(anim);
        }
    }
}