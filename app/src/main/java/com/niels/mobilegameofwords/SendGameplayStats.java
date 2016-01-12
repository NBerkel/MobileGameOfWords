package com.niels.mobilegameofwords;

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
 * Created by niels on 08/12/15.
 */
public class SendGameplayStats {

    private Context mContext;

    public SendGameplayStats(Context context) {
        mContext = context;
    }

    public void UpdateStatsDB() throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);

        String word = GameplayStats.getWord();
        int score = GameplayStats.getScore();
        int entry = GameplayStats.getEntry();
        int gamified = GameplayStats.getGamified();
        
        Location gps_location = GameplayStats.getGPSLocation();
        float gps_accuracy = GameplayStats.getGPSAccuracy();
        String gps_zone = GameplayStats.getGPSZone();

        if(gps_location == null) {
            gps_location = new Location("");
            gps_location.setLatitude(0);
            gps_location.setLongitude(0);
        }

        String nickname = HomeScreen.getNickname();

        String url = MainActivity.getIP() + "updategameplaystats.php";

        final JSONObject gamePlayStatsJSON = new JSONObject();
        gamePlayStatsJSON.put("nickname", nickname);
        gamePlayStatsJSON.put("word", word);
        gamePlayStatsJSON.put("score", score);
        String gps_location_string = gps_location.getLatitude() + "," + gps_location.getLongitude();
        gamePlayStatsJSON.put("gps_location", gps_location_string);
        gamePlayStatsJSON.put("gps_zone", gps_zone);
        gamePlayStatsJSON.put("gps_accuracy", gps_accuracy);
        gamePlayStatsJSON.put("entry", entry);
        gamePlayStatsJSON.put("gamified", gamified);

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
                params.put("gameplayStats", String.valueOf(gamePlayStatsJSON));
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
