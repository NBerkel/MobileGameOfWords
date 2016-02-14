package com.niels.geooulu;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by niels on 14/02/16.
 */
public class MyReceiverGPS extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("NielsAlert", "called GPS receiver method");
        try {
            if (GameplayStats.alertCanceled == false) {
                AlertInfo.UpdateAlert(context, "time_gps_dismissed");
            }
            mNotificationManager.cancelAll();
            GameplayStats.alertCanceled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
