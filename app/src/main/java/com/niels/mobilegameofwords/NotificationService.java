package com.niels.mobilegameofwords;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

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
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent NotifyIntent = new Intent(this, SetAlarmBroadcastReceiver.class); // this is the actual notification
        PendingIntent NotifySender = PendingIntent.getBroadcast(this, 0, NotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        //am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 3600, Notifysender);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, cal.getTimeInMillis(), Constants.NOTIFICATION_TIMEOUT, NotifySender);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("");
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("RESPONSE_STRING", "responseString");
        sendBroadcast(broadcastIntent);

        Intent alarmIntent = new Intent(this, SetAlarmBroadcastReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendIntent); //cancel if active already
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, Constants.NOTIFICATION_TIMEOUT, Constants.NOTIFICATION_TIMEOUT, pendIntent);
    }
}