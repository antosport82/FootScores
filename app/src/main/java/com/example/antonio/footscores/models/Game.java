package com.example.antonio.footscores.models;

public class Game {

    private int gameId;
    private String league;
    private String homeTeam;
    private String awayTeam;
    private long kickoff;
    private int played;
    private int homeScore;
    private int awayScore;
    private String scorers;

    public Game(int id, String league, String homeTeam, String awayTeam, long kickoff, int played, int homeScore, int awayScore, String scorers) {
        this.gameId = id;
        this.league = league;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickoff = kickoff;
        this.played = played;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.scorers = scorers;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int id) {
        this.gameId = id;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public long getKickoff() {
        return kickoff;
    }

    public void setKickoff(long kickoff) {
        this.kickoff = kickoff;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getScorers() {
        return scorers;
    }

    public void setScorers(String scorers) {
        this.scorers = scorers;
    }
}