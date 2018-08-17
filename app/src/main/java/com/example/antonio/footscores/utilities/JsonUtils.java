package com.example.antonio.footscores.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.antonio.footscores.data.GameContract;
import com.example.antonio.footscores.models.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class JsonUtils {
    public static ArrayList<Game> getGames(String jsonGames) throws JSONException {
        ArrayList<Game> games = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonGames);
        int jsonArrayLength = jsonArray.length();
        for (int i = 0; i < jsonArrayLength; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int gameId = jsonObject.optInt(Constants.GAME_ID);
            String league = jsonObject.optString(Constants.LEAGUE);
            String teamHome = jsonObject.optString(Constants.TEAM_HOME);
            String teamAway = jsonObject.optString(Constants.TEAM_AWAY);
            long kickoff = jsonObject.optLong(Constants.KICKOFF);
            int played = jsonObject.optInt(Constants.PLAYED);
            int scoreHome = jsonObject.optInt(Constants.SCORE_HOME);
            int scoreAway = jsonObject.optInt(Constants.SCORE_AWAY);
            String scorers = jsonObject.optString(Constants.SCORERS);
            games.add(new Game(gameId, league, teamHome, teamAway, kickoff, played, scoreHome, scoreAway, scorers));
        }
        return games;
    }

    public static ArrayList<Game> cursorToGames(Cursor cursor) {
        ArrayList<Game> games = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            int gameId = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_GAME_ID));
            String league = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_LEAGUE));
            String homeTeam = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_HOME));
            String awayTeam = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_AWAY));
            long kickoff = cursor.getLong(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_KICKOFF));
            int played = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_PLAYED));
            int scoreHome = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_HOME));
            int scoreAway = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_AWAY));
            String scorers = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORERS));
            games.add(new Game(gameId, league, homeTeam, awayTeam, kickoff, played, scoreHome, scoreAway, scorers));
        }
        return games;
    }

    public static Game cursorToGame(Cursor cursor) {
        Game game;
        if (cursor != null) {
            cursor.moveToFirst();
            int gameId = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_GAME_ID));
            String league = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_LEAGUE));
            String homeTeam = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_HOME));
            String awayTeam = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_TEAM_AWAY));
            long kickoff = cursor.getLong(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_KICKOFF));
            int played = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_PLAYED));
            int scoreHome = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_HOME));
            int scoreAway = cursor.getInt(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORE_AWAY));
            String scorers = cursor.getString(cursor.getColumnIndex(GameContract.GameEntry.COLUMN_SCORERS));
            game = new Game(gameId, league, homeTeam, awayTeam, kickoff, played, scoreHome, scoreAway, scorers);
        } else {
            game = null;
        }

        return game;
    }

    public static ContentValues[] getContentValues(ArrayList<Game> games) {
        ContentValues[] cvGames = new ContentValues[games.size()];
        for (int i = 0; i < games.size(); i++) {
            int gameId = games.get(i).getGameId();
            String league = games.get(i).getLeague();
            String homeTeam = games.get(i).getHomeTeam();
            String awayTeam = games.get(i).getAwayTeam();
            long kickoff = games.get(i).getKickoff();
            int played = games.get(i).getPlayed();
            int homeScore = games.get(i).getHomeScore();
            int awayScore = games.get(i).getAwayScore();
            String scorers = games.get(i).getScorers();
            cvGames[i] = new ContentValues();
            cvGames[i].put(GameContract.GameEntry.COLUMN_GAME_ID, gameId);
            cvGames[i].put(GameContract.GameEntry.COLUMN_LEAGUE, league);
            cvGames[i].put(GameContract.GameEntry.COLUMN_TEAM_HOME, homeTeam);
            cvGames[i].put(GameContract.GameEntry.COLUMN_TEAM_AWAY, awayTeam);
            cvGames[i].put(GameContract.GameEntry.COLUMN_KICKOFF, kickoff);
            cvGames[i].put(GameContract.GameEntry.COLUMN_PLAYED, played);
            cvGames[i].put(GameContract.GameEntry.COLUMN_SCORE_HOME, homeScore);
            cvGames[i].put(GameContract.GameEntry.COLUMN_SCORE_AWAY, awayScore);
            cvGames[i].put(GameContract.GameEntry.COLUMN_SCORERS, scorers);
        }
        return cvGames;
    }

    public static ContentValues getGameContentValues(Game game) {
        ContentValues cvGame = new ContentValues();
        int gameId = game.getGameId();
        String league = game.getLeague();
        String homeTeam = game.getHomeTeam();
        String awayTeam = game.getAwayTeam();
        long kickoff = game.getKickoff();
        int played = game.getPlayed();
        int homeScore = game.getHomeScore();
        int awayScore = game.getAwayScore();
        String scorers = game.getScorers();
        cvGame.put(GameContract.GameEntry.COLUMN_GAME_ID, gameId);
        cvGame.put(GameContract.GameEntry.COLUMN_LEAGUE, league);
        cvGame.put(GameContract.GameEntry.COLUMN_TEAM_HOME, homeTeam);
        cvGame.put(GameContract.GameEntry.COLUMN_TEAM_AWAY, awayTeam);
        cvGame.put(GameContract.GameEntry.COLUMN_KICKOFF, kickoff);
        cvGame.put(GameContract.GameEntry.COLUMN_PLAYED, played);
        cvGame.put(GameContract.GameEntry.COLUMN_SCORE_HOME, homeScore);
        cvGame.put(GameContract.GameEntry.COLUMN_SCORE_AWAY, awayScore);
        cvGame.put(GameContract.GameEntry.COLUMN_SCORERS, scorers);
        return cvGame;
    }

    public static ArrayList<String> extractScorers(String allScorers) {
        String scorersArray[] = allScorers.split(", ");
        ArrayList<String> scorers = new ArrayList<>();
        Collections.addAll(scorers, scorersArray);
        return scorers;
    }

    public static String getGameDate(long time) {
        Date date = new java.util.Date(time * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);
        // Data check
        SimpleDateFormat dateForChecking = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDateForChecking = dateForChecking.format(date);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (formattedDateForChecking.equals(currentDate)) {
            SimpleDateFormat justTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formattedDate = "Today " + justTime.format(date);
        }
        return formattedDate;
    }

    public static int getDrawableFlag(String league, Context context) {
        String drawableFlag;
        switch (league) {
            case Constants.SERIE_A:
                drawableFlag = "italy";
                break;
            case Constants.LIGUE_1:
                drawableFlag = "france";
                break;
            case Constants.BUNDESLIGA:
                drawableFlag = "germany";
                break;
            case Constants.LIGA:
                drawableFlag = "spain";
                break;
            default:
                drawableFlag = "uk";
                break;
        }
        return context.getResources().getIdentifier(drawableFlag,
                "drawable", context.getPackageName());
    }

    public static ArrayList<Integer> cursorToFavorites(Cursor data) {
        ArrayList<Integer> favorites = new ArrayList<>();
        data.moveToPosition(-1);
        while (data.moveToNext()) {
            int gameId = data.getInt(data.getColumnIndex(GameContract.GameEntry.COLUMN_GAME_ID));
            favorites.add(gameId);
        }
        return favorites;
    }

    public static boolean isFavorite(ArrayList<Integer> mFavorites, int gameId) {
        return mFavorites != null && mFavorites.contains(gameId);
    }
}