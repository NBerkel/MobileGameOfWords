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

        /*Intent intent = new Intent(this, MainActivity.class);
        String id = "AlarmNotification";
        intent.putExtra("ID_KEY", id);
        PendingIntent sender = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/

        //String id = "AlarmNotification";
        //intent.putExtra("ID_KEY", id);
        //PendingIntent sender = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent Notifyintent = new Intent(this, SetAlarmBroadcastReceiver.class);
        PendingIntent Notifysender = PendingIntent.getBroadcast(this, 0, Notifyintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 3600, Notifysender);


        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Constants.NOTIFICATION_TIMEOUT, Constants.NOTIFICATION_TIMEOUT, pendingIntent);


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

        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);*/
        /*AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);*/
        /*alarmManager2.set(AlarmManager.ELAPSED_REALTIME, Constants.NOTIFICATION_TIMEOUT, pendingIntent);*/
    }

    private void sendAlert() {
        Log.d("Niels", "sendAlert called");
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setVibrate(new long[]{0, 1000})
                .setContentTitle("Play Game of Words?")
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager.
        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification - only if application is not open.
        if (MainActivity.isActivityRunning != true) {
            mNotificationManager.notify(0, builder.build());

            // Dismiss notification after a set amount of time.
            Handler h = new Handler();
            long delayInMilliseconds = Constants.NOTIFICATION_DISMISS_TIME;
            h.postDelayed(new Runnable() {
                public void run() {
                    mNotificationManager.cancel(0);
                    Log.d("Niels", "Notification cancelled");
                }
            }, delayInMilliseconds);
        }
    }
}