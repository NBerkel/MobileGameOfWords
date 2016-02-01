package com.niels.geooulu;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niels on 08/12/15.
 */
public class GeoFenceTransitionIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";
    int previousTransition;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeoFenceTransitionIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeoFenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        Log.d("Niels", "intent detected");
        GameplayStats gameplayStats = new GameplayStats(this);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && geofenceTransition != previousTransition) {
            previousTransition = geofenceTransition;

            Log.d("Niels", "Entered location");

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details (only if application is not in foreground)
            if (MainActivity.isActivityRunning != true) {
                sendNotification("Entered " + geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
                GameplayStats.alertCanceled = false;
                Log.d("Niels", geofenceTransitionDetails);
            }
            // Set zone
            gameplayStats.setGPSZone(geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // User has left the geofence
            Log.d("Niels", "exit geofence");
            // dismiss notification
            if (GameplayStats.alertCanceled == false) {
                dismissNotification();
            }
            // remove set zone
            gameplayStats.setGPSZone("Other");
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
            gameplayStats.setGPSZone("Other");
        }
    }

    private void dismissNotification() {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Dismiss the notification
        mNotificationManager.cancel(0);

        // Only store in database if notification has not already been cancelled.
        if (GameplayStats.alertCanceled == false) {
            AlertInfo.UpdateAlert(getApplicationContext(), "dismissed_gps");
            GameplayStats.alertCanceled = true;
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public void sendNotification(String notificationDetails) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent NotifyIntent = new Intent(this, SetGPSAlarmBroadcastReceiver.class); // this is the actual notification
        NotifyIntent.putExtra("location", notificationDetails);
        PendingIntent NotifySender = PendingIntent.getBroadcast(this, 0, NotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), NotifySender); // fire immediately
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}