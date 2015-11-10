package com.niels.mobilegameofwords;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    public gameplay() {
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
    TextView currentGameWord;
    Button irrelevantBtn;
    Button relevantBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_gameplay, container, false);
        currentGameWord = (TextView) view.findViewById(R.id.currentGameWord);
        irrelevantBtn = (Button) view.findViewById(R.id.irrelevantBtn);
        relevantBtn = (Button) view.findViewById(R.id.relevantBtn);

        irrelevantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { irrelevantBtnPressed(); }
        });
        relevantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { relevantBtnPressed(); }
        });
        
        startGame();
        return view;
    }

    private void irrelevantBtnPressed() {
        Log.d("Niels", "Irrelevant Button Pressed");
        currentGameWord.setVisibility(View.INVISIBLE);
    }
    private void relevantBtnPressed() {
    }


    private void startGame() {
        Log.d("Niels", "STart game?");
        currentGameWord.setText("Test????");
        moveText();
    }

    private void moveText() {
        float to_x = 0;
        float to_y = currentGameWord.getY() - 900;

        Log.d("Niels", String.valueOf(currentGameWord.getY()));
        Log.d("Niels", String.valueOf(view.getHeight()));

        float x_from = currentGameWord.getX();
        float y_from = currentGameWord.getY();
        //txtTwo.getLocationInWindow(fromLoc);
        //currentGameWord.getLocationOnScreen(toLoc);
        Animations anim = new Animations();
        Animation moveText = anim.fromAtoB(0, 0, to_x-x_from, to_y - y_from, animL, 5000);

        currentGameWord.startAnimation(moveText);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public class Animations {
        public Animation fromAtoB(float fromX, float fromY, float toX, float toY, Animation.AnimationListener l, int speed){
            Animation fromAtoB = new TranslateAnimation(fromX,toX,fromY,toY);
            fromAtoB.setDuration(speed);
            fromAtoB.setInterpolator(new DecelerateInterpolator());
            if(l != null)
                fromAtoB.setAnimationListener(l);
            return fromAtoB;
        }
    }

    Animation.AnimationListener animL = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //currentGameWord.setVisibility(View.INVISIBLE);
            currentGameWord.setText("New word");
        }
    };

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