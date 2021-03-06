package com.stauss.simon.stundenplan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeworkAdd extends AppCompatActivity {

    static EditText dateText;
    EditText description;
    Spinner subjectSpinner;
    List<String> subjects;
    static boolean datePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_add);

        initSpinner();

        // DateText input shouldn't be clicked
        dateText = findViewById(R.id.dateText);
        dateText.setFocusable(false);
        dateText.setInputType(InputType.TYPE_NULL);

        // Open DatePicker Fragment once "Date" Button is clicked
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Once "Save" Button is pressed, add homework with all the Information given in the InputFields
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allInformationGiven()) {
                    // Add Homework with "Subject", "Description" and "Date"
                    getMain().addHomework(subjectSpinner.getSelectedItem().toString(), description.getText().toString(), dateText.getText().toString());

                    // Inform user that the homework was added successfully
                    Toast.makeText(HomeworkAdd.this, getString(R.string.homework_success), Toast.LENGTH_SHORT).show();

                    // Open Main Activity again
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                } else {
                    Toast.makeText(HomeworkAdd.this, getString(R.string.error_not_filled), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // User isn't allowed to use ";" in the "Description" since it'll corrupt the saving and restoring process (";" is used as regex to split items)
        InputFilter filter = new InputFilter() {
            String blockedCharacters = getString(R.string.blocked_characters);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && blockedCharacters.contains(("" + source))) {
                    Toast.makeText(HomeworkAdd.this, getString(R.string.error_char_not_allowed), Toast.LENGTH_SHORT).show();
                    return "";
                }
                return null;
            }
        };
        description = findViewById(R.id.homeworkDescription);
        description.setFilters(new InputFilter[] {filter});
    }

    // Get Subjects from IntentExtra and initialize spinner (Drop-Down List) with subjects
    private void initSpinner() {
        subjectSpinner = findViewById(R.id.subSpinner);
        ArrayAdapter<String> adapter;
        subjects = new ArrayList<>();

        // Is subject list from intent empty?
        if(!getIntent().getStringArrayListExtra("subjects").isEmpty()) {
            subjects = getIntent().getStringArrayListExtra("subjects");
        } else {
            // No subjects were added -> Impossible to add Homework
            subjects.add(getString(R.string.no_subjects));
        }

        adapter = new ArrayAdapter<>(this,  R.layout.support_simple_spinner_dropdown_item, subjects.toArray(new String[1]));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }

    // Is every InputField filled?
    private boolean allInformationGiven() {
        return datePicked && !description.getText().toString().equalsIgnoreCase("") && !subjects.contains(getString(R.string.no_subjects));
    }

    private Main getMain() {
        return new Main();
    }

    public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            //Open DatePicker with the current date selected
            return new DatePickerDialog(getActivity(), this, y, m, d);
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            // Build the String to always have the same format DD.MM.YYY (Used for sorting)
            String date = "";
            if(dayOfMonth < 10) {
                date = "0";
            }
            date += dayOfMonth + ".";
            if(month < 9) {
                date += "0";
            }
            date += (month + 1) + "." + year;
            // 02.02.2019
            // 12.12.2019
            dateText.setText(date);
            datePicked = true;
        }
    }

    // TimePicker -> Needed for Notifications, Disabled for now

    /*
    public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, h, m, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

        }
    }
    */
}
