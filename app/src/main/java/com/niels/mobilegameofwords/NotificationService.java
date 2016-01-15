package com.niels.mobilegameofwords;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by niels on 09/12/15.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Niels", "onHandleIntent.");

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent NotifyIntent = new Intent(this, SetAlarmBroadcastReceiver.class); // this is the actual notification
        PendingIntent NotifySender = PendingIntent.getBroadcast(this, 0, NotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Constants.NOTIFICATION_TIMEOUT, Constants.NOTIFICATION_TIMEOUT, NotifySender);
    }
}