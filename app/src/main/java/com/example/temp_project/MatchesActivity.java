package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MatchesActivity extends AppCompatActivity {

    private Spinner spinnerMatchday;
    private LinearLayout matchesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        setTitle(getString(R.string.title_matches));

        spinnerMatchday = findViewById(R.id.spinnerMatchday);
        matchesContainer = findViewById(R.id.matchesContainer);

        // Γέμισμα του Spinner
        populateSpinner();
    }

    private void populateSpinner() {
        ArrayList<String> matchdays = new ArrayList<>();
        String prefix = getString(R.string.matchday_prefix);

        // Βάζουμε HARDCODED τον αριθμό των αγωνιστικών (9)
        for (int i = 1; i <= 9; i++) {
            matchdays.add(prefix + " " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, matchdays){
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // HARDCODED, ξέρουμε ότι η 4η αγωνιστική (index 3) είναι η live!
                if (position == 3) {
                    tv.setTextColor(android.graphics.Color.RED);
                    tv.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    tv.setTextColor(android.graphics.Color.BLACK);
                    tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatchday.setAdapter(adapter);

        // Τι γίνεται όταν ο χρήστης διαλέγει αγωνιστική (using onItemSelected interface)
        spinnerMatchday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedMatchday = position + 1; // Το index 0 είναι η 1η αγωνιστική

                // Καλούμε τη μέθοδο και μαθαίνουμε αν η αγωνιστική που επιλέχθηκε είναι Live
                boolean isLive = loadMatchesForRound(selectedMatchday);
                if (view instanceof TextView) {
                    TextView spinnerTextView = (TextView) view;

                    if (isLive) {
                        spinnerTextView.setTextColor(android.graphics.Color.RED);
                        spinnerTextView.setTypeface(null, android.graphics.Typeface.BOLD);
                    } else {
                        spinnerTextView.setTextColor(android.graphics.Color.BLACK);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private boolean loadMatchesForRound(int matchday) {
        // Καθαρίζουμε την οθόνη από τους αγώνες της προηγούμενης επιλογής
        matchesContainer.removeAllViews();

        // Καλούμε την υπηρεσία - ΠΡΟΣΟΧΗ η υπηρεσία 'getMatches.php' παίρνει ως GET attribute την αγωνιστική! (π.χ. 'getMatches.php?matchday=3')
        MatchesList listManager = new MatchesList(matchday);
        ArrayList<Match> matches = listManager.getMatches();

        boolean isRoundLive = false; // Μεταβλητή για το αν η αγωνιστική έχει LIVE αγώνα

        // Σχεδιάζουμε τον κάθε αγώνα
        for (Match match : matches) {

            // Inflate το XML του αγώνα που φτιάξαμε
            View matchRow = getLayoutInflater().inflate(R.layout.item_match, matchesContainer, false);

            // Βρίσκουμε τα στοιχεία ΜΕΣΑ σε αυτή τη γραμμή
            TextView tvHome = matchRow.findViewById(R.id.tvHomeTeam);
            TextView tvAway = matchRow.findViewById(R.id.tvAwayTeam);
            TextView tvScore = matchRow.findViewById(R.id.tvScore);
            ImageView imgHome = matchRow.findViewById(R.id.imgHomeLogo);
            ImageView imgAway = matchRow.findViewById(R.id.imgAwayLogo);

            tvHome.setText(match.getHomeTeam());
            tvAway.setText(match.getAwayTeam());
            // Έλεγχος αν ο αγώνας είναι Live
            if ("live".equalsIgnoreCase(match.getStatus())) {
                isRoundLive = true; // Βρήκαμε live αγώνα, άρα όλη η αγωνιστική είναι live!
                tvScore.setTextColor(android.graphics.Color.RED);
                tvScore.setText(capitalizeStatus(match.getStatus()) + "\n" + match.getScoreText() );
            } else {
                tvScore.setText(match.getScoreText());
                tvScore.setTextColor(Color.BLACK);
            }
            Picasso.with(getApplicationContext()).load(match.getHomeLogo()).error(R.mipmap.ic_launcher_round).into(imgHome);
            Picasso.with(getApplicationContext()).load(match.getAwayLogo()).error(R.mipmap.ic_launcher_round).into(imgAway);

            // Τι γίνεται όταν πατάει πάνω σε έναν αγώνα
            matchRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Φτιάχνουμε το Intent για την επόμενη οθόνη (Τα στατιστικά)
                    Intent intent = new Intent(MatchesActivity.this, MatchDetailsActivity.class);
                    // "Πακετάρουμε" το ID του αγώνα για να ξέρει η επόμενη οθόνη τι να ψάξει
                    intent.putExtra("MATCH_ID", match.getMatchId());
                    startActivity(intent);
                }
            });

            // Προσθέτουμε το row στο κεντρικό UI
            matchesContainer.addView(matchRow);
        }

        // Επιστρέφουμε το αποτέλεσμα αν η αγωνιστική είναι live ή όχι
        return isRoundLive;
    }

    private String capitalizeStatus(String status) {
        if (status == null || status.isEmpty()) return "";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
}