package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MatchDetailsActivity extends AppCompatActivity {

    private ImageView imgHomeLogo, imgAwayLogo;
    private TextView tvHomeTeam, tvAwayTeam, tvScore, tvStatus;
    private Button teamStatsBtn, personalStatsBtn;

    private ScrollView statsScroll;
    private TextView tvHomePossession, tvAwayPossession;
    private TextView tvHomeShots, tvAwayShots;
    private TextView tvHomeShotsOn, tvAwayShotsOn;
    private TextView tvHomePasses, tvAwayPasses;
    private TextView tvHomePassAcc, tvAwayPassAcc;
    private TextView tvHomeTackles, tvAwayTackles;
    private TextView tvHomeCorners, tvAwayCorners;
    private TextView tvHomeFouls, tvAwayFouls;
    private TextView tvHomeYellows, tvAwayYellows;
    private TextView tvHomeReds, tvAwayReds;

    private MatchDetails matchDetails;
    private int matchId;
    private boolean statsFilled = false;

    private ScrollView lineupsScroll;
    private boolean lineupsFilled = false;

    private LinearLayout teamSelectorLayout;
    private ImageView btnHomeTeam, btnAwayTeam;

    private LinearLayout layoutSelectedStarters, layoutSelectedSubs;

    private ArrayList<ArrayList<String>> cachedLineups;

    private java.util.HashMap<String, String> playerColors = new java.util.HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        setTitle(R.string.title_match_details);

        matchId = getIntent().getIntExtra("MATCH_ID", -1);

        bindViews();

        matchDetails = new MatchDetails(matchId);

        if (matchDetails.getMatch() != null) {
            populateHeader(matchDetails.getMatch());
        }

        setupButtons();

        fillTeamStats();
        statsScroll.setVisibility(View.VISIBLE);
    }

    private void bindViews() {
        imgHomeLogo = findViewById(R.id.imgHomeLogo);
        imgAwayLogo = findViewById(R.id.imgAwayLogo);
        tvHomeTeam = findViewById(R.id.tvHomeTeam);
        tvAwayTeam = findViewById(R.id.tvAwayTeam);
        tvScore = findViewById(R.id.tvScore);
        tvStatus = findViewById(R.id.tvStatus);
        teamStatsBtn = findViewById(R.id.teamStatsBtn);
        personalStatsBtn = findViewById(R.id.personalStatsBtn);

        statsScroll = findViewById(R.id.statsScroll);
        tvHomePossession = findViewById(R.id.tvHomePossession);
        tvAwayPossession = findViewById(R.id.tvAwayPossession);
        tvHomeShots = findViewById(R.id.tvHomeShots);
        tvAwayShots = findViewById(R.id.tvAwayShots);
        tvHomeShotsOn = findViewById(R.id.tvHomeShotsOn);
        tvAwayShotsOn = findViewById(R.id.tvAwayShotsOn);
        tvHomePasses = findViewById(R.id.tvHomePasses);
        tvAwayPasses = findViewById(R.id.tvAwayPasses);
        tvHomePassAcc = findViewById(R.id.tvHomePassAcc);
        tvAwayPassAcc = findViewById(R.id.tvAwayPassAcc);
        tvHomeTackles = findViewById(R.id.tvHomeTackles);
        tvAwayTackles = findViewById(R.id.tvAwayTackles);
        tvHomeCorners = findViewById(R.id.tvHomeCorners);
        tvAwayCorners = findViewById(R.id.tvAwayCorners);
        tvHomeFouls = findViewById(R.id.tvHomeFouls);
        tvAwayFouls = findViewById(R.id.tvAwayFouls);
        tvHomeYellows = findViewById(R.id.tvHomeYellows);
        tvAwayYellows = findViewById(R.id.tvAwayYellows);
        tvHomeReds = findViewById(R.id.tvHomeReds);
        tvAwayReds = findViewById(R.id.tvAwayReds);

        lineupsScroll = findViewById(R.id.lineupsScroll);
        teamSelectorLayout = findViewById(R.id.teamSelectorLayout);
        btnHomeTeam = findViewById(R.id.btnHomeTeam);
        btnAwayTeam = findViewById(R.id.btnAwayTeam);
        layoutSelectedStarters = findViewById(R.id.layoutSelectedStarters);
        layoutSelectedSubs = findViewById(R.id.layoutSelectedSubs);
    }

    private void populateHeader(Match match) {
        tvHomeTeam.setText(match.getHomeTeam());
        tvAwayTeam.setText(match.getAwayTeam());

        Picasso.with(getApplicationContext())
                .load(match.getHomeLogo())
                .error(R.mipmap.ic_launcher_round)
                .into(imgHomeLogo);

        Picasso.with(getApplicationContext())
                .load(match.getAwayLogo())
                .error(R.mipmap.ic_launcher_round)
                .into(imgAwayLogo);

        tvScore.setText(match.getScoreText());

        if ("live".equalsIgnoreCase(match.getStatus())) {
            tvScore.setTextColor(android.graphics.Color.RED);
            tvStatus.setText("LIVE");
            tvStatus.setTextColor(android.graphics.Color.RED);
        } else if ("finished".equalsIgnoreCase(match.getStatus())) {
            tvStatus.setText(getString(R.string.status_finished));
        } else {
            tvStatus.setText(getString(R.string.status_pending));
        }

        Picasso.with(getApplicationContext())
                .load(match.getHomeLogo())
                .into(btnHomeTeam);

        Picasso.with(getApplicationContext())
                .load(match.getAwayLogo())
                .into(btnAwayTeam);
    }

    private void setupButtons() {
        teamStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineupsScroll.setVisibility(View.GONE);
                teamSelectorLayout.setVisibility(View.GONE);

                if (statsScroll.getVisibility() == View.VISIBLE) {
                    statsScroll.setVisibility(View.GONE);
                } else {
                    if (!statsFilled) {
                        fillTeamStats();
                    }
                    statsScroll.setVisibility(View.VISIBLE);
                }
            }
        });

        personalStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statsScroll.setVisibility(View.GONE);

                if (teamSelectorLayout.getVisibility() == View.VISIBLE) {
                    teamSelectorLayout.setVisibility(View.GONE);
                    lineupsScroll.setVisibility(View.GONE);
                } else {
                    if (!lineupsFilled) {
                        fillLineups();
                    }
                    teamSelectorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fillTeamStats() {
        MatchStats home = matchDetails.getHomeStats();
        MatchStats away = matchDetails.getAwayStats();

        if (home == null || away == null) {
            return;
        }

        tvHomePossession.setText(home.getPossessionText());
        tvAwayPossession.setText(away.getPossessionText());

        tvHomeShots.setText(String.valueOf(home.getTotalShots()));
        tvAwayShots.setText(String.valueOf(away.getTotalShots()));

        tvHomeShotsOn.setText(String.valueOf(home.getShotsOnTarget()));
        tvAwayShotsOn.setText(String.valueOf(away.getShotsOnTarget()));

        tvHomePasses.setText(String.valueOf(home.getTotalPasses()));
        tvAwayPasses.setText(String.valueOf(away.getTotalPasses()));

        tvHomePassAcc.setText(home.getPassAccuracyText());
        tvAwayPassAcc.setText(away.getPassAccuracyText());

        tvHomeTackles.setText(String.valueOf(home.getTacklesSucc()));
        tvAwayTackles.setText(String.valueOf(away.getTacklesSucc()));

        tvHomeCorners.setText(String.valueOf(home.getCornersWon()));
        tvAwayCorners.setText(String.valueOf(away.getCornersWon()));

        tvHomeFouls.setText(String.valueOf(home.getFoulsCommitted()));
        tvAwayFouls.setText(String.valueOf(away.getFoulsCommitted()));

        tvHomeYellows.setText(String.valueOf(home.getYellowCards()));
        tvAwayYellows.setText(String.valueOf(away.getYellowCards()));

        tvHomeReds.setText(String.valueOf(home.getRedCards()));
        tvAwayReds.setText(String.valueOf(away.getRedCards()));

        statsFilled = true;
    }

    private void fillLineups() {
        try {
            OkHttpHandler handler = new OkHttpHandler();

            cachedLineups = handler.getMatchLineups(
                    Config.BASE_URL + "getMatchLineups.php?matchId=" + matchId
            );

            fetchPlayerColors();
            setupTeamButtons();

            lineupsFilled = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchPlayerColors() {
        Thread t = new Thread(() -> {
            try {
                okhttp3.OkHttpClient c = new okhttp3.OkHttpClient();
                okhttp3.Request req = new okhttp3.Request.Builder()
                        .url(Config.BASE_URL + "getLineupStatus.php?matchId=" + matchId)
                        .build();
                okhttp3.Response resp = c.newCall(req).execute();
                if (resp.isSuccessful()) {
                    String data = resp.body().string();
                    org.json.JSONArray arr = new org.json.JSONArray(data);
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject o = arr.getJSONObject(i);
                        playerColors.put(o.getString("name"), o.optString("color", ""));
                    }
                }
                resp.close();
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
    }

    private void populateColumn(LinearLayout container, ArrayList<String> players) {
        for (String playerName : players) {
            Button btn = new Button(this);
            btn.setText(playerName);

            btn.setTextColor(android.graphics.Color.WHITE);
            btn.setTextSize(15f);
            btn.setTypeface(null, android.graphics.Typeface.BOLD);
            btn.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

            int tintColor;
            String color = playerColors.get(playerName);
            if ("red".equals(color)) {
                tintColor = android.graphics.Color.parseColor("#D32F2F");
            } else if ("green".equals(color)) {
                tintColor = android.graphics.Color.parseColor("#2E7D32");
            } else if ("yellow".equals(color)) {
                tintColor = android.graphics.Color.parseColor("#FBC02D");
                btn.setTextColor(android.graphics.Color.BLACK);
            } else {
                tintColor = ContextCompat.getColor(this, R.color.palette_blue_4);
            }
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(tintColor));

            btn.setOnClickListener(v -> {
                Intent intent = new Intent(MatchDetailsActivity.this, PersonalStatsActivity.class);
                intent.putExtra("PLAYER_NAME", playerName);
                intent.putExtra("MATCH_ID", matchId);
                startActivity(intent);
            });

            container.addView(btn);
        }
    }

    private void setupTeamButtons() {
        btnHomeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeamLineup(
                        cachedLineups.get(0),
                        cachedLineups.get(2)
                );
            }
        });

        btnAwayTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeamLineup(
                        cachedLineups.get(1),
                        cachedLineups.get(3)
                );
            }
        });
    }

    private void showTeamLineup(ArrayList<String> starters, ArrayList<String> subs) {
        layoutSelectedStarters.removeAllViews();
        layoutSelectedSubs.removeAllViews();

        populateColumn(layoutSelectedStarters, starters);
        populateColumn(layoutSelectedSubs, subs);

        lineupsScroll.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}