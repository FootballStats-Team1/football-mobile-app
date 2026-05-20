package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StandingsActivity extends AppCompatActivity {

    TableLayout leagueTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        // Ενεργοποίηση του βέλους επιστροφής (Back Arrow)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        leagueTable = findViewById(R.id.leagueTable);

        // Εκκίνηση της διαδικασίας λήψης δεδομένων
        fetchStandingsFromAPI();
    }

    // Λειτουργικότητα του βέλους επιστροφής
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchStandingsFromAPI() {
        OkHttpHandler handler = new OkHttpHandler();

        handler.getStandings(new OkHttpHandler.StandingsCallback() {
            @Override
            public void onSuccess(JSONArray standings) {
                runOnUiThread(() -> populateTable(standings));
            }

            @Override
            public void onError(String message) {
                Log.e("API_ERROR", message);
                runOnUiThread(() -> Toast.makeText(
                        StandingsActivity.this,
                        "Σφάλμα Δικτύου!",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void populateTable(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String teamName = obj.getString("team_name");
                int points = obj.getInt("points");
                int wins = obj.getInt("wins");
                int draws = obj.getInt("draws");
                int losses = obj.getInt("losses");
                int goalsFor = obj.getInt("goals_for");
                int goalsAgainst = obj.getInt("goals_against");

                int mp = wins + draws + losses;
                int gd = goalsFor - goalsAgainst;

                // 1. Δημιουργία Γραμμής
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0) {
                    row.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.table_row_gray));
                } else {
                    row.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
                }

                // 2. Δημιουργία των κελιών (6 στήλες)

                // Στήλη 1: Θέση
                TextView tvPosition = createTextView(String.valueOf(i + 1));
                tvPosition.setGravity(Gravity.CENTER);

                // Στήλη 2: Logo Ομάδας (Χρησιμοποιώ προσωρινά το logo του Android)
                ImageView imgLogo = new ImageView(this);
                imgLogo.setImageResource(R.mipmap.ic_launcher_round);
                // Μέγεθος εικόνας 24x24dp
                int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
                TableRow.LayoutParams imgParams = new TableRow.LayoutParams(imgSize, imgSize);
                imgParams.gravity = Gravity.CENTER;
                imgLogo.setLayoutParams(imgParams);

                // Στήλη 3: Όνομα Ομάδας
                TextView tvTeam = createTextView(teamName);
                tvTeam.setTypeface(null, android.graphics.Typeface.BOLD);

                // Στήλη 4: Αγώνες
                TextView tvMP = createTextView(String.valueOf(mp));
                tvMP.setGravity(Gravity.CENTER);

                // Στήλη 5: Διαφορά Γκολ
                String gdText = (gd > 0) ? "+" + gd : String.valueOf(gd);
                TextView tvGD = createTextView(gdText);
                tvGD.setGravity(Gravity.CENTER);

                // Στήλη 6: Πόντοι
                TextView tvPoints = createTextView(String.valueOf(points));
                tvPoints.setTypeface(null, android.graphics.Typeface.BOLD);
                tvPoints.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.points_blue));
                tvPoints.setGravity(Gravity.CENTER);

                row.addView(tvPosition);
                row.addView(imgLogo);
                row.addView(tvTeam);
                row.addView(tvMP);
                row.addView(tvGD);
                row.addView(tvPoints);

                leagueTable.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); //
        textView.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_dark_gray));

        int paddingInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        return textView;
    }
}