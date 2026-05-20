package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StandingsActivity extends AppCompatActivity {

    // Δηλώνουμε το TableLayout που έχεις στο XML σου
    TableLayout leagueTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        Intent intent = getIntent();

        // Βρίσκουμε το TableLayout από το id του
        leagueTable = findViewById(R.id.leagueTable);

        // Εκκίνηση της διαδικασίας λήψης δεδομένων
        fetchStandingsFromAPI();
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

    // Η μέθοδος που παίρνει το JSON Array και χτίζει τις γραμμές του πίνακα
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

                // Υπολογισμοί για τις στήλες MP και GD που έχεις στο Header σου
                int mp = wins + draws + losses;
                int gd = goalsFor - goalsAgainst;

                // 1. Φτιάχνουμε μια νέα γραμμή (TableRow)
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                // Προαιρετικό: Εναλλάξ χρώματα στις γραμμές (Zebra effect)
                if (i % 2 == 0) {
                    row.setBackgroundColor(Color.parseColor("#E3F2FD")); // Απαλό μπλε (ταιριάζει με το θέμα σου)
                } else {
                    row.setBackgroundColor(Color.parseColor("#FFFFFF")); // Λευκό
                }

                // 2. Φτιάχνουμε τα 5 κελιά (TextViews) της γραμμής
                TextView tvPosition = createTextView(String.valueOf(i + 1));
                TextView tvTeam = createTextView(teamName);

                TextView tvMP = createTextView(String.valueOf(mp));
                // Στοίχιση στο κέντρο για το MP
                tvMP.setGravity(Gravity.CENTER);

                String gdText = (gd > 0) ? "+" + gd : String.valueOf(gd);
                TextView tvGD = createTextView(gdText);
                // Στοίχιση στο κέντρο για το GD
                tvGD.setGravity(Gravity.CENTER);

                TextView tvPoints = createTextView(String.valueOf(points));
                tvPoints.setTypeface(null, android.graphics.Typeface.BOLD); // Οι πόντοι Bold
                // Στοίχιση στο κέντρο για το PTS
                tvPoints.setGravity(Gravity.CENTER);

                // 3. Προσθέτουμε τα κελιά στη γραμμή με τη σειρά που τα έβαλες στο XML
                row.addView(tvPosition);
                row.addView(tvTeam);
                row.addView(tvMP);
                row.addView(tvGD);
                row.addView(tvPoints);

                // 4. Προσθέτουμε τη γραμμή στον κεντρικό Πίνακα
                leagueTable.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Ελαφρώς μικρότερο από το Header
        textView.setTextColor(Color.BLACK);

        // 4dp padding (μετατροπή σε pixels)
        int paddingInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        return textView;
    }
}