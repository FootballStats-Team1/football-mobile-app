package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

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
}