package com.example.temp_project;

public class Match {
    private int id;
    private int round;
    private int homeTeamId;
    private int awayTeamId;
    private String status; // "pending", "live", "finished"
    private int homeScore;
    private int awayScore;

    public Match(int id, int round, int homeTeamId, int awayTeamId, String status, int homeScore, int awayScore) {
        this.id = id;
        this.round = round;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    // Getters
    public int getId() { return id; }
    public int getRound() { return round; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public String getStatus() { return status; }
    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
}
