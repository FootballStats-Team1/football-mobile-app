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

    // Επιστρέφει ΕΝΑ MatchDetails (αγώνας + συγκεντρωτικά στατιστικά home/away) με βάση το matchId.
    // ΠΡΟΣΟΧΗ: εδώ το JSON είναι εξωτερικά Object (ΟΧΙ Array), γιατί
    // η υπηρεσία επιστρέφει έναν μόνο αγώνα και όχι λίστα.
    public MatchDetails getMatchDetails(String url) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).build(); // GET METHOD

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        Match match = null;
        MatchStats homeStats = null;
        MatchStats awayStats = null;

        try {
            JSONObject obj = new JSONObject(data); // Object, όχι Array

            // Αν το PHP γύρισε error, επιστρέφουμε άδειο MatchDetails
            if (obj.has("error")) {
                return new MatchDetails(null, null, null);
            }

            // --- 1. Βασικά στοιχεία αγώνα ---
            int matchId = obj.getInt("match_id");
            int matchday = obj.getInt("matchday");
            String homeTeam = obj.getString("home_team");
            String homeLogo = obj.getString("home_logo");   // <-- το logo του γηπεδούχου (από το JOIN)
            String awayTeam = obj.getString("away_team");
            String awayLogo = obj.getString("away_logo");   // <-- το logo του φιλοξενούμενου (από το JOIN)
            int homeScore = obj.getInt("home_score");
            int awayScore = obj.getInt("away_score");
            String status = obj.getString("status");

            match = new Match(matchId, matchday, homeTeam, homeLogo, awayTeam, awayLogo, homeScore, awayScore, status);

            // --- 2. Συγκεντρωτικά στατιστικά κάθε ομάδας (nested objects μέσα στο JSON) ---
            homeStats = parseStats(obj.getJSONObject("home_stats"));
            awayStats = parseStats(obj.getJSONObject("away_stats"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new MatchDetails(match, homeStats, awayStats);
    }

    // Βοηθητική: μετατρέπει ένα JSONObject στατιστικών (home_stats / away_stats) σε MatchStats
    private MatchStats parseStats(JSONObject obj) throws JSONException {
        int goals = obj.getInt("goals");
        int shotsOnTarget = obj.getInt("shots_on_target");
        int shotsOffTarget = obj.getInt("shots_off_target");
        int passesSucc = obj.getInt("passes_succ");
        int passesFail = obj.getInt("passes_fail");
        int tacklesSucc = obj.getInt("tackles_succ");
        int tacklesFail = obj.getInt("tackles_fail");
        int crossesSucc = obj.getInt("crosses_succ");
        int crossesFail = obj.getInt("crosses_fail");
        int assists = obj.getInt("assists");
        int foulsCommitted = obj.getInt("fouls_committed");
        int foulsWon = obj.getInt("fouls_won");
        int cornersWon = obj.getInt("corners_won");
        int yellowCards = obj.getInt("yellow_cards");
        int redCards = obj.getInt("red_cards");
        int totalPasses = obj.getInt("total_passes");
        int possession = obj.getInt("possession");

        return new MatchStats(goals, shotsOnTarget, shotsOffTarget,
                passesSucc, passesFail, tacklesSucc, tacklesFail,
                crossesSucc, crossesFail, assists,
                foulsCommitted, foulsWon, cornersWon,
                yellowCards, redCards, totalPasses, possession);
    }

    public ArrayList<ArrayList<String>> getMatchLineups(String url) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ArrayList<ArrayList<String>> allLists = new ArrayList<>();
        for (int i=0; i<4; i++) allLists.add(new ArrayList<String>());

        try {
            JSONObject obj = new JSONObject(response.body().string());

            JSONArray hs = obj.getJSONArray("home_starters");
            for (int i=0; i<hs.length(); i++) allLists.get(0).add(hs.getString(i));

            JSONArray as = obj.getJSONArray("away_starters");
            for (int i=0; i<as.length(); i++) allLists.get(1).add(as.getString(i));

            JSONArray hsub = obj.getJSONArray("home_subs");
            for (int i=0; i<hsub.length(); i++) allLists.get(2).add(hsub.getString(i));

            JSONArray asub = obj.getJSONArray("away_subs");
            for (int i=0; i<asub.length(); i++) allLists.get(3).add(asub.getString(i));

        } catch (JSONException e) { e.printStackTrace(); }
        return allLists;
    }

    public ArrayList<ArrayList<PlayerMatchStats>> getMatchPlayerStats(String url) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ArrayList<PlayerMatchStats> home = new ArrayList<>();
        ArrayList<PlayerMatchStats> away = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(response.body().string());
            parsePlayers(obj.getJSONArray("home_players"), home);
            parsePlayers(obj.getJSONArray("away_players"), away);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<PlayerMatchStats>> result = new ArrayList<>();
        result.add(home); // index 0 = home
        result.add(away); // index 1 = away
        return result;
    }

    private void parsePlayers(JSONArray arr, ArrayList<PlayerMatchStats> list) throws JSONException {
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            list.add(new PlayerMatchStats(
                    o.getInt("player_id"), o.getString("name"), o.getString("position"), o.getString("photo"),
                    o.getInt("goals"), o.getInt("assists"),
                    o.getInt("shots_on_target"), o.getInt("shots_off_target"),
                    o.getInt("passes_succ"), o.getInt("passes_fail"),
                    o.getInt("tackles_succ"), o.getInt("tackles_fail"),
                    o.getInt("crosses_succ"), o.getInt("crosses_fail"),
                    o.getInt("errors"), o.getInt("fouls_won"), o.getInt("fouls_committed"),
                    o.getInt("yellow_cards"), o.getInt("red_cards")
            ));
        }
    }

    public ArrayList<TopStat> getTopStats(String url) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ArrayList<TopStat> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(response.body().string());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                TopStat t = new TopStat();
                t.playerId = o.getInt("player_id");
                t.name     = o.getString("name");
                t.position = o.getString("position");
                t.photo    = o.getString("photo");
                t.teamId   = o.getInt("team_id");
                t.teamName = o.getString("team_name");
                t.badge    = o.getString("badge");
                t.total    = o.getInt("total");
                list.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


}
