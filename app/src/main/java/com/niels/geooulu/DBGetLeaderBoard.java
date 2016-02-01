package com.niels.geooulu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by niels on 27/11/15.
 */
public class DBGetLeaderBoard {

    public Activity activity;
    ArrayList nicknames = new ArrayList();
    ArrayList<highScoreEntry> leaderBoard;

    public DBGetLeaderBoard(Activity _activity) {
        this.activity = _activity;
    }

    public ArrayList getNicknames() {
        return nicknames;
    }

    private void setNicknames(String response) {
        try {
            JSONArray strJson = new JSONArray(response);
            for (int i = 0; i < strJson.length(); i++) {
                JSONObject object = strJson.getJSONObject(i);

                JSONObject nickname = new JSONObject();
                nickname.put("nicknames", object.getString("nickname"));
                nicknames.add(nickname);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLeaderboard(Context applicationContext) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(applicationContext);
        String url = MainActivity.getIP() + "leaderboard.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setNicknames(response);
                        setLeaderboard(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GeoOulu onErrorResponse", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setLeaderboard(String response) {
        leaderBoard = new ArrayList<>();

        try {
            JSONArray strJson = new JSONArray(response);
            for (int i = 0; i < 5; i++) {
                JSONObject object = strJson.getJSONObject(i);

                highScoreEntry newEntry = new highScoreEntry();
                newEntry.nickname = object.getString("nickname");
                newEntry.score = object.getInt("score");

                leaderBoard.add(newEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(leaderBoard);

        if (BuildConfig.FLAVOR.equals("gamified")) {
            setLeaderboardText();
        }
    }

    void setLeaderboardText() {
        LinearLayout leaderboardUsersLayout = (LinearLayout) activity.findViewById(R.id.leaderboardUsersLayout);
        LinearLayout leaderboardScoresLayout = (LinearLayout) activity.findViewById(R.id.leaderboardScoresLayout);

        for (int i = 0; i < leaderBoard.size(); i++) {
            String nickname = leaderBoard.get(i).nickname;
            String score = String.valueOf(leaderBoard.get(i).score);

            RelativeLayout.LayoutParams userScoreLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            TextView userNickname = new TextView(activity);
            userScoreLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            userNickname.setText(nickname);

            if (leaderboardUsersLayout != null) {
                leaderboardUsersLayout.addView(userNickname, userScoreLayoutParams);
            }

            TextView userScore = new TextView(activity);
            userScoreLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            userScore.setText(score);
            if (leaderboardUsersLayout != null) {
                leaderboardScoresLayout.addView(userScore, userScoreLayoutParams);
            }
        }
    }

    class highScoreEntry implements Comparable<highScoreEntry> {
        String nickname;
        int score;

        @Override
        public int compareTo(highScoreEntry highScoreEntry) {
            return this.score > highScoreEntry.score ? -1 : 1;
        }
    }
}
