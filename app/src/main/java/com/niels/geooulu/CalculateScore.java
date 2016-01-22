package com.niels.geooulu;


import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;


/**
 * Created by niels on 01/12/15.
 */
public class CalculateScore {

    private int achievedScore;
    private Context mContext;

    public CalculateScore(Context context) {
            mContext = context;
    }

    public void CalculateScore(String userAnswers) {
        sendVolley(userAnswers);
    }

    private void sendVolley(final String userAnswers) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = MainActivity.getIP() + "calculatescore.php";

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GeoOulu", response);

                Intent setPoints = new Intent("ACTION_GAME_POINTS");
                setPoints.putExtra("score", Integer.parseInt(response));
                mContext.sendBroadcast(setPoints);

                achievedScore = Integer.parseInt(response);

                UpdateScore updateScore = new UpdateScore(mContext);
                try {
                    updateScore.UpdateScoreDB(achievedScore);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                params.put("json_words", userAnswers);
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
