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
 * Created by niels on 14/01/16.
 */
public class AlertInfo {
    static String endTime;
    private static Context mContext;

    public static void UpdateAlert(Context context, String event) {
        try {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = MainActivity.getIP() + "addnewalertinfo.php";

            Long tsLong = System.currentTimeMillis() / 1000;
            endTime = tsLong.toString();

            String nickname = HomeScreen.getNickname();
            int gamified = GameplayStats.getGamified();

            final JSONObject alert = new JSONObject();
            alert.put("nickname", nickname);
            alert.put("gamified", gamified);
            alert.put("event", event);
            alert.put("timestamp", endTime);

            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("NielsAlert", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("GameOfWords", "Error: " + error.getMessage());
                    Log.d("NielsAlert", "" + error.getMessage() + "," + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("alert", String.valueOf(alert));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
