package com.example.antonio.footscores.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class ScoresSyncIntentService extends IntentService {

    public ScoresSyncIntentService() {
        super("ScoresSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ScoresSyncTask.syncData(this);
    }
}