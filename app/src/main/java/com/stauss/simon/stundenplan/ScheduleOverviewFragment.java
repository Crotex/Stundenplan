package com.stauss.simon.stundenplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScheduleOverviewFragment extends Fragment {

    SharedPreferences sharedPreferences;

    String[] days;

    public ScheduleOverviewFragment() {
        //"Required empty public constructor" - Android
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        days = getMain().getWeek();

        sharedPreferences = getMain().getSharedPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_overview, container, false);

        buildSchedule((TableLayout) v.findViewById(R.id.table));

        // Inflate the layout for this fragment
        return v;
    }

    private Main getMain() {
        return new Main();
    }

    private void buildSchedule(TableLayout table) {
        TableRow row;

        for (int i = 1; i <= 11; i++) {
            row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            row.setPadding(0, 5, 0, 5);

            TextView h = new TextView(getContext());
            h.setText("" + i);
            h.setGravity(Gravity.CENTER);
            row.addView(h, new TableRow.LayoutParams(0));

            for (int d = 1; d <= 5; d++) {
                TextView sub = new TextView(getContext());
                sub.setText(sharedPreferences.getString( days[d] + i + "s", "-"));
                sub.setGravity(Gravity.CENTER);

                Space space = new Space(getContext());
                row.addView(sub, new TableRow.LayoutParams(d + 1));
                row.addView(space, new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            table.addView(row);
        }
    }
}