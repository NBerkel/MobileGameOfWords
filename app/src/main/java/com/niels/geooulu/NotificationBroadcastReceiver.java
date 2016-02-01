package com.niels.geooulu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by niels on 15/01/16.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("user_dismissed_time")) {
            AlertInfo.UpdateAlert(context, "user_dismissed_time");
            GameplayStats.alertCanceled = true;
        } else if (action.equals("user_dismissed_gps")) {
            AlertInfo.UpdateAlert(context, "user_dismissed_gps");
            GameplayStats.alertCanceled = true;
        }
    }
}
