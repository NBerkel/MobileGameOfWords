package com.niels.geooulu;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

public class SetAlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        GameplayStats.alertCanceled = false;
        Log.d("Niels", "SetAlarmBroadcastReceiver");

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("ID_KEY", "notification");

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setVibrate(new long[]{0, 1400})
                .setContentTitle("Start GeoOulu?")
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent)
                .setDeleteIntent(getDeleteIntent(context));

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager.
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification - only if application is not open and time is not within restricted area (between 22:00 & 08:00)
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (MainActivity.isActivityRunning != true && hour <= 21 && hour >= 8) {
            AlertInfo.UpdateAlert(context, "notified_time");
            GameplayStats.alertCanceled = false;
            mNotificationManager.notify(0, builder.build());

            long delayInMilliseconds = Constants.NOTIFICATION_DISMISS_TIME;

            // Dismiss notification after a set amount of time.
            Intent myIntent = new Intent(context, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + delayInMilliseconds, pendingIntent);

//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                public void run() {
//                    AlertInfo.UpdateAlert(context, "dismissed_time");
//                    mNotificationManager.cancelAll();
//                    GameplayStats.alertCanceled = true;
//                }
//            }, delayInMilliseconds);
        }
    }

    private PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("user_dismissed_time");
        GameplayStats.alertCanceled = true;
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
