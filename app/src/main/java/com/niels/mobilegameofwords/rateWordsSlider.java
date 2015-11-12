package com.niels.mobilegameofwords;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    LinearLayout sliderLayoutHolder;

    private OnFragmentInteractionListener mListener;

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

    public rateWordsSlider() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rate_words_slider, container, false);

        sliderLayoutHolder = (LinearLayout) view.findViewById(R.id.sliderLayoutHolder);

        JSONArray sliderWords = MainActivity.sliderWords;
        for (int i = 0; i < sliderWords.length(); i++) {
            try {
                addSlider(sliderWords.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Button startGameBtn = new Button(getActivity());
        startGameBtn.setText("Start Game");
        LinearLayout.LayoutParams buttonLayoutParams;
        buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        buttonLayoutParams.setMargins(0,34,0,0);
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

        for (int i = 0; i < sliderLayoutHolder.getChildCount(); i++) {
            View v = sliderLayoutHolder.getChildAt(i);
            if (v instanceof SeekBar) {
                int value = ((SeekBar) v).getProgress();
                JSONObject sliderAnswer = new JSONObject();
                sliderAnswer.put("Question", 2);
                sliderAnswer.put("Location", 2);
                sliderAnswer.put("Criteria", 2);
                sliderAnswer.put("Slider", value);

                Log.d("Niels", String.valueOf(value));

                sliderAnswers.put(sliderAnswer);
            }
            else {}
        }
        String sliderAnswersString = sliderAnswers.toString();

        sendData sendData = new sendData(sliderAnswersString);
        sendData.execute();
    }

    public class sendData extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        String sliderAnswersString;

        public sendData(String s) {
            sliderAnswersString = s;
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL("http://dss.simohosio.com/api/getcriteria.php?question_id=7");
                urlConnection = (HttpURLConnection) url.openConnection();


            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            //return result.toString();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //getJSONInfo(result);
        }
    }

    private void startGame() {
        //TODO: replace with inputLocRelevantWord()
        Fragment fragment = new gameplay();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addSlider(JSONObject jsonObj) throws JSONException {
        String criterion_body = jsonObj.getString("criterion_body");

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
        seekbar.setProgress(50); //TODO implement "no-change" detected?

        sliderLayoutHolder.addView(seekbar);

        RelativeLayout textViewHolder = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams textVHLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sliderLayoutHolder.addView(textViewHolder, textVHLayoutParams);

        TextView irrelevant = new TextView(getActivity());
        RelativeLayout.LayoutParams irrelevantLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        irrelevantLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        irrelevant.setText("Highly irrelevant");
        irrelevant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textViewHolder.addView(irrelevant, irrelevantLayoutParams);

        TextView relevant = new TextView(getActivity());
        RelativeLayout.LayoutParams relevantLabelLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relevantLabelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relevant.setText("Highly relevant");
        relevant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textViewHolder.addView(relevant, relevantLabelLayoutParams);
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
        public void onFragmentInteraction(Uri uri);
    }

}
