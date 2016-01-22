package com.niels.geooulu;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends Fragment {
    public static boolean enablePlayBtn;
    public static String nickname;
    public static Boolean userAlreadyExists = false;
    View view;
    TextView welcomeTextView;
    Button playGameBtn;
    EditText usernameEditText;
    String fileName = "nickname";
    DBGetLeaderBoard dbGetLeaderboard;

    public HomeScreen() {
        // Required empty public constructor
    }

    public static String getNickname() {
        return nickname;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                        FileOutputStream outputStream;
                        try {
                            // Create file with nickname
                            Log.d("Niels", String.valueOf(getContext().getApplicationContext()));
                            Log.d("Niels", String.valueOf(getActivity().getApplicationContext()));
                            //outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            outputStream = getActivity().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            outputStream.write(content.getBytes());
                            outputStream.close();
                            // Store nickname in DB
                            insertNicknameDB(content);
                            // Store nickname in variable
                            nickname = content;
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

    private boolean checkUsernameInput() {
        String userNameText = usernameEditText.getText().toString();
        if (userNameText.matches("")) {
            //Empty, no username provided.
            Toast.makeText(getContext(), "You did not enter a username", Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList nicknames = dbGetLeaderboard.nicknames;
        for (int i = 0; i < nicknames.size(); i++) {
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

    private void insertNicknameDB(String nickname) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = MainActivity.getIP() + "addnewnickname.php";

        int gamified = GameplayStats.getGamified();
        String android_os = Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT;
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String phone = Build.MANUFACTURER + " " + Build.MODEL;

        DisplayMetrics metrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        String screenSize = widthPixels + "-" + heightPixels;

        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();

        final JSONObject nicknameObj = new JSONObject();
        nicknameObj.put("nickname", nickname);
        nicknameObj.put("gamified", gamified);
        nicknameObj.put("android_os", android_os);
        nicknameObj.put("device_id", device_id);
        nicknameObj.put("phone", phone);
        nicknameObj.put("screen_size", screenSize);
        nicknameObj.put("timestamp", timestamp);

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
                params.put("nickname", String.valueOf(nicknameObj));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void startGame() {
        /*
        Location recordedLocation = GameplayStats.getGPSLocation();
        if (recordedLocation == null) {
            Log.d("Niels", "location is null");
        }
        */
        GameplayStats.setStartTime();

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
