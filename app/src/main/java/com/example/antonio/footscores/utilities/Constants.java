package com.example.antonio.footscores.utilities;

public class Constants {
    // Json
    public static final String GAMES_URL = "http://gobetting24.com/scores.json";
    public static final String GAME_ID = "game_id";
    public static final String LEAGUE = "league";
    public static final String TEAM_HOME = "team_home";
    public static final String TEAM_AWAY = "team_away";
    public static final String KICKOFF = "kickoff";
    public static final String PLAYED = "played";
    public static final String SCORE_HOME = "score_home";
    public static final String SCORE_AWAY = "score_away";
    public static final String SCORERS = "scorers";
    // Loaders
    public static final int SINGLE_GAME_LOADER = 11;
    public static final int ALL_GAMES_LOADER = 12;
    public static final int FAVORITES_LOADER = 13;
    public static final int SINGLE_FAV_LOADER = 14;
    // Leagues
    public static final String SERIE_A = "Serie A";
    public static final String BUNDESLIGA = "Bundesliga";
    public static final String LIGA = "Liga";
    public static final String LIGUE_1 = "Ligue 1";
    // Game Types
    public static final String GAME_TYPE = "game_type";
    public static final int ALL_GAMES_TYPE = 1;
    public static final int FAVORITE_TYPE = 2;
    // Notifications
    public static final int PENDING_INTENT_NOTIFICATION = 31;
    public static final String NOTIFICATION_CHANNEL = "channel_games";
    public static final int NOTIFICATION_CHANNEL_ID = 41;
}