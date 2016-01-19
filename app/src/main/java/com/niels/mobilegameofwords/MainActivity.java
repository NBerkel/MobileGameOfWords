package com.niels.mobilegameofwords;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "MainActivity";
    public static JSONArray sliderWords = new JSONArray();
    public static boolean isActivityRunning;
    static String ip = "http://gow.ddns.net/";
    public GameplayStats gameplayStats;
    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;

    String fileName = "nickname";
    //private static GameplayStats.GeoUpdateHandler geoUpdateHandler;
    LocationListener ll;
    LocationManager lm;
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    public static String getIP() {
        return ip;
    }

    public static JSONArray getsliderWords() {
        return sliderWords;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //geoUpdateHandler = new GameplayStats.GeoUpdateHandler();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new GameplayStats.GeoUpdateHandler();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

        gameplayStats = new GameplayStats(getApplicationContext());
        gameplayStats.setEntry(1);

        if (BuildConfig.FLAVOR.equals("gamified")) {
            gameplayStats.setGamified(1);
        } else if (BuildConfig.FLAVOR.equals("nongamified")) {
            gameplayStats.setGamified(0);
        }

        setContentView(R.layout.activity_main);

        checkUsername();

        Fragment fragment = new HomeScreen();
        Log.d("NielsMain", "New HomeScreen created");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //gameplayStats = new GameplayStats(this);
        // Initialise with "other" zone
        gameplayStats.setGPSZone("Other");
        gameplayStats.startGPSSensor(lm, ll);

        addGeofenceList(this);

        getCriteria();
    }

    private void startAlarm() {
        Log.d("Alarm scheduler", "Alarm is being scheduled");

        Intent notificationIntent = new Intent(this, NotificationService.class);
        startService(notificationIntent);
    }

    public void checkUsername() {
        //check if username already exist, else offer possibility to enter new username
        if (fileExistance(fileName) == true) {
            Log.d("Niels", "File exist, lets read it!");
            HomeScreen.userAlreadyExists = true;
            BufferedReader input;
            File file;
            try {
                file = new File(getFilesDir(), fileName);

                input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                Log.d("Niels", "File content " + buffer.toString());

                HomeScreen.nickname = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HomeScreen.userAlreadyExists = false;
        }
    }

    public boolean fileExistance(String fname) {
        File file = null;
        file = this.getApplicationContext().getFileStreamPath(fname);
        return file.exists();
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
                        HomeScreen.enablePlayBtn = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HomeScreen.enablePlayBtn = false;
                Log.d("GameOfWords", String.valueOf(error));
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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

    public void addGeofenceList(Context context) {
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        // Get the geofences used. Geofence data is hard coded.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient(context);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     *
     * @param context
     */
    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Niels", "onStart called");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Start ESM notification
        startAlarm();
        // Stop GPS collection
        gameplayStats.stopGPSSensor(lm, ll);

        isActivityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameplayStats.startGPSSensor(lm, ll);

        if (getIntent() != null && getIntent().getExtras() != null) { //Launched from Notification
            if (getIntent().getExtras().containsKey("ID_KEY")) {
                GameplayStats gameplayStats = new GameplayStats(getApplicationContext());

                //if (getIntent().getStringExtra("ID_KEY") == "geoNotification") {
                if (getIntent().getStringExtra("ID_KEY").contains("geoNotification")) {
                    gameplayStats.setEntry(3);
                    //launched from geo notification
                    AlertInfo.UpdateAlert(getApplicationContext(), "user_opened_gps");
                    Log.d("Niels", "geo notification launch");
                } else {
                    gameplayStats.setEntry(2);
                    //launched from time notification
                    AlertInfo.UpdateAlert(getApplicationContext(), "user_opened_time");
                    Log.d("Niels", "time notification launch");
                }
                getIntent().removeExtra("ID_KEY");
            }
        } else {
            //launched from launcher
            Log.d("Niels", "application launch");
            GameplayStats gameplayStats = new GameplayStats(getApplicationContext());
            gameplayStats.setEntry(1);
        }

        isActivityRunning = true;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " + "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p/>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeoFenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFenceTransitionIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.OULU_LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this geofence.
                    .setRequestId(entry.getKey())
                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setLoiteringDelay(Constants.GEOFENCES_LOITERING_DELAY)
                            // Set the expiration duration of the geofence. This geofence gets automatically removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_HOURS)
                            // Set the transition types of interest
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            //.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            // Create the geofence.
                    .build());
        }
    }
}