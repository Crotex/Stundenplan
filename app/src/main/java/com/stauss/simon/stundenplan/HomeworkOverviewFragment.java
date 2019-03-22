package com.stauss.simon.stundenplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeworkOverviewFragment extends Fragment {

    RecyclerView homeworkList;
    FloatingActionButton deleteHomework;
    List<String> homework;

    public HomeworkOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_homework_overview, container, false);

        homework = getMain().getHomework();

        homeworkList = v.findViewById(R.id.list);
        homeworkList.setLayoutManager(new LinearLayoutManager(getContext()));
        homeworkList.setAdapter(new ListAdapter(homework));

        deleteHomework = v.findViewById(R.id.deleteHomework);
        deleteHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                getMain().clearHomework();
                                Toast.makeText(getContext(), R.string.homework_cleared, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getContext(), Main.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(i);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.ask_clear_homework).setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    private Main getMain() {
        return new Main();
    }
}
