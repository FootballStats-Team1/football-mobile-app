package com.example.temp_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Μία οθόνη, ΠΟΛΛΟΙ πίνακες (ένας ανά στατιστικό), ο ένας κάτω από τον άλλον.
// Container: ScrollView -> LinearLayout (statsContainer). Σε αυτό ρίχνουμε
// ένα ζευγάρι "τίτλος + TableLayout" για κάθε στατιστικό.
public class TopStatsActivity extends AppCompatActivity {

    private LinearLayout statsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_stats);

        setTitle("Στατιστικά Παικτών");

        statsContainer = findViewById(R.id.statsContainer);

        // Ένας πίνακας για κάθε στατιστικό. Πρόσθεσε/αφαίρεσε γραμμές εδώ.
        addStatSection("Πρώτοι Σκόρερ",     "goals",        "Γκολ");
        addStatSection("Πρώτοι σε Ασίστ",   "assists",      "Ασίστ");
        addStatSection("Κίτρινες Κάρτες",   "yellow_cards", "Κίτρ.");
        addStatSection("Κόκκινες Κάρτες",   "red_cards",    "Κόκ.");
    }

    // --- Φτιάχνει ΕΝΑ ολόκληρο section: τίτλος + πίνακας για το συγκεκριμένο stat ---
    private void addStatSection(String title, String stat, String label) {

        // 1. Τίτλος ενότητας
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.points_blue));
        int pad = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        tvTitle.setPadding(pad, pad, pad, pad / 2);
        statsContainer.addView(tvTitle);

        // 2. Ο πίνακας αυτού του stat
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.setColumnStretchable(2, true); // η στήλη ονόματος (index 2) τραβιέται

        // 3. Κατέβασμα δεδομένων (ίδια λογική με το StandingsList)
        ArrayList<TopStat> list = new TopStatsList(stat).getTopStats();

        // 4. Γέμισμα του πίνακα
        addHeaderRow(table, label);
        populateTable(table, list);

        statsContainer.addView(table);
    }

    // --- HEADER ROW (τώρα παίρνει τον πίνακα ως παράμετρο) ---
    private void addHeaderRow(TableLayout table, String label) {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.header_blue));

        headerRow.addView(createHeaderTextView("#"));
        headerRow.addView(createHeaderTextView(""));        // φωτό
        headerRow.addView(createHeaderTextView("Παίκτης"));
        headerRow.addView(createHeaderTextView(""));        // σήμα ομάδας
        headerRow.addView(createHeaderTextView(label));

        table.addView(headerRow);
        addDivider(table);
    }

    private void populateTable(TableLayout table, ArrayList<TopStat> list) {
        for (int i = 0; i < list.size(); i++) {
            TopStat item = list.get(i);

            TableRow row = new TableRow(this);
            if (i % 2 == 0) {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.table_row_gray));
            } else {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            }

            int imgSize = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());

            // Στήλη 1: Κατάταξη
            TextView tvRank = createTextView(String.valueOf(i + 1));
            tvRank.setGravity(Gravity.CENTER);
            tvRank.setTypeface(null, Typeface.BOLD);

            // Στήλη 2: Φωτό παίκτη
            ImageView imgPhoto = new ImageView(this);
            TableRow.LayoutParams photoParams = new TableRow.LayoutParams(imgSize, imgSize);
            photoParams.gravity = Gravity.CENTER;
            photoParams.setMargins(8, 8, 8, 8);
            imgPhoto.setLayoutParams(photoParams);
            Picasso.with(getApplicationContext())
                    .load(item.photo)
                    .error(R.mipmap.ic_launcher_round)
                    .into(imgPhoto);

            // Στήλη 3: Όνομα
            TextView tvName = createTextView(item.name);
            tvName.setTypeface(null, Typeface.BOLD);

            // Στήλη 4: Σήμα ομάδας
            ImageView imgBadge = new ImageView(this);
            TableRow.LayoutParams badgeParams = new TableRow.LayoutParams(imgSize, imgSize);
            badgeParams.gravity = Gravity.CENTER;
            badgeParams.setMargins(8, 8, 8, 8);
            imgBadge.setLayoutParams(badgeParams);
            Picasso.with(getApplicationContext())
                    .load(item.badge)
                    .error(R.mipmap.ic_launcher_round)
                    .into(imgBadge);

            // Στήλη 5: Ο αριθμός
            TextView tvTotal = createTextView(String.valueOf(item.total));
            tvTotal.setGravity(Gravity.CENTER);
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(ContextCompat.getColor(this, R.color.points_blue));

            row.addView(tvRank);
            row.addView(imgPhoto);
            row.addView(tvName);
            row.addView(imgBadge);
            row.addView(tvTotal);

            table.addView(row);
            addDivider(table);
        }
    }

    private void addDivider(TableLayout table) {
        TableRow dividerRow = new TableRow(this);
        TextView divider = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, 1);
        params.span = 5; // 5 στήλες
        divider.setLayoutParams(params);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider_gray));
        dividerRow.addView(divider);
        table.addView(dividerRow);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(this, R.color.text_dark_gray));
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