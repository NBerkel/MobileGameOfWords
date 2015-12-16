package com.niels.mobilegameofwords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link finishGame.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link finishGame#newInstance} factory method to
 * create an instance of this fragment.
 */
public class finishGame extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static TextView pointsTextView;
    static CalculateScore scorer;
    Button backMainScreenBtn;
    Button playAgainBtn;
    View view;
    private OnFragmentInteractionListener mListener;

    public finishGame() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment finishGame.
     */
    // TODO: Rename and change types and number of parameters
    public static finishGame newInstance(String param1, String param2) {
        finishGame fragment = new finishGame();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scorer = new CalculateScore(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_finish_game, container, false);

        pointsTextView = (TextView) view.findViewById(R.id.pointsTextView);

        backMainScreenBtn = (Button) view.findViewById(R.id.backMainScreenButton);
        playAgainBtn = (Button) view.findViewById(R.id.playAgainButton);


        backMainScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Niels", "backtomain launch");

                Fragment fragment = new HomeScreen();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
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

    public static class PointsListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int score = intent.getIntExtra("score", 0);
            if (pointsTextView != null) pointsTextView.setText(score + " points!");

            // Submit GameplayStats
            SendGameplayStats sendGameplayStats = new SendGameplayStats(context);
            try {
                sendGameplayStats.UpdateStatsDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
