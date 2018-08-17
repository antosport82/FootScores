package com.example.antonio.footscores.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.antonio.footscores.R;
import com.example.antonio.footscores.data.FootScoresPreferences;
import com.example.antonio.footscores.ui.MainActivity;

public class NotificationsUtils {

    public static void notifyUser(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, Constants.NOTIFICATION_CHANNEL)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_sync_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.notification_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(Constants.NOTIFICATION_CHANNEL_ID, notificationBuilder.build());
        FootScoresPreferences.saveLastNotificationTime(context, System.currentTimeMillis());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivity = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, Constants.PENDING_INTENT_NOTIFICATION, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}