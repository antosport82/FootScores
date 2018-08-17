package com.example.antonio.footscores.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class GameContract {

    public static final String CONTENT_AUTHORITY = "com.example.antonio.footscores";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_GAMES = "games";
    public static final String PATH_FAVORITES = "favorites";

    public static final class GameEntry implements BaseColumns {

        public static final Uri CONTENT_URI_GAMES = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_GAMES)
                .build();

        public static final Uri CONTENT_URI_FAVORITES = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME_GAMES = "games";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_LEAGUE = "league";
        public static final String COLUMN_TEAM_HOME = "team_home";
        public static final String COLUMN_TEAM_AWAY = "team_away";
        public static final String COLUMN_KICKOFF = "kickoff";
        public static final String COLUMN_PLAYED = "played";
        public static final String COLUMN_SCORE_HOME = "score_home";
        public static final String COLUMN_SCORE_AWAY = "score_away";
        public static final String COLUMN_SCORERS = "scorers";

        public static final String TABLE_NAME_FAVORITES = "favorites";
        public static final String _FAV_ID = BaseColumns._ID;

        /**
         * Builds a URI that adds the game id to the end of the game content URI path.
         * This is used to query details about a single game entry by id.
         *
         * @param id Id of the game
         * @return Uri to query details about a single game entry
         */

        public static Uri buildGameUriWithId(int id) {
            return CONTENT_URI_GAMES.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

        public static Uri buildGameFavUriWithId(int id) {
            return CONTENT_URI_FAVORITES.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }
}