package com.example.temp_project;

public class Player {
    private int id;
    private String name;
    private String position;
    private int teamId;
    private String photoUrl;
    private boolean isStarting; // Χρήσιμο όταν τραβάμε τις ενδεκάδες

    public Player(int id, String name, String position, int teamId, String photoUrl, boolean isStarting) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.teamId = teamId;
        this.photoUrl = photoUrl;
        this.isStarting = isStarting;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public int getTeamId() { return teamId; }
    public String getPhotoUrl() { return photoUrl; }
    public boolean isStarting() { return isStarting; }
}
