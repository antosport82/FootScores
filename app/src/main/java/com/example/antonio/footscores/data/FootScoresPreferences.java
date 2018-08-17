package com.example.antonio.footscores.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.antonio.footscores.R;

public class FootScoresPreferences {

    public static long getEllapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis =
                FootScoresPreferences.getLastNotificationTimeInMillis(context);
        return System.currentTimeMillis() - lastNotificationTimeMillis;
    }

    private static long getLastNotificationTimeInMillis(Context context) {
        // Key for accessing the time at which a notification was displayed
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(lastNotificationKey, 0);
    }

    public static boolean areNotificationsActive(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sendNotificationKey = context.getString(R.string.pref_activate_notification);
        return sharedPreferences.getBoolean(sendNotificationKey, false);
    }
    public static void activateNotification(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String sendNotificationKey = context.getString(R.string.pref_activate_notification);
        editor.putBoolean(sendNotificationKey, true);
        editor.apply();
    }

    /**
     * Saves the time that a notification is shown. This will be used to get the ellapsed time
     * since a notification was shown.
     *
     * @param context            Used to access SharedPreferences
     * @param timeOfNotification Time of last notification to save (in UNIX time)
     */
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }
}