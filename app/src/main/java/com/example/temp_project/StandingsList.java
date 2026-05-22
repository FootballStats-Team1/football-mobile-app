package com.example.temp_project;

import java.util.ArrayList;

public class StandingsList {
    private ArrayList<Standing> standings = new ArrayList<>();

    // Δεν χρειάζεται να παίρνει παράμετρο ο constructor γιατί έχουμε το URL ως global μεταβλητή
    public StandingsList() {
        String url = Config.BASE_URL + "getStandings.php";

        try {
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            standings = okHttpHandler.getStandings(url); // Γεμίζουμε τη λίστα
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Standing> getStandings() {
        return standings;
    }
}