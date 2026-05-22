package com.example.temp_project;

public class Match {
    private int matchId;
    private int matchday;
    private String homeTeam;
    private String homeLogo;
    private String awayTeam;
    private String awayLogo;
    private int homeScore;
    private int awayScore;
    private String status; // "pending", "live", "finished"

    // Constructor
    public Match(int matchId, int matchday, String homeTeam, String homeLogo, String awayTeam, String awayLogo, int homeScore, int awayScore, String status) {
        this.matchId = matchId;
        this.matchday = matchday;
        this.homeTeam = homeTeam;
        this.homeLogo = homeLogo;
        this.awayTeam = awayTeam;
        this.awayLogo = awayLogo;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }

    // Βοηθητική μέθοδος για να παίρνουμε έτοιμο το κείμενο του σκορ στο UI
    public String getScoreText() {
        // SOS - To service επιστρέφει -1 σκορ για τους αγώνες που είναι 'pending'
        if (homeScore == -1 || awayScore == -1) {
            return "- : -";
        }
        return homeScore + " - " + awayScore;
    }

    // Getters
    public String getHomeTeam() { return homeTeam; }
    public String getHomeLogo() { return homeLogo; }
    public String getAwayTeam() { return awayTeam; }
    public String getAwayLogo() { return awayLogo; }
    public String getStatus() { return status; }
    public int getMatchId() { return matchId; } // will be used

    /* NOT USED GETTERS
    public int getMatchday() { return matchday; }
    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }*/



}