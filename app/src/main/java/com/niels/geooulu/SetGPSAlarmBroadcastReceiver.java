package com.niels.geooulu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by niels on 25/01/16.
 */
public class SetGPSAlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        GameplayStats.alertCanceled = false;
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("ID_KEY", "geoNotification");

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

        String location = intent.getStringExtra("location"); //if it's a string you stored.

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setVibrate(new long[]{0, 1400})
                .setContentTitle(location + ".")
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent)
                .setDeleteIntent(getDeleteIntent(context));

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager.
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        if (MainActivity.isActivityRunning != true) {
            AlertInfo.UpdateAlert(context, "notified_gps");
            mNotificationManager.notify(0, builder.build());

            // Dismiss notification after a set amount of time.
            Handler h = new Handler();
            long delayInMilliseconds = Constants.NOTIFICATION_DISMISS_TIME;
            h.postDelayed(new Runnable() {
                public void run() {
                    AlertInfo.UpdateAlert(context, "time_gps_dismissed");
                    GameplayStats.alertCanceled = true;
                    mNotificationManager.cancelAll();
                }
            }, delayInMilliseconds);
        }
    }

    private PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("user_dismissed_gps");
        GameplayStats.alertCanceled = true;
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
