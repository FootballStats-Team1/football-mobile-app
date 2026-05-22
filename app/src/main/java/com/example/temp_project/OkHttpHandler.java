package com.example.temp_project;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHandler {

    public OkHttpHandler() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    // Οι μέθοδοι επιστρέφουν απευθείας Λίστα από Objects

    public ArrayList<Standing> getStandings(String url) throws Exception {
        ArrayList<Standing> standingslist = new ArrayList<>();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // Δεν χρειάζεται να κάνουμε δημιουργία ενός κενού body (RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));)
        // Γιατί ΔΕΝ βάζουμε μέθοδο POST στο request με .method("POST", body). Οπότε το request είναι από προεπιλογή GET και δεν χρειάζεται σώμα
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        try {
            // Φτιάχνουμε JSONArray αντί για JSONObject γιατί το JSON message επιστρέφει εξωτερικά Array και ΟΧΙ Object
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Τραβάμε τα δεδομένα
                int teamId = obj.getInt("team_id");
                String teamName = obj.getString("team_name");
                String logoUrl = obj.getString("logo_url");
                int points = obj.getInt("points");
                int wins = obj.getInt("wins");
                int draws = obj.getInt("draws");
                int losses = obj.getInt("losses");
                int goalsFor = obj.getInt("goals_for");
                int goalsAgainst = obj.getInt("goals_against");

                // Φτιάχνουμε το Object
                standingslist.add(new Standing(teamId, teamName, logoUrl, points, wins, draws, losses, goalsFor, goalsAgainst));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return standingslist;
    }

    public ArrayList<Match> getMatches(String url) throws Exception {
        ArrayList<Match> matcheslist = new ArrayList<>();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).build(); // GET METHOD

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int matchId = obj.getInt("match_id");
                int matchday = obj.getInt("matchday");
                String homeTeam = obj.getString("home_team");
                String homeLogo = obj.getString("home_logo");
                String awayTeam = obj.getString("away_team");
                String awayLogo = obj.getString("away_logo");
                int homeScore = obj.getInt("home_score");
                int awayScore = obj.getInt("away_score");
                String status = obj.getString("status");

                // Δημιουργία του Object
                matcheslist.add(new Match(matchId, matchday, homeTeam, homeLogo, awayTeam, awayLogo, homeScore, awayScore, status));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return matcheslist;
    }
}