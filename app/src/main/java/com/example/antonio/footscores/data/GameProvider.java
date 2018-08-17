package com.example.antonio.footscores.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GameProvider extends ContentProvider {

    private static final int CODE_GAMES = 100;
    private static final int CODE_GAME_WITH_ID = 101;
    private static final int CODE_FAVORITES = 200;
    private static final int CODE_FAVORITES_WITH_ID = 201;

    private GameDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GameContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, GameContract.PATH_GAMES, CODE_GAMES);
        matcher.addURI(authority, GameContract.PATH_GAMES + "/#", CODE_GAME_WITH_ID);

        matcher.addURI(authority, GameContract.PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(authority, GameContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new GameDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            case CODE_GAMES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        GameContract.GameEntry.TABLE_NAME_GAMES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_GAME_WITH_ID:

                String idGame = uri.getLastPathSegment();
                String[] selectionArgumentsGame = new String[]{idGame};

                cursor = mOpenHelper.getReadableDatabase().query(
                        GameContract.GameEntry.TABLE_NAME_GAMES,
                        projection,
                        GameContract.GameEntry.COLUMN_GAME_ID + " = ? ",
                        selectionArgumentsGame,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_FAVORITES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        GameContract.GameEntry.TABLE_NAME_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_FAVORITES_WITH_ID:

                String idFavorite = uri.getLastPathSegment();
                String[] selectionArgumentsFavorite = new String[]{idFavorite};

                cursor = mOpenHelper.getReadableDatabase().query(
                        GameContract.GameEntry.TABLE_NAME_FAVORITES,
                        projection,
                        GameContract.GameEntry.COLUMN_GAME_ID + " = ? ",
                        selectionArgumentsFavorite,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {

            case CODE_FAVORITES:
                long idFavorite = db.insert(GameContract.GameEntry.TABLE_NAME_FAVORITES, null, values);
                if (idFavorite > 0) {
                    returnUri = ContentUris.withAppendedId(GameContract.GameEntry.CONTENT_URI_FAVORITES, idFavorite);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the movies directory
        int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case CODE_GAMES:
                tableName = GameContract.GameEntry.TABLE_NAME_GAMES;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        // allows for multiple transactions
        db.beginTransaction();

        // keep track of successful inserts
        int numInserted = 0;

        try {
            for (ContentValues value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("Cannot have null content values");
                }
                long _id = -1;

                try {
                    _id = db.insert(tableName,
                            null, value);
                } catch (SQLiteConstraintException e) {
                    e.printStackTrace();
                }
                if (_id != -1) {
                    numInserted++;
                }
            }
            if (numInserted > 0) {
                // If no errors, declare a successful transaction.
                // database will not populate if this is not called
                db.setTransactionSuccessful();
            }
        } finally {
            // all transactions occur at once
            db.endTransaction();
        }
        if (numInserted > 0) {
            // if there was successful insertion, notify the content resolver that there
            // was a change
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return numInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int deletedRows;

        switch (match) {

            case CODE_GAMES:
                deletedRows = db.delete(GameContract.GameEntry.TABLE_NAME_GAMES, selection, selectionArgs);
                break;

            case CODE_FAVORITES_WITH_ID:
                String idFavorite = uri.getLastPathSegment();
                String[] selectionArgumentsFavorite = new String[]{idFavorite};

                deletedRows = db.delete(GameContract.GameEntry.TABLE_NAME_FAVORITES,
                        GameContract.GameEntry.COLUMN_GAME_ID + " = ? ",
                        selectionArgumentsFavorite);
                break;

            default:
                return 0;
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}