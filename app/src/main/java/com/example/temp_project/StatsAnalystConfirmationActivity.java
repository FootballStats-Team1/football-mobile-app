package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StatsAnalystConfirmationActivity extends AppCompatActivity {

    EditText passwordText;

    TextView errorText;
    String enteredPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_analyst_confirmation);

        passwordText = findViewById(R.id.passwordTxt);
        errorText = findViewById(R.id.wrongPasswordTxt);

        errorText.setVisibility(View.INVISIBLE);

    }


    public void validatePassword(View v)
    {
        enteredPassword = passwordText.getText().toString();

            if (enteredPassword.equals("14111926"))
            {
                Intent intent = new Intent(this, StandingsActivity.class);
                startActivity(intent);
            }
            else
            {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Wrong Key. Contact epo@gmail.com to get a key.");
            }

    }
}