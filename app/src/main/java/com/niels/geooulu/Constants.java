package com.niels.geooulu;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by niels on 08/12/15.
 */
public class Constants {
    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = -1; // No expiration
    public static final int GEOFENCES_LOITERING_DELAY = 3000; // 3 seconds.
    public static final float GEOFENCE_RADIUS_IN_METERS = 75;
    public static final int NOTIFICATION_TIMEOUT = 7200000; // 120 minutes.
    //public static final int NOTIFICATION_TIMEOUT = 18000; // 3 minutes.
    public static final int NOTIFICATION_DISMISS_TIME = 900000; // 15 minutes.
    //public static final int NOTIFICATION_DISMISS_TIME = 60000; // 1 minute.

    /**
     * Map for storing information about Oulu landmarks.
     */
    public static final HashMap<String, LatLng> OULU_LANDMARKS = new HashMap<String, LatLng>();

    static {
        OULU_LANDMARKS.put("University", new LatLng(65.059294, 25.467327));
        OULU_LANDMARKS.put("Downtown", new LatLng(65.011782, 25.470193));
        OULU_LANDMARKS.put("Railway station", new LatLng(65.010543, 25.483737));
        OULU_LANDMARKS.put("Ainola park", new LatLng(65.017738, 25.475968));
        OULU_LANDMARKS.put("Kiikeli", new LatLng(65.013272, 25.459445));
        OULU_LANDMARKS.put("Market square", new LatLng(65.013637, 25.464436));
        OULU_LANDMARKS.put("Library", new LatLng(65.015293, 25.462651));
    }

    private Constants() {
    }
}