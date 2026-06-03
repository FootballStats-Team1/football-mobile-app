package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonalStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_stats);

        int matchId = getIntent().getIntExtra("MATCH_ID", -1);
        String playerName = getIntent().getStringExtra("PLAYER_NAME");



        ((TextView) findViewById(R.id.playerNameTxt)).setText(playerName);

        PlayerMatchStats target = null;
        try {
            OkHttpHandler handler = new OkHttpHandler();
            ArrayList<ArrayList<PlayerMatchStats>> lists = handler.getMatchPlayerStats(
                    Config.BASE_URL + "getMatchPlayerStats.php?matchId=" + matchId);

            // Πόσους παίκτες γύρισε το endpoint;


            for (ArrayList<PlayerMatchStats> list : lists) {
                for (PlayerMatchStats p : list) {
                    if (p.getName().equals(playerName)) {
                        target = p;
                        break;
                    }
                }
            }

            // ΤΩΡΑ λογάρουμε το target — ΜΕΤΑ το loop

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (target != null) {
            fillStats(target);

            ImageView playerHeader = findViewById(R.id.playerHeader);
            Picasso.with(getApplicationContext())
                    .load(target.getPhoto())
                    .error(R.mipmap.ic_launcher_round)
                    .into(playerHeader);
        }
    }

    private void fillStats(PlayerMatchStats p) {
        ((TextView) findViewById(R.id.goalTxt)).setText(getString(R.string.goals) + ": " + p.getGoals());
        ((TextView) findViewById(R.id.assistTxt)).setText(getString(R.string.assists) + ": " + p.getAssists());
        ((TextView) findViewById(R.id.shotsOnTxt)).setText(getString(R.string.shots_on_target) + ": " + p.getShotsOnTarget());
        ((TextView) findViewById(R.id.shotsOffTxt)).setText(getString(R.string.shots_off_target) + ": " + p.getShotsOffTarget());
        ((TextView) findViewById(R.id.passesSuccTxt)).setText(getString(R.string.passes_succ) + ": " + p.getPassesSucc());
        ((TextView) findViewById(R.id.passesFailTxt)).setText(getString(R.string.passes_fail) + ": " + p.getPassesFail());
        ((TextView) findViewById(R.id.tacklesSuccTxt)).setText(getString(R.string.tackles_succ) + ": " + p.getTacklesSucc());
        ((TextView) findViewById(R.id.tacklesFailTxt)).setText(getString(R.string.tackles_fail) + ": " + p.getTacklesFail());
        ((TextView) findViewById(R.id.crossesSuccTxt)).setText(getString(R.string.crosses_succ) + ": " + p.getCrossesSucc());
        ((TextView) findViewById(R.id.crossesFailTxt)).setText(getString(R.string.crosses_fail) + ": " + p.getCrossesFail());
        ((TextView) findViewById(R.id.errorsTxt)).setText(getString(R.string.errors) + ": " + p.getErrors());
        ((TextView) findViewById(R.id.foulsWonTxt)).setText(getString(R.string.fouls_won) + ": " + p.getFoulsWon());
        ((TextView) findViewById(R.id.foulsCommittedTxt)).setText(getString(R.string.fouls_committed) + ": " + p.getFoulsCommitted());
       // ((TextView) findViewById(R.id.cornersWonTxt)).setText(getString(R.string.corners_won) + ": " + p.getCornersWon());
        ((TextView) findViewById(R.id.yellowCardsTxt)).setText(getString(R.string.yellow_cards) + ": " + p.getYellowCards());
        ((TextView) findViewById(R.id.redCardsTxt)).setText(getString(R.string.red_cards) + ": " + p.getRedCards());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}