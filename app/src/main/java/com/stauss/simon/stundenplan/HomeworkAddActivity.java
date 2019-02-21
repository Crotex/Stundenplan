package com.stauss.simon.stundenplan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class HomeworkAddActivity extends AppCompatActivity {

    static EditText dateText;
    EditText description;
    Spinner subjectSpinner;
    static boolean datePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_add);

        initSpinner();

        dateText = findViewById(R.id.dateText);
        dateText.setFocusable(false);
        dateText.setInputType(InputType.TYPE_NULL);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allInformationGiven()) {
                    getMain().addHomework(subjectSpinner.getSelectedItem().toString(), description.getText().toString(), dateText.getText().toString());
                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), MainSchedule.class);
                    startActivity(i);
                } else {
                    Toast.makeText(HomeworkAddActivity.this, getString(R.string.error_not_filled), Toast.LENGTH_SHORT).show();
                }
            }
        });
        description = findViewById(R.id.homeworkDescription);
    }

    private void initSpinner() {
        if(!getIntent().getStringArrayListExtra("subjects").isEmpty()) {
            List<String> subjects = getIntent().getStringArrayListExtra("subjects");
            subjectSpinner = findViewById(R.id.subSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,  R.layout.support_simple_spinner_dropdown_item, subjects.toArray(new String[1]));
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(adapter);
        }
    }

    private boolean allInformationGiven() {
        if(datePicked && !description.getText().toString().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    private MainSchedule getMain() {
        MainSchedule main = new MainSchedule();
        main.sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        main.prefEdit = main.sharedPreferences.edit();
        return main;
    }

    public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, y, m, d);
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateText.setText(dayOfMonth+ "." + (month + 1) + "." + year);
            datePicked = true;
        }
    }

    /*
    //TimePicker -> Needed for Notifications
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
