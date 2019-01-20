package com.stauss.simon.stundenplan;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FirstLaunch extends AppCompatActivity {

    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
    SharedPreferences.Editor prefEdit = sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
    }
}
