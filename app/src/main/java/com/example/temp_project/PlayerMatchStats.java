package com.example.temp_project;

// Ατομικά στατιστικά ΕΝΟΣ παίκτη σε ΕΝΑΝ αγώνα (μία γραμμή του match_events).
public class PlayerMatchStats{
    private int playerId;
    private String name;
    private String position;

    private int goals, assists;
    private int shotsOnTarget, shotsOffTarget;
    private int passesSucc, passesFail;
    private int tacklesSucc, tacklesFail;
    private int crossesSucc, crossesFail;
    private int errors, foulsWon, foulsCommitted;
    private int yellowCards, redCards;
    private String photo;

    public PlayerMatchStats(int playerId, String name, String position, String photo,
                            int goals, int assists,
                            int shotsOnTarget, int shotsOffTarget,
                            int passesSucc, int passesFail,
                            int tacklesSucc, int tacklesFail,
                            int crossesSucc, int crossesFail,
                            int errors, int foulsWon, int foulsCommitted,
                            int yellowCards, int redCards) {
        this.playerId = playerId;
        this.name = name;
        this.position = position;
        this.photo = photo;
        this.goals = goals;
        this.assists = assists;
        this.shotsOnTarget = shotsOnTarget;
        this.shotsOffTarget = shotsOffTarget;
        this.passesSucc = passesSucc;
        this.passesFail = passesFail;
        this.tacklesSucc = tacklesSucc;
        this.tacklesFail = tacklesFail;
        this.crossesSucc = crossesSucc;
        this.crossesFail = crossesFail;
        this.errors = errors;
        this.foulsWon = foulsWon;
        this.foulsCommitted = foulsCommitted;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public int getPlayerId() { return playerId; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public int getGoals() { return goals; }
    public String getPhoto() { return photo; }
    public int getAssists() { return assists; }
    public int getShotsOnTarget() { return shotsOnTarget; }
    public int getShotsOffTarget() { return shotsOffTarget; }
    public int getPassesSucc() { return passesSucc; }
    public int getPassesFail() { return passesFail; }
    public int getTacklesSucc() { return tacklesSucc; }
    public int getTacklesFail() { return tacklesFail; }
    public int getCrossesSucc() { return crossesSucc; }
    public int getCrossesFail() { return crossesFail; }
    public int getErrors() { return errors; }
    public int getFoulsWon() { return foulsWon; }
    public int getFoulsCommitted() { return foulsCommitted; }
    public int getYellowCards() { return yellowCards; }
    public int getRedCards() { return redCards; }
}