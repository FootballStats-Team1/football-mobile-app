package com.example.temp_project;

public class Standing {
    private int teamId;
    private String teamName; // <-- ΝΕΟ ΠΕΔΙΟ
    private String logoUrl; // <-- ΝΕΟ ΠΕΔΙΟ
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    // Ενημερωμένος Constructor
    public Standing(int teamId, String teamName, String logoUrl, int points, int wins, int draws, int losses, int goalsFor, int goalsAgainst) {
        this.teamId = teamId;
        this.teamName = teamName; // <-- ΝΕΟ ΠΕΔΙΟ
        this.logoUrl = logoUrl; // <-- ΝΕΟ ΠΕΔΙΟ
        this.points = points;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
    }

    // Getters
    public String getTeamName() { return teamName; }
    public int getPoints() { return points; }
    public String getLogoUrl() { return logoUrl; }
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
    public int getMatchesPlayed() {
        return wins + draws + losses;
    }

    /* NOT USED GETTERS
    public int getTeamId() { return teamId; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }*/
}
