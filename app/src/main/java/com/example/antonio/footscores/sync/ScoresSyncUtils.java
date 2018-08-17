package com.example.antonio.footscores.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.antonio.footscores.data.GameContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class ScoresSyncUtils {

    private static final int SYNC_INTERVAL_MINUTES = 120;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS;
    private static boolean sInitialized;
    private static final String SCORES_SYNC_TAG = "scores-sync";

    public static void scheduleFirebaseJobDispatcherSync(final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncScoresJob = dispatcher.newJobBuilder()
                .setService(ScoresJobService.class)
                .setTag(SCORES_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncScoresJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri scoresUri = GameContract.GameEntry.CONTENT_URI_GAMES;
                String[] projectionColumns = {GameContract.GameEntry._ID};
                /* Here, we perform the query to check to see if we have any data */
                Cursor cursor = context.getContentResolver().query(
                        scoresUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });
        checkForEmpty.start();
    }

    private static void startImmediateSync(Context context) {
        Intent intentToSyncImmediately = new Intent(context, ScoresSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}