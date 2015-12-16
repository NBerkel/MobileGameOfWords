package com.niels.mobilegameofwords;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean enablePlayBtn;
    public static String nickname;
    static Boolean userAlreadyExists = false;
    View view;
    TextView welcomeTextView;
    Button playGameBtn;
    EditText usernameEditText;
    String fileName = "nicknamem";
    DBGetLeaderBoard dbGetLeaderboard;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public HomeScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeScreen newInstance(String param1, String param2) {
        HomeScreen fragment = new HomeScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getNickname() {
        return nickname;
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
        view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        dbGetLeaderboard = new DBGetLeaderBoard(getActivity());
        dbGetLeaderboard.getLeaderboard(getContext());

        welcomeTextView = (TextView) view.findViewById(R.id.welcomeTextView);

        if (nickname != null) {
            welcomeTextView.setText("Welcome back " + nickname + "!");
            // remove username input
            LinearLayout linearLayoutUsernameInput = (LinearLayout) view.findViewById(R.id.linearLayoutUsernameInput);
            linearLayoutUsernameInput.setVisibility(View.GONE);
        }

        usernameEditText = (EditText) view.findViewById(R.id.usernameEditText);
        playGameBtn = (Button) view.findViewById(R.id.playGameBtn);

        // TODO check for enable / disable Play button. Maybe call function inside mainactivity.
        /*if(enablePlayBtn == true) {
            playGameBtn.setEnabled(true);
        } else {
            playGameBtn.setEnabled(false);
        }*/

        playGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAlreadyExists == false) {
                    if (checkUsernameInput() == true) {
                        // Write text file with username
                        String content = String.valueOf(usernameEditText.getText());

                        Log.d("Niels", "Create file");
                        FileOutputStream outputStream;
                        try {
                            outputStream = getContext().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            outputStream.write(content.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startGame();
                    }
                } else {
                    startGame();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private boolean checkUsernameInput() {
        String userNameText = usernameEditText.getText().toString();
        if (userNameText.matches("")) {
            //Empty, no username provided.
            Toast.makeText(getContext(), "You did not enter a username", Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList nicknames = dbGetLeaderboard.nicknames;
        for (int i = 0; i < nicknames.size(); i++) {
            //System.out.println(nicknames.get(i));

            String nickname = "";
            JSONObject jObj = (JSONObject) nicknames.get(i);
            try {
                nickname = jObj.getString("nicknames");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (userNameText.equals(nickname)) {
                Toast.makeText(getContext(), "Username already in use", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
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

    private void startGame() {
        LinearLayout intro_text = (LinearLayout) view.findViewById(R.id.intro_text);
        intro_text.setVisibility(View.GONE);

        Fragment fragment = new inputLocRelevantWord();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
