package com.example.antonio.footscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    public GameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GAMES_TABLE =
                "CREATE TABLE " + GameContract.GameEntry.TABLE_NAME_GAMES + " (" +
                        GameContract.GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GameContract.GameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL, " +
                        GameContract.GameEntry.COLUMN_LEAGUE + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_TEAM_HOME + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_TEAM_AWAY + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_KICKOFF + " LONG NOT NULL, " +
                        GameContract.GameEntry.COLUMN_PLAYED + " INTEGER NOT NULL, " +
                        GameContract.GameEntry.COLUMN_SCORE_HOME + " INTEGER," +
                        GameContract.GameEntry.COLUMN_SCORE_AWAY + " INTEGER," +
                        GameContract.GameEntry.COLUMN_SCORERS + " TEXT" +
                        ");";

        db.execSQL(SQL_CREATE_GAMES_TABLE);

        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + GameContract.GameEntry.TABLE_NAME_FAVORITES + " (" +
                        GameContract.GameEntry._FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GameContract.GameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL, " +
                        GameContract.GameEntry.COLUMN_LEAGUE + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_TEAM_HOME + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_TEAM_AWAY + " TEXT NOT NULL, " +
                        GameContract.GameEntry.COLUMN_KICKOFF + " LONG NOT NULL, " +
                        GameContract.GameEntry.COLUMN_PLAYED + " INTEGER NOT NULL, " +
                        GameContract.GameEntry.COLUMN_SCORE_HOME + " INTEGER," +
                        GameContract.GameEntry.COLUMN_SCORE_AWAY + " INTEGER," +
                        GameContract.GameEntry.COLUMN_SCORERS + " TEXT" +
                        ");";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GameContract.GameEntry.TABLE_NAME_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + GameContract.GameEntry.TABLE_NAME_FAVORITES);
        onCreate(db);
    }
}