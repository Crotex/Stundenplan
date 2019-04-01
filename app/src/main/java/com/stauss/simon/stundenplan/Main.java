package com.stauss.simon.stundenplan;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

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
            NavigationView.OnNavigationItemSelectedListener {


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

        //  Loading SharedPreferences (= Config)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();

        //  Is this the first launch? Execute firstLaunch()
        boolean firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
        if(firstLaunch) {
            firstLaunch();
        }

        //  Loading Layout Components
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        day = getDay();

        //  Reading username from config and setting the text of the label to the name
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userName.setText(sharedPreferences.getString("userName", getString(R.string.userName)));

        //  Loading subjects from config
        subjectString = sharedPreferences.getString("subjects", "");
        subjects = getSubjects();

        //  Loading homework from Config
        homeworkCount = getHomeworkCount();
        homeworkString = sharedPreferences.getString("homework", "");
        homework = getHomework();

        //  Initializing HomeworkOverview MenuItem
        homeworkOverviewItem = navigationView.getMenu().findItem(R.id.homeworkOverview);

        //  Disable said Item if there is no homework present
        if(getHomework().size() == 0) {
            homeworkOverviewItem.setEnabled(false);
        }

        // Create notification channel
        createNotificationChannel();

        // Initialize Alarms
        initializeAlarms();

        //  Open fragment with the schedule of today
        openFragment(scheduleToday);
    }

    @Override
    public void onBackPressed() {
        //  Close NavigationDrawer if back-key is pressed
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_schedule, menu);
        return true;
    }

    //  This method is called once a MenuItem is seletected
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent i = new Intent();

        if (id == R.id.scheduleToday) {
            //  Open fragment with daily schedule
            openFragment(scheduleToday);
            setActionBarTitle(getString(R.string.schedule));
        } else if (id == R.id.scheduleOverview) {
            //  Open ScheduleOverview fragment
            openFragment(scheduleOverview);
            setActionBarTitle(getString(R.string.schedule_overview));
        } else if (id == R.id.scheduleEdit) {
            //  Open ScheduleEdit Activity, put current day
            i.putExtra("day", getDayNr());
            openActivity(i, ScheduleEdit.class);
        } else if (id == R.id.homeworkAdd) {
            //  Open HomeworkAdd Activity, put subjects
            ArrayList<String> sub = new ArrayList<>(getSubjects());
            i.putStringArrayListExtra("subjects", sub);
            openActivity(i, HomeworkAdd.class);
        } else if (id == R.id.homeworkOverview) {
            //  Open HomeworkOverview
            openFragment(homeworkOverview);
            setActionBarTitle(getString(R.string.homework_overview));
        } else if(id == R.id.settings) {
            //  Open Settings
            openActivity(i, SettingsActivity.class);
        } else if(id == R.id.about) {
            // Open AboutFragment
            openFragment(aboutFragment);
        }

        // Close Navigation with an animation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This is the first Time the user launched the app -> open FirstLaunch Activity
    private void firstLaunch() {
        Intent i = new Intent();
        i.setClass(this, FirstLaunch.class);
        startActivity(i);
    }

    // Open new Activity (Class c) with a provided Intent (Intent i)
    private void openActivity(Intent i, Class c) {
        i.setClass(this, c);
        // No opening-animation
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    // This method replaced the Apps display content with the new Fragment f
    private void openFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, f); // "Container" is part of the layout and will be replaced by the fragments layout
        fragmentTransaction.addToBackStack(null);

        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    //  This method determines whether its weekend or not
    public boolean isWeekend() {
        return getDayNr() == 6 || getDayNr() == 0;
    }

    // This method returns the current day
    public String getDay() {
        if(isWeekend()) {
            day = days[1]; // If the app is launched on a weekend (Saturday/Sunday) next mondays schedule will be opened
        } else {
            day = days[getDayNr()]; // Else return the current day
        }
        return day;
    }

    //  Order of the days according to the Calendar library:
    // Sunday(0), Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6)
    //  This method return the current DayNr (see above)
    public int getDayNr() {
        Calendar c = Calendar.getInstance();
        dayNr = c.get(Calendar.DAY_OF_WEEK) - 1;
        return dayNr;
    }

    // This method returns days of the week as Array
    public String[] getWeek() {
        return days;
    }

    // This method adds a subject to the List unless it hasn't been added yet 
    public void addSubject(String subject) {
        if(!getSubjects().contains(subject)) {
            subjects.add(subject);
            saveSubjects();
        }
    }

    // This method loads subjects as a String from config and converts it into a List
    public List<String> getSubjects() {
        subjectString = sharedPreferences.getString("subjects", "");
        subjects = stringToList(subjectString, subjectRegex);
        return subjects;
    }

    // This method saves subjects as a String in the config 
    public void saveSubjects() {
        subjectString = listToString(subjects, subjectRegex);
        prefEdit.putString("subjects", subjectString);
        prefEdit.apply();
    }

    // This method removes all subjects
    public void clearSubjects() {
        prefEdit.remove("subjects");
        prefEdit.commit();
    }


    // This method adds a new homework and sorts the List again
    public void addHomework(String subject, String description, String dueTo) {
        String homeworkSubstring = subject + homeworkSubregex + description + homeworkSubregex + dueTo;
        getHomework().add(homeworkSubstring);
        homeworkCount++;
        prefEdit.putInt("homeworkCount", homeworkCount);
        sortHomework();
        saveHomework(homework);
    }

    // This method loads homework as a String from the Config and converts it into a List
    public List<String> getHomework() {
        homeworkString = sharedPreferences.getString("homework", "");
        homework = stringToList(homeworkString, homeworkRegex);
        return homework;
    }

    // This method return the homeworkCount from the Config
    public int getHomeworkCount() {
        homeworkCount = sharedPreferences.getInt("homeworkCount", 0);
        return homeworkCount;
    }

    // This method saves homework as a String in the Config
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

    // This method removes all homework
    public void clearHomework() {
        prefEdit.remove("homework");
        prefEdit.commit();
    }

    // This method sorts homework according to the date (for now)
    public void sortHomework() {
        Collections.sort(homework, new Comparator<String>() {
            @Override
            public int compare(String h1, String h2) {
                //  -1 = smaller,
                //  1  = bigger,
                //  0  = equal,
                //  inverted for descending

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

    // This method resets the entire schedule
    public void resetSchedule() {
        for(int d = 1; d <= 5; d++) {
            for(int h = 1; h <= 11; h++) {
                prefEdit.remove(days[d]  + h + "s");
                prefEdit.remove(days[d]  + h + "r");
            }
        }
        prefEdit.commit();
        clearSubjects();

        //  Restarting the Activity
        try {
            recreate();
        } catch (NullPointerException e) {
            Intent i = new Intent();
            i.setClass(Main.this, Main.class); // TODO: Context
            startActivity(i);
        }

    }

    // This method refreshed the userName
    public void refreshName() {
        userName.setText(sharedPreferences.getString("userName", ""));
    }

    // This method changes the title of the ActionBar
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.schedule);
            String description = getString(R.string.notification_channel_desc);
            NotificationChannel channel = new NotificationChannel("default", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // This method sets up the alarms triggering the notification
    private void initializeAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar c = Calendar.getInstance();

        // Setup Notifications for each day
        for(int i = 1; i <= 5; i++) {
            int h = getSharedPreferences().getInt("scheduleNotificationHour", 7);
            int m = getSharedPreferences().getInt("scheduleNotificationMinute", 0);

            c.set(Calendar.DAY_OF_WEEK, i+1);
            c.set(Calendar.HOUR_OF_DAY, h);
            c.set(Calendar.MINUTE, m);
            c.set(Calendar.SECOND, 0);

            Date date = c.getTime();

            // This Intent will be opened when the alarm fires
            // -> onReceive() in NotificationReceiver will be called
            Intent intent = new Intent(this, NotificationReceiver.class);

            // Put dayNr as extra
            // 1 = Monday ... 5 = Friday
            intent.putExtra("day", i);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

            // Set weekly (7* daily interval) repeating alarm for the specified date executing the pendingIntent
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }

    // This method splits List at provided regex and converts it into a String
    public String listToString(List<String> list, String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : list) {
            stringBuilder.append(s);
            stringBuilder.append(regex);
        }
        return stringBuilder.toString();
    }

    // This method splits String at provided regex and returns it as a List
    public List<String> stringToList(String string, String regex) {
        if(!string.equalsIgnoreCase("")) {
            String[] stringArray = string.split(regex);
            return new ArrayList<>(Arrays.asList(stringArray));
        } else {
            return new ArrayList<>();
        }
    }

    // This method return the Config (static)
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    // This method returns the ConfigEditor (static)
    public SharedPreferences.Editor getPrefEdit() {
        return prefEdit;
    }
}