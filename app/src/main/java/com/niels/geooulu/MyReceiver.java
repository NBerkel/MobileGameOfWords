package com.niels.geooulu;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by niels on 13/02/16.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("NielsAlert", "called receiver method");
        try {
            if (GameplayStats.alertCanceled == false) {
                AlertInfo.UpdateAlert(context, "dismissed_time");
            }
            mNotificationManager.cancelAll();
            GameplayStats.alertCanceled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
