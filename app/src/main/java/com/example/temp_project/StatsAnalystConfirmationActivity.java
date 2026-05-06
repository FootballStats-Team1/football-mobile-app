package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StatsAnalystConfirmationActivity extends AppCompatActivity {

    Button backButton;
    EditText passwordText;

    TextView errorText;
    String enteredPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_analyst_confirmation);

        backButton = findViewById(R.id.backBtn);
        passwordText = findViewById(R.id.passowrdTxt);
        errorText = findViewById(R.id.wrongPasswordTxt);

        errorText.setVisibility(View.INVISIBLE);



    }

    public void backFeature(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void validatePassword(View v)
    {
        enteredPassword = passwordText.getText().toString();
        errorText.setVisibility(View.VISIBLE);

            if (enteredPassword.equals("14111926"))
            {
                // new intent (3rd activity)
                errorText.setText("You may enter.");
            }
            else
            {
                errorText.setText("Wrong Key. Contact epo@gmail.com to get a key.");
            }

    }
}