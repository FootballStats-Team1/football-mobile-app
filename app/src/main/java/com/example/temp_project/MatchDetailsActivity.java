package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class MatchDetailsActivity extends AppCompatActivity {

    private ImageView imgHomeLogo, imgAwayLogo;
    private TextView tvHomeTeam, tvAwayTeam, tvScore, tvStatus;
    private Button teamStatsBtn, personalStatsBtn;

    // Panel στατιστικών
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

    // Κρατάμε τα δεδομένα του αγώνα για να τα χρησιμοποιήσουν τα κουμπιά
    private MatchDetails matchDetails;
    private int matchId;
    private boolean statsFilled = false; // για να μη γεμίζουμε το panel πολλές φορές

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        setTitle(R.string.title_match_details);

        // 1. "Ξεπακετάρουμε" ΜΟΝΟ το ID που έστειλε η MatchesActivity
        matchId = getIntent().getIntExtra("MATCH_ID", -1);

        // 2. Σύνδεση των μεταβλητών με τα UI elements
        bindViews();

        // 3. Κατεβάζουμε τον αγώνα + τα στατιστικά από το backend με βάση το matchId
        //    Το service κάνει JOIN με τον πίνακα teams (για logos) και SUM των match_events (για stats)
        matchDetails = new MatchDetails(matchId);

        // 4. Γεμίζουμε το header (logos, ονόματα, σκορ, status)
        if (matchDetails.getMatch() != null) {
            populateHeader(matchDetails.getMatch());
        }

        // 5. Ορίζουμε τι κάνουν τα κουμπιά
        setupButtons();
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

        // Panel στατιστικών
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
    }

    private void populateHeader(Match match) {
        // --- Ονόματα ομάδων ---
        tvHomeTeam.setText(match.getHomeTeam());
        tvAwayTeam.setText(match.getAwayTeam());

        // --- Logos των δύο ομάδων με Picasso (ίδιο pattern με τη MatchesActivity) ---
        Picasso.with(getApplicationContext())
                .load(match.getHomeLogo())
                .error(R.mipmap.ic_launcher_round)
                .into(imgHomeLogo);

        Picasso.with(getApplicationContext())
                .load(match.getAwayLogo())
                .error(R.mipmap.ic_launcher_round)
                .into(imgAwayLogo);

        // --- Σκορ + έλεγχος αν είναι LIVE ---
        tvScore.setText(match.getScoreText()); // έτοιμο κείμενο (π.χ. "1 - 1" ή "- : -")

        if ("live".equalsIgnoreCase(match.getStatus())) {
            tvScore.setTextColor(android.graphics.Color.RED);
            tvStatus.setText("LIVE");
            tvStatus.setTextColor(android.graphics.Color.RED);
        } else if ("finished".equalsIgnoreCase(match.getStatus())) {
            tvStatus.setText(getString(R.string.status_finished)); // π.χ. "Τελικό"
        } else {
            tvStatus.setText(getString(R.string.status_pending));  // π.χ. "Δεν έχει ξεκινήσει"
        }
    }

    private void setupButtons() {
        // --- TEAM STATS: εμφανίζει/κρύβει το panel με τα συγκεντρωτικά στατιστικά ---
        teamStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle: αν είναι ορατό κρύψτο, αλλιώς δείξτο
                if (statsScroll.getVisibility() == View.VISIBLE) {
                    statsScroll.setVisibility(View.GONE);
                    return;
                }

                // Γεμίζουμε το panel μόνο την πρώτη φορά
                if (!statsFilled) {
                    fillTeamStats();
                }
                statsScroll.setVisibility(View.VISIBLE);
            }
        });

        // --- PERSONAL STATS: ανά παίκτη (θα φτιαχτεί αργότερα) ---
        personalStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Εδώ θα ανοίγει η οθόνη με τα προσωπικά στατιστικά κάθε παίκτη.
                /*
                Intent intent = new Intent(MatchDetailsActivity.this, PersonalStatsActivity.class);
                intent.putExtra("MATCH_ID", matchId);
                startActivity(intent);
                */
            }
        });
    }

    // Γεμίζει το panel με τα συγκεντρωτικά στατιστικά (τα έχουμε ήδη στη μνήμη)
    private void fillTeamStats() {
        MatchStats home = matchDetails.getHomeStats();
        MatchStats away = matchDetails.getAwayStats();

        if (home == null || away == null) {
            return; // δεν υπάρχουν στατιστικά (π.χ. αγώνας χωρίς events / pending)
        }

        // Κατοχή
        tvHomePossession.setText(home.getPossessionText());
        tvAwayPossession.setText(away.getPossessionText());

        // Συνολικά σουτ
        tvHomeShots.setText(String.valueOf(home.getTotalShots()));
        tvAwayShots.setText(String.valueOf(away.getTotalShots()));

        // Σουτ εντός
        tvHomeShotsOn.setText(String.valueOf(home.getShotsOnTarget()));
        tvAwayShotsOn.setText(String.valueOf(away.getShotsOnTarget()));

        // Πάσες (σύνολο)
        tvHomePasses.setText(String.valueOf(home.getTotalPasses()));
        tvAwayPasses.setText(String.valueOf(away.getTotalPasses()));

        // Ευστοχία πάσας
        tvHomePassAcc.setText(home.getPassAccuracyText());
        tvAwayPassAcc.setText(away.getPassAccuracyText());

        // Επιτυχημένα τάκλιν
        tvHomeTackles.setText(String.valueOf(home.getTacklesSucc()));
        tvAwayTackles.setText(String.valueOf(away.getTacklesSucc()));

        // Κόρνερ
        tvHomeCorners.setText(String.valueOf(home.getCornersWon()));
        tvAwayCorners.setText(String.valueOf(away.getCornersWon()));

        // Φάουλ
        tvHomeFouls.setText(String.valueOf(home.getFoulsCommitted()));
        tvAwayFouls.setText(String.valueOf(away.getFoulsCommitted()));

        // Κίτρινες
        tvHomeYellows.setText(String.valueOf(home.getYellowCards()));
        tvAwayYellows.setText(String.valueOf(away.getYellowCards()));

        // Κόκκινες
        tvHomeReds.setText(String.valueOf(home.getRedCards()));
        tvAwayReds.setText(String.valueOf(away.getRedCards()));

        statsFilled = true;
    }
}