package com.niels.mobilegameofwords;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link inputLocRelevantWord.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class inputLocRelevantWord extends Fragment {
    View view;
    EditText locRelevantEditText;
    private OnFragmentInteractionListener mListener;

    public inputLocRelevantWord() {
        // Required empty public constructor
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_input_loc_relevant_word, container, false);

        locRelevantEditText = (EditText) view.findViewById(R.id.locRelevantEditText);

        Button continueToSlidersBtn = (Button) view.findViewById(R.id.continueToSlidersBtn);
        continueToSlidersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locRelevantWord = locRelevantEditText.getText().toString();
                ((GameOfWords) getActivity().getApplication()).setLocRelevantWord(locRelevantWord);

                if (locRelevantEditText.length() != 0) {
                    try {
                        GameplayStats gameplayStats = new GameplayStats(getContext());
                        gameplayStats.setWord(locRelevantWord);
                        sendNewWord(locRelevantWord);
                    } catch (Exception e) {}


                    closeKeyboard(getActivity(), locRelevantEditText.getWindowToken());

                    //TODO: replace with inputLocRelevantWord()
                    Fragment fragment = new rateWordsSlider();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                } else {
                    locRelevantEditText.setError("A location relevant word must be provided.");
                }
            }
        });

        return view;
    }

    private void sendNewWord(final String locRelevantWord) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = MainActivity.getIP() + "addnewword.php";

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GameOfWords SendNewWord", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("GameOfWords", "Error: " + error.getMessage());
                Log.d("GameOfWords SendNewWord", "" + error.getMessage() + "," + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("dataString", locRelevantWord);
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
