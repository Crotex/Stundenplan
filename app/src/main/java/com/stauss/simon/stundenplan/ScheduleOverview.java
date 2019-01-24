package com.stauss.simon.stundenplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScheduleOverview extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    String[] days;

    ScheduleToday main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_overview);


        days = getMain().getWeek();

        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(getMain());

        buildTable((TableLayout) findViewById(R.id.table));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void buildTable(TableLayout table) {
        TableRow row;
        int c;
        for (int i = 1; i <= 11; i++) {
            c = 0;

            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 5, 0, 5);

            TextView h = new TextView(this);
            h.setText("" + i);
            h.setGravity(Gravity.CENTER);
            row.addView(h, new TableRow.LayoutParams(c));
            c++;

            TextView subMo = new TextView(this);
            subMo.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subMo.setGravity(Gravity.CENTER);
            row.addView(subMo, new TableRow.LayoutParams(c));
            c++;

            TextView subDi = new TextView(this);
            subDi.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subDi.setGravity(Gravity.CENTER);
            row.addView(subDi, new TableRow.LayoutParams(c));
            c++;

            TextView subMi = new TextView(this);
            subMi.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subMi.setGravity(Gravity.CENTER);
            row.addView(subMi, new TableRow.LayoutParams(c));
            c++;

            TextView subDo = new TextView(this);
            subDo.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subDo.setGravity(Gravity.CENTER);
            row.addView(subDo, new TableRow.LayoutParams(c));
            c++;

            TextView subFr = new TextView(this);
            subFr.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subFr.setGravity(Gravity.CENTER);
            row.addView(subFr, new TableRow.LayoutParams(c));

            table.addView(row);
        }
    }

    private ScheduleToday getMain() {
        main = new ScheduleToday();
        return main;
    }
}
