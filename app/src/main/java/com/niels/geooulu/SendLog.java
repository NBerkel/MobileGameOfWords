package com.niels.geooulu;

import android.content.Context;
import android.location.Location;
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
 * Created by niels on 18/01/16.
 */
public class SendLog {
    public void UpdateLogDB(String word, Boolean vote, Context context) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        String nickname = HomeScreen.getNickname();
        int gamified = GameplayStats.getGamified();

        Location gps_location = GameplayStats.getGPSLocation();
        String gps_zone = GameplayStats.getGPSZone();
        float gps_accuracy = GameplayStats.getGPSAccuracy();
        if (gps_location == null) {
            gps_location = new Location("");
            gps_location.setLatitude(0);
            gps_location.setLongitude(0);
        }
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        String url = MainActivity.getIP() + "updatelog.php";

        final JSONObject logJSON = new JSONObject();
        logJSON.put("nickname", nickname);
        logJSON.put("gamified", gamified);
        logJSON.put("word", word);
        logJSON.put("vote", vote);
        String gps_location_string = gps_location.getLatitude() + "," + gps_location.getLongitude();
        logJSON.put("gps_zone", gps_zone);
        logJSON.put("gps_location", gps_location_string);
        logJSON.put("gps_accuracy", gps_accuracy);
        logJSON.put("timestamp", time);

        Log.d("NielsGPS", gps_zone);

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
                params.put("logJSON", String.valueOf(logJSON));
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
}
