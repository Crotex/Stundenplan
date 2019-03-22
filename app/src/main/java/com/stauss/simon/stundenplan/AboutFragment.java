package com.stauss.simon.stundenplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// This fragment only displays text and therefor basically no code is needed
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        // Text has to be set trough code to have the HTML Tags(e. g. <b>) take effect
        TextView instructions = v.findViewById(R.id.instructions);
        instructions.setText(getText(R.string.about_instructions));
        return v;
    }
}
