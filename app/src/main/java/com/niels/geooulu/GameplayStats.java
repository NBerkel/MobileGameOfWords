package com.niels.geooulu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by niels on 08/12/15.
 */
public class GameplayStats {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public static int entry;
    static Location gps_location;
    static float gps_accuracy;
    static String gps_zone;
    static LocationListener locationListener;
    static String word;
    static int gamified;
    static String startTime;
    static String endTime;
    private static Context context;
    private static int score;
    private static String nickname;

    public GameplayStats(Context _context) {
        context = _context;
    }

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String _nickname) {
        nickname = _nickname;
    }

    public static String getWord() {
        return word;
    }

    public void setWord(String locRelevantWord) {
        word = locRelevantWord;
    }

    public static int getGamified() {
        return gamified;
    }

    public void setGamified(int _gamified) {
        gamified = _gamified;
    }

    public static Location getGPSLocation() {
        return gps_location;
    }

    public static float getGPSAccuracy() {
        return gps_accuracy;
    }

    public static String getGPSZone() {
        return gps_zone;
    }

    public void setGPSZone(String gpsZone) {
        Log.d("Niels", "GPS zone set to " + gpsZone);
        gps_zone = gpsZone;
    }

    public static void setStartTime() {
        Long tsLong = System.currentTimeMillis() / 1000;
        startTime = tsLong.toString();
    }

    public static String getStartTime() {
        return startTime;
    }

    public static void setEndTime() {
        Long tsLong = System.currentTimeMillis() / 1000;
        endTime = tsLong.toString();
    }

    public static String getEndTime() {
        return endTime;
    }

    public static int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        GameplayStats.entry = entry;
    }

    public static int getScore() {
        return score;
    }

    public void setScore(int achievedScore) {
        score = achievedScore;
    }


    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate) {
            return true;
        }
        return false;
    }

    // http://developer.android.com/guide/topics/location/strategies.html
    public void startGPSSensor(LocationManager lm, LocationListener ll) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
    }

    public void stopGPSSensor(LocationManager lm, LocationListener ll) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm.removeUpdates(ll);
        }
        ll = null;
        lm = null;
        Log.d("Niels", "Stop GPS called");
    }

    public static class GeoUpdateHandler implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Check if location is better estimate.
            if (isBetterLocation(location, gps_location)) {
                gps_location = location;
                gps_accuracy = location.getAccuracy();
                Log.d("NielsGPS", "location updated; " + location);
            } else {
                // Do nothing.
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


}
