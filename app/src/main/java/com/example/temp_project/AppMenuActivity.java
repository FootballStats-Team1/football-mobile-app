package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AppMenuActivity extends AppCompatActivity {

    LinearLayout analystSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_menu);

        analystSection = findViewById(R.id.analystSection);

        boolean isAnalyst = getIntent().getBooleanExtra("IS_ANALYST", false);

        if (!isAnalyst) {
            analystSection.setVisibility(View.GONE);
        }
    }

    public void manageMatchEnter(View v) {
        // Θα συνδεθεί με την οθόνη Διαχείρισης Αγώνα (Requirements R1, R2)
    }

    public void standingsEnter(View v) {
        Intent intent = new Intent(this, StandingsActivity.class);
        startActivity(intent);
    }

    public void matchesEnter(View v) {
        // Θα συνδεθεί με την οθόνη Παρακολούθησης/Στατιστικών Αγώνων (Requirements R3, R4)
        Intent intent = new Intent(this, MatchesActivity.class);
        startActivity(intent);
    }

    public void playerStatsEnter(View v) {
        // Θα συνδεθεί με την οθόνη Ατομικών/Ομαδικών Στατιστικών (Requirement R5)
    }

}