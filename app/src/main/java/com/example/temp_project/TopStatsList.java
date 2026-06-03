package com.example.temp_project;

import java.util.ArrayList;

public class TopStatsList {

    private ArrayList<TopStat> topStats = new ArrayList<>();

    public TopStatsList(String stat) {
        Thread t = new Thread(() -> {
            try {
                OkHttpHandler handler = new OkHttpHandler();
                String url = Config.BASE_URL + "getTopStats.php?stat=" + stat;
                topStats = handler.getTopStats(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join(); // περιμένουμε να ολοκληρωθεί το κατέβασμα (όπως στο StandingsList)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TopStat> getTopStats() {
        return topStats;
    }
}