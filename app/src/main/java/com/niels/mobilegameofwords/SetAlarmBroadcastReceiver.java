package com.niels.mobilegameofwords;

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
import android.util.Log;

import java.util.Calendar;

public class SetAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
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
                .setContentTitle("Play Game of Words?")
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent)
                .setDeleteIntent(getDeleteIntent(context));

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager.
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification - only if application is not open and time is not within restricted area (between 22:00 & 08:00)
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (MainActivity.isActivityRunning != true && hour <= 21 && hour >= 8 ) {
            AlertInfo.UpdateAlert(context, "notified_time");
            mNotificationManager.notify(0, builder.build());
        }

        // Dismiss notification after a set amount of time.
        Handler h = new Handler();
        long delayInMilliseconds = Constants.NOTIFICATION_DISMISS_TIME;
        h.postDelayed(new Runnable() {
            public void run() {
                AlertInfo.UpdateAlert(context, "dismissed_time");
                mNotificationManager.cancel(0);

            }
        }, delayInMilliseconds);
    }

    private PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("user_dismissed_time");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
