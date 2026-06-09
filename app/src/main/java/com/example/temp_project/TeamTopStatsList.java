package com.example.temp_project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeamTopStatsList {

    private final String stat;

    public TeamTopStatsList(String stat) {
        this.stat = stat;
    }

    public ArrayList<TeamTopStat> getTopStats() {
        final ArrayList<TeamTopStat> list = new ArrayList<>();
        final String url = Config.BASE_URL + "getTeamTopStats.php?stat=" + stat;

        Thread t = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                String json = response.body().string();

                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    TeamTopStat ts = new TeamTopStat();
                    ts.teamId = o.getInt("team_id");
                    ts.name   = o.getString("team_name");
                    ts.badge  = o.getString("badge");
                    ts.total  = o.getInt("total");
                    list.add(ts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list;
    }
}