package com.niels.mobilegameofwords;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static JSONArray sliderWords = new JSONArray();
    String currentLocation;
    TextView currentLocationTextView;
    Button playGameBtn;
    static String ip = "http://gow.ddns.net/";

    public static String getIP() {
        return ip;
    }


    public static JSONArray getsliderWords() {
        return sliderWords;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        currentLocationTextView = (TextView) findViewById(R.id.currentLocationTextView);
        playGameBtn = (Button) findViewById(R.id.playGameBtn);
        playGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout intro_text = (LinearLayout) findViewById(R.id.intro_text);
                intro_text.setVisibility(View.GONE);

                //TODO: replace with inputLocRelevantWord()
                Fragment fragment = new inputLocRelevantWord();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        getCriteria();
        getLeaderboard();
    }

    private void getLeaderboard() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ip + "leaderboard.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setLeaderboardText(response);
                        playGameBtn.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                playGameBtn.setEnabled(false);
                Log.d("GameOfWords", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setLeaderboardText(String leaderboardResponse) {
        try {
            JSONArray strJson = new JSONArray(leaderboardResponse);

            LinearLayout leaderboardUsersLayout = (LinearLayout) findViewById(R.id.leaderboardUsersLayout);
            LinearLayout leaderboardScoresLayout = (LinearLayout) findViewById(R.id.leaderboardScoresLayout);

            for (int i = 0; i < strJson.length(); i++) {
                JSONObject object = strJson.getJSONObject(i);

                JSONObject leaderboard_user = new JSONObject();
                leaderboard_user.put("nickname", object.getString("nickname"));
                leaderboard_user.put("score", object.getString("score"));

                RelativeLayout.LayoutParams userScoreLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextView userNickname = new TextView(this);
                userScoreLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                userNickname.setText(object.getString("nickname"));
                leaderboardUsersLayout.addView(userNickname, userScoreLayoutParams);

                TextView userScore = new TextView(this);
                userScoreLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                userScore.setText(object.getString("score"));
                leaderboardScoresLayout.addView(userScore,userScoreLayoutParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO block person from starting game?
        }
    }

    private void getCriteria() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://dss.simohosio.com/api/getcriteria.php?question_id=8";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getJSONInfo(response);
                        playGameBtn.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                playGameBtn.setEnabled(false);
                Log.d("GameOfWords", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean getCurrentLocation() {
        boolean a = true;
        if (a) {
            // Beacon detection here.
            currentLocation = "UniversityA";
            currentLocationTextView.setText(currentLocation);
        } else {
            currentLocation = "";
            currentLocationTextView.setText("Current location not applicable.");
            a = false;
        }
        return a;
    }

    private void getJSONInfo(String currentLocation) {
        try {
            JSONArray strJson = new JSONArray(currentLocation);
            for (int i = 0; i < strJson.length(); i++) {
                JSONObject object = strJson.getJSONObject(i);

                JSONObject word = new JSONObject();
                word.put("criterion_id", object.getString("criterion_id"));
                word.put("criterion_body", object.getString("criterion_body"));
                sliderWords.put(word);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO block person from starting game?
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
