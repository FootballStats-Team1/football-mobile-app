package com.example.temp_project;

public class Standing {
    private int teamId;
    private String teamName; // <-- Η ΠΡΟΣΘΗΚΗ
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    // Ενημερωμένος Constructor
    public Standing(int teamId, String teamName, int points, int wins, int draws, int losses, int goalsFor, int goalsAgainst) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.points = points;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
    }

    // Getters
    public int getTeamId() { return teamId; }
    public String getTeamName() { return teamName; } // <-- Η ΠΡΟΣΘΗΚΗ
    public int getPoints() { return points; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
}
