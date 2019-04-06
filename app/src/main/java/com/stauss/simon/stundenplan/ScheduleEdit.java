package com.stauss.simon.stundenplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleEdit extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    int day;
    String[] days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);

        days = getMain().getWeek();

        sharedPreferences = getMain().getSharedPreferences();
        prefEdit = getMain().getPrefEdit();

        // Get Current DayNr (1 = Monday, 5 = Friday
        day = getIntent().getIntExtra("day", 1);

        //Is it weekend?
        if(day > 5) {
            day = 1;
        }


        // Is it Friday? -> remove "Weiter" Button (-> No saturday Schedule)
        // Is it Monday? -> remove "Zurück" Button
        if(day == 5) {
            findViewById(R.id.finishedButton).setVisibility(View.INVISIBLE);
            Button b = findViewById(R.id.submitButton);
            b.setText("Fertig");
        } else if (day == 1) {
            findViewById(R.id.backButton).setVisibility(View.GONE);
        }

        // Header text with corresponding day
        TextView header = findViewById(R.id.header);
        header.setText(getString(R.string.schedule_edit_header).replace("%DAY%", days[day]));

        buildTable((TableLayout)findViewById(R.id.table));

        // Submit ("Weiter") button Clicked
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                if(prefEdit.commit()) {
                    Intent i = new Intent();
                    // Is it not Friday? -> Open ScheduleEdit for tomorrow, else open Main Acitity
                    if(day <= 4) {
                        i.putExtra("day", day + 1);
                        openActivity(i, ScheduleEdit.class);
                    } else {
                        openActivity(i, Main.class);
                    }

                }
            }
        });

        // Back ("Zurück") button clicked
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                if(prefEdit.commit()) {
                    Intent i = new Intent();
                    // Open ScheduleEdit for yesterday
                    i.putExtra("day", day - 1);
                    openActivity(i, ScheduleEdit.class);
                }
            }
        });

        // Finished ("Fertig") button clicked
        findViewById(R.id.finishedButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                if(prefEdit.commit()) {
                    // Open Main Activity
                    openActivity(new Intent(), Main.class);
                }
            }
        });
    }

    private void buildTable(TableLayout tableLayout) {
        // User isn't allowed to use ";" since it'll corrupt the saving and restoring process (";" is used as regex to split items)
        InputFilter filter = new InputFilter() {
            String blockedCharacters = getString(R.string.blocked_characters);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && blockedCharacters.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        // Repeat this for each hour (1-11)
        for (int i = 1; i <= 11; i++) {
            // Create a new row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 5, 0, 5);

            // Create new TextView for each hour
            TextView h = new TextView(this);
            h.setText("" + i);
            h.setGravity(Gravity.CENTER);

            // Create new EditText representing subject input
            String subject;
            EditText sub = new EditText(this);

            // Ids 21-32 represent subjects, needed later on for retrieving the data
            sub.setId(20+i);
            sub.setHint("Fach" + i);
            sub.setFilters(new InputFilter[] {filter});

            // Key for subject e. g. Montag11s (Day + hour + s)
            subject = sharedPreferences.getString(days[day] + i + "s", "");

            // Remove "-" to make editing schedule easier
            // "-" appear after the Schedule has been saved once already
            if(subject.equalsIgnoreCase("-")) {
                sub.setText("");
            } else {
                sub.setText(subject);
            }

            sub.setGravity(Gravity.CENTER);

            // Prevent MultiLine Input
            sub.setSingleLine(true);

            // Create new EditText representing room input
            String rooms;
            EditText room = new EditText(this);

            // Ids 41-52 represent rooms, needed later on for retrieving the data
            room.setId(40+i);
            room.setHint("Raum" + i);
            room.setFilters(new InputFilter[] {filter});

            // Key for subject e. g. Montag11r (Day + hour + r)
            rooms = sharedPreferences.getString(days[day] + i + "r", "");

            // Remove "-" to make editing schedule easier
            // "-" appear after the Schedule has been saved once already
            if(rooms.equalsIgnoreCase("-")) {
                room.setText("");
            } else {
                room.setText(rooms);
            }
            room.setGravity(Gravity.CENTER);

            // Prevent MultiLine Input
            room.setSingleLine(true);

            // Add TextView (Hour), EditText (Subject) and EditText (Room) to the row
            row.addView(h, new TableRow.LayoutParams(0));
            row.addView(sub, new TableRow.LayoutParams(1));
            row.addView(room, new TableRow.LayoutParams(2));

            // Add the row to the table
            tableLayout.addView(row);
        }
    }

    // Retrieve user input
    private void getInput() {
        for(int i = 1; i <= 11; i++) {
            EditText subject = findViewById(20+i);
            EditText room = findViewById(40+i);
            setLesson(days[day], i, subject.getText().toString(), room.getText().toString());
        }
    }

    private void setLesson(String day, int hour, String subject, String room) {
        // If subject input isn't empty, add it to the Config and to subject List, otherwise add "-" to the Config
        if(!subject.equalsIgnoreCase("")) {
            prefEdit.putString(day + hour + "s", subject);
            getMain().addSubject(subject);
        } else {
            prefEdit.putString(day + hour + "s", "-");
        }

        // If room input isn't empty, add it to the Config, otherwise add "-" to the Config
        if(!room.equalsIgnoreCase("")) {
            prefEdit.putString(day + hour + "r", room);
        } else {
            prefEdit.putString(day + hour + "r", "-");
        }
    }

    private void openActivity(Intent i, Class c) {
        i.setClass(getApplicationContext(), c);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    private Main getMain() {
        return new Main();
    }
}