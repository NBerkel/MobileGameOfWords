package com.niels.mobilegameofwords;

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
    public static final float GEOFENCE_RADIUS_IN_METERS = 70;
    public static final int NOTIFICATION_TIMEOUT = 3600000; // 60 minutes. TODO change to something more sensible.
    //public static final int NOTIFICATION_TIMEOUT = 3000; // test. TODO change to something more sensible.

    public static final int NOTIFICATION_DISMISS_TIME = 60000; //1 minute. TODO change to something more sensible.
    /**
     * Map for storing information about Oulu landmarks.
     */
    public static final HashMap<String, LatLng> OULU_LANDMARKS = new HashMap<String, LatLng>();

    static {
        OULU_LANDMARKS.put("University", new LatLng(65.059294, 25.467327));

        OULU_LANDMARKS.put("University office", new LatLng(65.0576855, 25.4685685));

        OULU_LANDMARKS.put("Niels", new LatLng(65.059182, 25.473887));

        OULU_LANDMARKS.put("Babel", new LatLng(65.059917, 25.479695));

        OULU_LANDMARKS.put("Rotuaari", new LatLng(65.012336, 25.470941));
    }

    private Constants() {
    }
}
