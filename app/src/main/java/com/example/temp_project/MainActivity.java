package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_main);
    }


    public void enterButton(View v)
    {
        Intent intent = new Intent(this, AppMenuActivity.class);
        startActivity(intent);
    }

    public void helpEnter(View v)
    {
        String helpTitle = getString(R.string.help_title);
        String helpText = getString(R.string.help_message);
        String btnText = getString(R.string.help_understood);

        new AlertDialog.Builder(this)
                .setTitle(helpTitle)
                .setMessage(helpText)
                .setPositiveButton(btnText, null)
                .setIcon(android.R.drawable.ic_menu_help)
                .show();
    }

    public void changeLanguage(View v) {
        String currentLang = getResources().getConfiguration().locale.getLanguage();

        String targetEn = getString(R.string.lang_en);
        String targetEl = getString(R.string.lang_el);

        String newLang = currentLang.equals(targetEn) ? targetEl : targetEn;

        java.util.Locale locale = new java.util.Locale(newLang);
        java.util.Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        recreate();
    }
}
