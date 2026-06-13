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

            for (ArrayList<PlayerMatchStats> list : lists) {
                for (PlayerMatchStats p : list) {
                    if (p.getName().equals(playerName)) {
                        target = p;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fillStats(target);

        ImageView playerHeader = findViewById(R.id.playerHeader);
        if (target != null) {
            Picasso.with(getApplicationContext())
                    .load(target.getPhoto())
                    .error(R.mipmap.ic_launcher_round)
                    .into(playerHeader);
        } else {
            playerHeader.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void fillStats(PlayerMatchStats p) {
        int goals          = (p != null) ? p.getGoals()          : 0;
        int assists        = (p != null) ? p.getAssists()        : 0;
        int shotsOn        = (p != null) ? p.getShotsOnTarget()  : 0;
        int shotsOff       = (p != null) ? p.getShotsOffTarget() : 0;
        int passesSucc     = (p != null) ? p.getPassesSucc()     : 0;
        int passesFail     = (p != null) ? p.getPassesFail()     : 0;
        int tacklesSucc    = (p != null) ? p.getTacklesSucc()    : 0;
        int tacklesFail    = (p != null) ? p.getTacklesFail()    : 0;
        int crossesSucc    = (p != null) ? p.getCrossesSucc()    : 0;
        int crossesFail    = (p != null) ? p.getCrossesFail()    : 0;
        int errors         = (p != null) ? p.getErrors()         : 0;
        int foulsWon       = (p != null) ? p.getFoulsWon()       : 0;
        int foulsCommitted = (p != null) ? p.getFoulsCommitted() : 0;
        int yellowCards    = (p != null) ? p.getYellowCards()    : 0;
        int redCards       = (p != null) ? p.getRedCards()       : 0;

        ((TextView) findViewById(R.id.goalTxt)).setText(getString(R.string.goals) + ": " + goals);
        ((TextView) findViewById(R.id.assistTxt)).setText(getString(R.string.assists) + ": " + assists);
        ((TextView) findViewById(R.id.shotsOnTxt)).setText(getString(R.string.shots_on_target) + ": " + shotsOn);
        ((TextView) findViewById(R.id.shotsOffTxt)).setText(getString(R.string.shots_off_target) + ": " + shotsOff);
        ((TextView) findViewById(R.id.passesSuccTxt)).setText(getString(R.string.passes_succ) + ": " + passesSucc);
        ((TextView) findViewById(R.id.passesFailTxt)).setText(getString(R.string.passes_fail) + ": " + passesFail);
        ((TextView) findViewById(R.id.tacklesSuccTxt)).setText(getString(R.string.tackles_succ) + ": " + tacklesSucc);
        ((TextView) findViewById(R.id.tacklesFailTxt)).setText(getString(R.string.tackles_fail) + ": " + tacklesFail);
        ((TextView) findViewById(R.id.crossesSuccTxt)).setText(getString(R.string.crosses_succ) + ": " + crossesSucc);
        ((TextView) findViewById(R.id.crossesFailTxt)).setText(getString(R.string.crosses_fail) + ": " + crossesFail);
        ((TextView) findViewById(R.id.errorsTxt)).setText(getString(R.string.errors) + ": " + errors);
        ((TextView) findViewById(R.id.foulsWonTxt)).setText(getString(R.string.fouls_won) + ": " + foulsWon);
        ((TextView) findViewById(R.id.foulsCommittedTxt)).setText(getString(R.string.fouls_committed) + ": " + foulsCommitted);
        ((TextView) findViewById(R.id.yellowCardsTxt)).setText(getString(R.string.yellow_cards) + ": " + yellowCards);
        ((TextView) findViewById(R.id.redCardsTxt)).setText(getString(R.string.red_cards) + ": " + redCards);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}