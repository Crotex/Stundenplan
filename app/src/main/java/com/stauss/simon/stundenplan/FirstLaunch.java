package com.stauss.simon.stundenplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class FirstLaunch extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;
    EditText nameInput;
    View.OnFocusChangeListener onFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        // Preventing the App from launching this activity twice
        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();
        prefEdit.putBoolean("firstLaunch", false);
        prefEdit.apply();

        // NameInput = EditText where the user is supposed to enter their name
        nameInput = findViewById(R.id.nameInput);

        // FocusChangeListener to handle focus change (e. g. user tapping out of the nameInput)
        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Execute the next step if nameInput lost focus
                if(v == nameInput && !hasFocus) {
                    setName(nameInput.getText().toString());
                }
            }
        };

        // Bind listener to layout
        nameInput.setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.firstLaunchLayout).setOnFocusChangeListener(onFocusChangeListener);

        // Execute the next step if user pressed the "Enter" button
        nameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if(event == null || !event.isShiftPressed()) {
                        setName(nameInput.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


    // Save userName to config and continue with setup
    private void setName(String name) {
        prefEdit.putString("userName", name);
        prefEdit.commit();
        nameInputFinished();
    }

    // Open ScheduleEdit Activity
    private void nameInputFinished() {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), ScheduleEdit.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("day", 1);
        startActivity(i);
    }
}