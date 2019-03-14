package com.stauss.simon.stundenplan;

import android.annotation.SuppressLint;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main
        extends
            AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            ScheduleOverviewFragment.OnFragmentInteractionListener,
            ScheduleTodayFragment.OnFragmentInteractionListener,
            HomeworkOverviewFragment.OnFragmentInteractionListener {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor prefEdit;

    static FragmentManager fragmentManager;

    public String day;
    String[] days = {"", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
    int dayNr;

    List<String> subjects;
    String subjectString;
    String subjectRegex = ";";

    List<String> homework;
    String homeworkString;
    String homeworkRegex = ";;";
    String homeworkSubregex = ";";
    int homeworkCount;
    boolean sortBySubject;

    static MenuItem homeworkOverviewItem;
    static TextView userName;

    ScheduleTodayFragment scheduleToday = new ScheduleTodayFragment();
    ScheduleOverviewFragment scheduleOverview = new ScheduleOverviewFragment();
    HomeworkOverviewFragment homeworkOverview = new HomeworkOverviewFragment();
    AboutFragment aboutFragment = new AboutFragment();


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_schedule);

        day = getDay();

        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();

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

        homeworkOverviewItem = navigationView.getMenu().findItem(R.id.homeworkOverview);

        if(getHomework().size() == 0) {
            homeworkOverviewItem.setEnabled(false);
        }

        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userName.setText(sharedPreferences.getString("userName", getString(R.string.userName)));

        subjectString = sharedPreferences.getString("subjects", "");
        subjects = getSubjects();

        homeworkCount = getHomeworkCount();
        homeworkString = sharedPreferences.getString("homework", "");
        homework = getHomework();

        openFragment(scheduleToday);
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
        int id = item.getItemId();

        Intent i = new Intent();

        if (id == R.id.scheduleToday) {
            //Open today fragment
            openFragment(scheduleToday);
            setActionBarTitle(getString(R.string.schedule));
        } else if (id == R.id.scheduleOverview) {
            //Open Overview Fragment
            openFragment(scheduleOverview);
            setActionBarTitle(getString(R.string.schedule_overview));
        } else if (id == R.id.scheduleEdit) {
            //Open ScheduleEdit Activity
            i.putExtra("day", getDayNr());
            openActivity(i, ScheduleEdit.class);
        } else if (id == R.id.homeworkAdd) {
            //Open HomeworkAdd Activity
            ArrayList<String> sub = new ArrayList<>(getSubjects());
            i.putStringArrayListExtra("subjects", sub);
            openActivity(i, HomeworkAdd.class);
        } else if (id == R.id.homeworkOverview) {
            //Open HomeworkOverview Fragment
            openFragment(homeworkOverview);
            setActionBarTitle(getString(R.string.homework_overview));
        } else if(id == R.id.settings) {
            //Open Settings Activity
            openActivity(i, SettingsActivity.class);
        } else if(id == R.id.about) {
            //Open About Fragment
            openFragment(aboutFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //No Interaction with Fragments needed
    }

    private void firstLaunch() {
        Intent i = new Intent();
        i.setClass(this, FirstLaunch.class);
        startActivity(i);
    }

    private void openActivity(Intent i, Class c) {
        i.setClass(this, c);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private void openFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.addToBackStack(null);

        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public boolean isWeekend() {
        return getDayNr() >= 6 || getDayNr() == 0;
    }

    public String getDay() {
        if(isWeekend()) {
            day = days[1];
        } else {
            day = days[getDayNr()];
        }
        return day;
    }

    public int getDayNr() {
        Calendar c = Calendar.getInstance();
        dayNr = c.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayNr > 5) {
            dayNr = 1;
        }
        return dayNr;
    }

    public String[] getWeek() {
        return days;
    }


    public void addSubject(String subject) {
        if(!getSubjects().contains(subject)) {
            subjects.add(subject);
            saveSubjects();
        }
    }

    public List<String> getSubjects() {
        subjectString = sharedPreferences.getString("subjects", "");
        subjects = stringToList(subjectString, subjectRegex);
        return subjects;
    }

    public void saveSubjects() {
        subjectString = listToString(subjects, subjectRegex);
        prefEdit.putString("subjects", subjectString);
        prefEdit.apply();
    }

    public void clearSubjects() {
        prefEdit.remove("subjects");
        prefEdit.commit();
    }


    public void addHomework(String subject, String description, String dueTo) {
        String homeworkSubstring = subject + homeworkSubregex + description + homeworkSubregex + dueTo;
        getHomework().add(homeworkSubstring);
        homeworkCount++;
        prefEdit.putInt("homeworkCount", homeworkCount);
        sortHomework();
        saveHomework(homework);
    }

    public List<String> getHomework() {
        homeworkString = sharedPreferences.getString("homework", "");
        homework = stringToList(homeworkString, homeworkRegex);
        return homework;
    }

    public int getHomeworkCount() {
        homeworkCount = sharedPreferences.getInt("homeworkCount", 0);
        return homeworkCount;
    }

    public void saveHomework(List<String> list) {
        if(list.size() == 0) {
            homeworkString = "";
            homeworkOverviewItem.setEnabled(false);
            openFragment(scheduleToday);
        } else {
            homeworkString = listToString(list, homeworkRegex);
        }

        prefEdit.putString("homework", homeworkString);
        prefEdit.commit();
    }

    public void clearHomework() {
        prefEdit.putString("homework", "");
        prefEdit.commit();
    }

    public void sortHomework() {
        Collections.sort(homework, new Comparator<String>() {
            @Override
            public int compare(String h1, String h2) {
                // -1 - less than,
                // 1 - greater than,
                // 0 - equal,
                // all inversed for descending

                String[] part1 = h1.split(homeworkSubregex);
                String[] part2 = h2.split(homeworkSubregex);

                if(!sortBySubject) {
                    Date date1, date2;
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

                    try {
                        date1 = format.parse(part1[2]);
                        date2 = format.parse(part2[2]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }

                    if(date1.after(date2)) {
                        return 1;
                    } else if (date1.before(date2)){
                        return -1;
                    } else {
                        return 0;
                    }
                } else{
                    return part1[0].compareTo(part2[0]);
                }
            }
        });
    }

    public void resetSchedule() {
        for(int d = 1; d <= 5; d++) {
            for(int h = 1; h <= 11; h++) {
                prefEdit.remove(days[d]  + h + "s");
                prefEdit.remove(days[d]  + h + "r");
            }
        }
        prefEdit.commit();
        clearSubjects();
        //restart Acitivity
    }

    public void refreshName() {
        userName.setText(sharedPreferences.getString("userName", ""));
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    public String listToString(List<String> list, String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : list) {
            stringBuilder.append(s);
            stringBuilder.append(regex);
        }
        return stringBuilder.toString();
    }

    public List<String> stringToList(String string, String regex) {
        if(!string.equalsIgnoreCase("")) {
            String[] stringArray = string.split(regex);
            return new ArrayList<>(Arrays.asList(stringArray));
        } else {
            return new ArrayList<>();
        }
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getPrefEdit() {
        return prefEdit;
    }
}