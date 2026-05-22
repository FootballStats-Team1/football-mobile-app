package com.example.temp_project;

import java.util.ArrayList;

public class MatchesList {
    private ArrayList<Match> matches = new ArrayList<>();

    // Constructor - πάντα θα στέλνουμε αγωνιστική!
    public MatchesList(int matchday) {
        String url = Config.BASE_URL + "getMatches.php?matchday=" + matchday; // ΠΡΟΣΟΧΗ η υπηρεσία παίρνει ως GET attribute την αγωνιστική! (π.χ. 'getMatches.php?matchday=3')

        try {
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            matches = okHttpHandler.getMatches(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }
}