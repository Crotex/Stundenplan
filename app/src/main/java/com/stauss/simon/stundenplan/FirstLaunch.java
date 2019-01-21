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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();
        prefEdit.putBoolean("firstLaunch", false);
        prefEdit.apply();

        nameInput = findViewById(R.id.nameInput);
        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setName(nameInput.getText().toString());
                }
            }
        });
        nameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if(event == null || !event.isShiftPressed()) {
                        setName(nameInput.getText().toString());
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), ScheduleEdit.class);
                        i.putExtra("day", 1);
                        startActivity(i);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setName(String name) {
        prefEdit.putString("userName", name);
        prefEdit.commit();
    }
}
