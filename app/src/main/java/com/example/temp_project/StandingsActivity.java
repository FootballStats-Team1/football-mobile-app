package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Typeface;
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

        // 1. Κατεβάζουμε τα δεδομένα απευθείας
        standingsList = new StandingsList();

        // 2. Προσθέτουμε πρώτα τη γραμμή με τις επικεφαλίδες (header)
        addHeaderRow();

        // 3. Κάνουμε populate τον πίνακα
        populateTable(standingsList.getStandings());
    }

    // --- HEADER ROW ---
    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lp);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.header_blue));


    }

    private void populateTable(ArrayList<Standing> list) {
        int totalTeams = list.size();

        for (int i = 0; i < totalTeams; i++) {
            Standing standing = list.get(i); // Παίρνουμε το Object της ομάδας

            // Ελέγχουμε αν η ομάδα είναι στις 2 τελευταίες θέσεις (relegation zone)
            boolean isRelegation = (i >= totalTeams - 2);

            // 1. Δημιουργία Γραμμής
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            // Χρώμα φόντου της γραμμής
            if (isRelegation) {
                // Οι 2 τελευταίες ομάδες παίρνουν κόκκινο φόντο (relegation)
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.relegation_red));
            } else if (i % 2 == 0) {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.table_row_gray));
            } else {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            }

            // 2. Δημιουργία των κελιών (6 στήλες)

            // Στήλη 1: Θέση
            TextView tvPosition = createTextView(String.valueOf(i + 1), isRelegation);
            tvPosition.setGravity(Gravity.CENTER);
            tvPosition.setTypeface(null, Typeface.BOLD);

            // Στήλη 2: Logo
            ImageView imgLogo = new ImageView(this);

            // Μέγεθος εικόνας 32x32dp
            int imgSize = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            TableRow.LayoutParams imgParams = new TableRow.LayoutParams(imgSize, imgSize);
            imgParams.gravity = Gravity.CENTER;
            imgParams.setMargins(0, 8, 8, 8);
            imgLogo.setLayoutParams(imgParams);

            // Φόρτωση της εικόνας με Picasso
            Picasso.with(getApplicationContext())
                    .load(standing.getLogoUrl())
                    .error(R.mipmap.ic_launcher_round)
                    .into(imgLogo);

            // Στήλη 3: Όνομα Ομάδας
            TextView tvTeam = createTextView(standing.getTeamName(), isRelegation);
            tvTeam.setTypeface(null, Typeface.BOLD);

            // Στήλη 4: Αγώνες
            TextView tvMP = createTextView(String.valueOf(standing.getMatchesPlayed()), isRelegation);
            tvMP.setGravity(Gravity.CENTER);

            // Στήλη 5: Διαφορά Γκολ
            int gd = standing.getGoalDifference();
            String gdText = (gd > 0) ? "+" + gd : String.valueOf(gd);
            TextView tvGD = createTextView(gdText, isRelegation);
            tvGD.setGravity(Gravity.CENTER);

            // Στήλη 6: Πόντοι
            TextView tvPoints = createTextView(String.valueOf(standing.getPoints()), isRelegation);
            tvPoints.setTypeface(null, Typeface.BOLD);
            tvPoints.setGravity(Gravity.CENTER);
            // Στις relegation ομάδες αφήνουμε το λευκό κείμενο, αλλιώς μπλε για τους πόντους
            if (!isRelegation) {
                tvPoints.setTextColor(ContextCompat.getColor(this, R.color.points_blue));
            }

            row.addView(tvPosition);
            row.addView(imgLogo);
            row.addView(tvTeam);
            row.addView(tvMP);
            row.addView(tvGD);
            row.addView(tvPoints);

            leagueTable.addView(row);

            // Λεπτή γραμμή διαχωρισμού ανάμεσα στις γραμμές
            addDivider();
        }
    }

    // --- DIVIDER (λεπτή γραμμή ανάμεσα στις σειρές) ---
    private void addDivider() {
        TableRow dividerRow = new TableRow(this);
        TextView divider = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, 1);
        params.span = 6; // καλύπτει και τις 6 στήλες
        divider.setLayoutParams(params);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider_gray));
        dividerRow.addView(divider);
        leagueTable.addView(dividerRow);
    }

    // --- Κανονικά κελιά δεδομένων ---
    private TextView createTextView(String text, boolean isRelegation) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        // Στις relegation ομάδες το κείμενο είναι λευκό για αντίθεση με το κόκκινο
        if (isRelegation) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.text_dark_gray));
        }

        int paddingInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        return textView;
    }


    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setAllCaps(true);

        int paddingInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        return textView;
    }
}