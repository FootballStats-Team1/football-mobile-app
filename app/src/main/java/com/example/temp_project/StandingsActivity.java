package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StandingsActivity extends AppCompatActivity {

    private StandingsList standingsList;
    private TableLayout leagueTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        // Σύνδεση της μεταβλητής με το UI element
        leagueTable = findViewById(R.id.leagueTable);

        // 1. Κατεβάζουμε τα δεδομένα απευθείας - Δεν χρειάζεται να παίρνει όρισμα ο constructor γιατί έχουμε το URL ως global μεταβλητή
        standingsList = new StandingsList();

        // 2. Κάνουμε populate τον πίνακα
        populateTable(standingsList.getStandings());
    }

    private void populateTable(ArrayList<Standing> list) {
        for (int i = 0; i < list.size(); i++) {
            Standing standing = list.get(i); // Παίρνουμε το Object της ομάδας

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

            // Στήλη 2: Logo
            ImageView imgLogo = new ImageView(this);

            // Μέγεθος εικόνας 28x28dp
            int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            TableRow.LayoutParams imgParams = new TableRow.LayoutParams(imgSize, imgSize);
            imgParams.gravity = Gravity.CENTER;
            imgLogo.setLayoutParams(imgParams);

            // Φόρτωση της εικόνας με Picasso
            Picasso.with(getApplicationContext())
                    .load(standing.getLogoUrl()) // (Με getter από το Object)
                    .error(R.mipmap.ic_launcher_round)  // Αν το link δεν δουλεύει, δείξε το placeholder
                    .into(imgLogo);

            // Στήλη 3: Όνομα Ομάδας (Με getter από το Object)
            TextView tvTeam = createTextView(standing.getTeamName());
            tvTeam.setTypeface(null, android.graphics.Typeface.BOLD);

            // Στήλη 4: Αγώνες (Με ΜΕΘΟΔΟ από το Object, see Standing.java)
            TextView tvMP = createTextView(String.valueOf(standing.getMatchesPlayed()));
            tvMP.setGravity(Gravity.CENTER);

            // Στήλη 5: Διαφορά Γκολ
            int gd = standing.getGoalDifference();
            String gdText = (gd > 0) ? "+" + gd : String.valueOf(gd); // βάζουμε '+' αν είναι θετικό το goal diff, αν είναι αρνητικό το 'χει απο μόνο του το '-'
            TextView tvGD = createTextView(gdText);
            tvGD.setGravity(Gravity.CENTER);

            // Στήλη 6: Πόντοι
            TextView tvPoints = createTextView(String.valueOf(standing.getPoints()));
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
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_dark_gray));

        int paddingInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()); // 12dp padding
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        return textView;
    }
}