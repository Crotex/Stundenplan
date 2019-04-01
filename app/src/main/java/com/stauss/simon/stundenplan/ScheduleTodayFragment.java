package com.stauss.simon.stundenplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleTodayFragment extends Fragment {

    SharedPreferences sharedPreferences;

    String day;

    public ScheduleTodayFragment() {
        //"Required empty public constructor" - Android
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        day = getMain().getDay();

        sharedPreferences = getMain().getSharedPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_today, container, false);

        TextView text = v.findViewById(R.id.textView);
        // Is the App opened on a weekend? -> Open schedule for monday, else open schedule for today with corresponding date
        if(!getMain().isWeekend()) {
            String date = new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMANY).format(new Date());
            text.setText("Heute, " + date + ", hast du folgende Fächer:" );
        } else {
            text.setText("Am folgenden Montag hast du folgende Fächer:");
        }

        buildSchedule((TableLayout) v.findViewById(R.id.table));

        // Inflate the layout for this fragment
        return v;
    }

    private Main getMain() {
        return new Main();
    }

    private void buildSchedule(TableLayout tableLayout) {
        // Repeat this for each hour (1-11)
        for (int i = 1; i <= 11; i++) {
            // Create a new row
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 5, 0, 5);

            // Create new TextView for each hour
            TextView h = new TextView(getContext());
            h.setText("" + i);
            h.setGravity(Gravity.CENTER);

            // Create new TextView for each subject
            TextView sub = new TextView(getContext());
            sub.setText(sharedPreferences.getString(day + i + "s", "-"));
            sub.setGravity(Gravity.CENTER);

            // Create new TextView for each room
            TextView room = new TextView(getContext());
            room.setText(sharedPreferences.getString(day + i + "r", "-"));
            room.setPadding(0,0,50, 0);
            room.setGravity(Gravity.CENTER);

            // Add views to the row
            row.addView(h, new TableRow.LayoutParams(0));
            row.addView(sub, new TableRow.LayoutParams(1));
            row.addView(room, new TableRow.LayoutParams(2));

            // Add row to table
            tableLayout.addView(row);
        }
    }
}
