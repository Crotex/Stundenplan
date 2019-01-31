package com.stauss.simon.stundenplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainSchedule extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ScheduleOverviewFragment.OnFragmentInteractionListener, ScheduleTodayFragment.OnFragmentInteractionListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public String day;
    String[] days = {"", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
    int dayNr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_schedule);

        day = getDay();

        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();

        boolean firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
        if(firstLaunch) {
            firstLaunch();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.userName);
        name.setText(sharedPreferences.getString("userName", getString(R.string.userName)));

        openFragment(new ScheduleTodayFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_schedule, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent i = new Intent();

        if (id == R.id.scheduleToday) {
            //Open today fragment
            openFragment(new ScheduleTodayFragment());
        } else if (id == R.id.scheduleOverview) {
            //Open Overview Fragment
            openFragment(new ScheduleOverviewFragment());
        } else if (id == R.id.scheduleEdit) {
            i.putExtra("day", getDayNr());
            openActivity(i, ScheduleEdit.class);
        } else if (id == R.id.homeworkAdd) {

        } else if (id == R.id.homeworkOverview) {

        } else if(id == R.id.settings) {

        } else {
            return false;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void firstLaunch() {
        Intent i = new Intent();
        i.setClass(this, FirstLaunch.class);
        startActivity(i);

    }

    private void openActivity(Intent i, Class c) {
        i.setClass(this, c);
        startActivity(i);
    }

    private void openFragment(Fragment f) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.addToBackStack(null);

        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public String getDay() {
        day = days[getDayNr()];
        return day;
    }

    public int getDayNr() {
        dayNr = Integer.parseInt(new SimpleDateFormat("u").format(new Date()));
        return dayNr;
    }

    public String[] getWeek() {
        return days;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //No Interaction with Fragments needed
    }
}