package com.stauss.simon.stundenplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        TextView instructions = v.findViewById(R.id.instructions);
        instructions.setText(getText(R.string.about_instructions));
        return v;
    }
}
