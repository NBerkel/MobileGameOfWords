package com.niels.geooulu;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link rateWordsSlider.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link rateWordsSlider#newInstance} factory method to
 * create an instance of this fragment.
 */
public class rateWordsSlider extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout sliderLayoutHolder;
    View view;
    int question_id = 8;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public rateWordsSlider() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment rateWordsSlider.
     */
    // TODO: Rename and change types and number of parameters
    public static rateWordsSlider newInstance(String param1, String param2) {
        rateWordsSlider fragment = new rateWordsSlider();
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
        view = inflater.inflate(R.layout.fragment_rate_words_slider, container, false);

        // If person second time in this location - no sliders.
        if (GameplayStats.entry == 4) {
            startGame();
        }

        sliderLayoutHolder = (LinearLayout) view.findViewById(R.id.sliderLayoutHolder);

        JSONArray sliderWords = MainActivity.sliderWords;
        for (int i = 0; i < 3; i++) {
            try {
                addSlider(sliderWords.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Button startGameBtn = new Button(getActivity());
        startGameBtn.setText("Continue");
        LinearLayout.LayoutParams buttonLayoutParams;
        buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        buttonLayoutParams.setMargins(0, 34, 0, 0);
        sliderLayoutHolder.addView(startGameBtn, buttonLayoutParams);
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    transferAnswers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startGame();
            }
        });

        return view;
    }

    private void transferAnswers() throws JSONException {
        JSONArray sliderAnswers = new JSONArray();
        JSONArray sliderWords = MainActivity.sliderWords;
        int j = 0;
        for (int i = 0; i <= sliderLayoutHolder.getChildCount(); i++) {
            View v = sliderLayoutHolder.getChildAt(i);
            if (v instanceof SeekBar) {
                JSONArray sliderAnswer = new JSONArray();

                Log.d("NIELS", sliderWords.getJSONObject(j).getString("criterion_id"));

                int value = ((SeekBar) v).getProgress();

                String gps_zone = GameplayStats.getGPSZone();
                int gps_id;
                if (gps_zone == "Downtown") {
                    gps_id = 1;
                } else if (gps_zone == "Railway station") {
                    gps_id = 2;
                } else if (gps_zone == "Park") {
                    gps_id = 3;
                } else if (gps_zone == "Kiikeli") {
                    gps_id = 4;
                } else if (gps_zone == "Market square") {
                    gps_id = 5;
                } else if (gps_zone == "Library") {
                    gps_id = 6;
                } else if (gps_zone == "University office") {
                    gps_id = 6;
                } else {
                    gps_id = 7; // "Other"
                }

                sliderAnswer.put(1); // Question
                sliderAnswer.put(gps_id); // Option: location from 1 to 6
                sliderAnswer.put(sliderWords.getJSONObject(j).getString("criterion_id"));
                sliderAnswer.put(value); // Rating
                sliderAnswers.put(sliderAnswer);
                j++;
            } else {
            }
        }
        String sliderAnswersString = sliderAnswers.toString();

        sendVolley(sliderAnswersString);
    }

    private void sendVolley(final String sliderAnswersString) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // String url = "http://gow2.simohosio.com/api/postrating.php";
        String url = MainActivity.getIP() + "/api/postrating.php";
        //String url = "http://requestb.in/upk1t8up";

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GeoOulu sliders", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("GeoOulu", "Error: " + error.getMessage());
                Log.d("GeoOulu sliders", "" + error.getMessage() + "," + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new LinkedHashMap<>();
                params.put("json_ratings", sliderAnswersString);
                params.put("user_id", HomeScreen.getNickname());
                Location GPS_Location = GameplayStats.getGPSLocation();
                params.put("meta", GPS_Location.toString() + " - Zone:" + GameplayStats.getGPSZone());
                return params;
            }
        };

        // Adding request to request queue
        queue.add(sr);
    }

    private void startGame() {
        Fragment fragment = new gameplay();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addSlider(JSONObject jsonObj) throws JSONException {
        String criterion_body = jsonObj.getString("criterion_body");
        String criterion_details = jsonObj.getString("criterion_details");

        TextView criterion = new TextView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 0);

        criterion.setText(criterion_body);
        criterion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        criterion.setGravity(Gravity.CENTER_HORIZONTAL);

        sliderLayoutHolder.addView(criterion, params);

        SeekBar seekbar = new SeekBar(getActivity());
        seekbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        seekbar.setMax(100);
        seekbar.setProgress(50);

        sliderLayoutHolder.addView(seekbar);

        RelativeLayout textViewHolder = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams textVHLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sliderLayoutHolder.addView(textViewHolder, textVHLayoutParams);

        TextView details = new TextView(getActivity());
        RelativeLayout.LayoutParams irrelevantLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        irrelevantLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        details.setText(criterion_details);
        details.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textViewHolder.addView(details, irrelevantLayoutParams);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
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
