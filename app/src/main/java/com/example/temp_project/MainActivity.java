package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button analystBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        analystBtn = findViewById(R.id.StatisticAnalystbtn);
    }

    public void analystEnter(View v)
    {
        Intent nextActivity = new Intent(this, StatsAnalystConfirmationActivity.class);
        startActivity(nextActivity);
    }

    public void userEnter(View v)
    {
        Intent intent = new Intent(this, StandingsActivity.class);
        startActivity(intent);
    }

    public void helpEnter(View v)
    {
        String helpText = "Καλώς ήρθατε στο Σύστημα Διαχείρισης!\n\n" +
                "Επιλέξτε τον ρόλο σας για να συνδεθείτε:\n\n" +
                "• User (Φίλαθλος): Παρακολούθηση αγώνων, live στατιστικά, βαθμολογίες και 11-άδες.\n\n" +
                "• Statistics Analyst: Διαχείριση κάρτας αγώνα και καταγραφή γεγονότων (σουτ, πάσες, κάρτες κ.λπ.).";

        new AlertDialog.Builder(this)
                .setTitle("Οδηγίες Χρήσης")
                .setMessage(helpText)
                .setPositiveButton("Κατάλαβα", null)
                .setIcon(android.R.drawable.ic_menu_help)
                .show();
    }
}