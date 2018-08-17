package com.example.antonio.footscores.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.antonio.footscores.data.FootScoresPreferences;
import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.models.Game;
import com.example.antonio.footscores.utilities.Constants;
import com.example.antonio.footscores.utilities.JsonUtils;
import com.example.antonio.footscores.utilities.NetworkUtils;
import com.example.antonio.footscores.utilities.NotificationsUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ScoresSyncTask {

    synchronized public static void syncData(Context context) {
        // Json and DB operations
        try {
            URL gameRequestUrl = new URL(Constants.GAMES_URL);
            // get json response in a string
            String jsonGameResponse = NetworkUtils
                    .getResponseFromHttpUrl(gameRequestUrl);
            // get data and insert it into DB
            ArrayList<Game> mGameData = JsonUtils.getGames(jsonGameResponse);
            ContentValues[] cvGames = JsonUtils.getContentValues(mGameData);
            if (cvGames != null && cvGames.length != 0) {
                ContentResolver scoresContentResolver = context.getContentResolver();
                scoresContentResolver.delete(GameContract.GameEntry.CONTENT_URI_GAMES, null, null);
                scoresContentResolver.bulkInsert(GameContract.GameEntry.CONTENT_URI_GAMES, cvGames);

                long timeSinceLastNotification = FootScoresPreferences.getEllapsedTimeSinceLastNotification(context);
                boolean sendNotifications = FootScoresPreferences.areNotificationsActive(context);
                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS && sendNotifications) {
                    NotificationsUtils.notifyUser(context);
                } else {
                    FootScoresPreferences.activateNotification(context);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}