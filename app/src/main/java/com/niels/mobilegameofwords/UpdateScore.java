package com.niels.mobilegameofwords;

import android.content.Context;
import android.util.Log;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niels on 04/12/15.
 */

public class UpdateScore {

    private Context mContext;

    public UpdateScore(Context context) {
        mContext = context;
    }

    public void UpdateScoreDB(int achievedScore) throws JSONException {
        // Send to GameplayStats
        GameplayStats gameplayStats = new GameplayStats(mContext);
        gameplayStats.setScore(achievedScore);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = MainActivity.getIP() + "updatescore.php";

        String nickname = HomeScreen.getNickname();
        int gamified = GameplayStats.getGamified();

        final JSONObject userScore = new JSONObject();
        userScore.put("nickname", nickname);
        userScore.put("score", achievedScore);
        userScore.put("gamified", gamified);

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
                params.put("userScore", String.valueOf(userScore));
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
}
