package com.stauss.simon.stundenplan;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor prefEdit;
    public static Main main = new Main();

    Context context = this;

    public static Preference.OnPreferenceClickListener preferenceClickListener;

    static Preference resetSchedule, deleteSubjects;

    String whichIsClicked;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            /* Not using listPreference for now
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            } */

            /* Settings are launched for the first time -> Display userName correctly
            if(stringValue.equalsIgnoreCase("Max Mustermann") && preference.getKey().equalsIgnoreCase("name_preference")) {
                String userName = sharedPreferences.getString("userName", "");
                preference.setSummary(userName);
                preference.setDefaultValue(userName);
                return true;
            } */

            // User Name changend -> save to config and refresh name TextView
            if(preference.getKey().equalsIgnoreCase("name_preference")) {
                prefEdit.putString("userName", stringValue);
                prefEdit.commit();
                getMain().refreshName();
            }

            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getMain().getSharedPreferences();
        prefEdit = getMain().getPrefEdit();

        String userName = sharedPreferences.getString("userName", getString(R.string.userName));
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("name_preference", userName).commit()) {
            setupListener();
            setupActionBar();
        } else {
            onCreate(savedInstanceState);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListener() {
        preferenceClickListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes button clicked
                                // Did the user want to clear subjects or homework (both have the same Yes / No dialog)
                                if(whichIsClicked.equalsIgnoreCase("subjects")) {
                                    getMain().clearSubjects();

                                    // Notify user
                                    Toast.makeText(context, "Du hast erfolgreich alle Fächer gelöscht!", Toast.LENGTH_SHORT).show();
                                } else if (whichIsClicked.equalsIgnoreCase("schedule")) {
                                    getMain().resetSchedule();

                                    // No notification needed since the app will restart itself
                                }
                                deleteSubjects.setEnabled(false);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked, do nothing
                                break;
                        }
                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                if(preference.getKey().equalsIgnoreCase(deleteSubjects.getKey())) {
                    whichIsClicked = "subjects";
                    //Yes / No Dialog
                    builder.setMessage(getString(R.string.pref_are_you_sure)).setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).setTitle(getString(R.string.pref_delete_subjects)).show();
                    return true;
                } else if(preference.getKey().equalsIgnoreCase(resetSchedule.getKey())) {
                    whichIsClicked = "schedule";
                    //Yes / No Dialog
                    builder.setMessage(getString(R.string.pref_are_you_sure)).setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).setTitle(getString(R.string.pref_reset_schedule)).show();
                    return true;
                }
                return false;
            }
        };
    }

    // The following Code is created by default
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            EditTextPreference namePreference = (EditTextPreference) findPreference("name_preference");

            deleteSubjects = findPreference("delete_subjects");
            resetSchedule = findPreference("reset_schedule");

            // Is subject list empty? -> Disable deleteSubjects and resetSchedule or bind them to the listener
            if(getMain().getSubjects().size() != 0) {
                deleteSubjects.setOnPreferenceClickListener(preferenceClickListener);
                resetSchedule.setOnPreferenceClickListener(preferenceClickListener);
            } else {
                deleteSubjects.setEnabled(false);
                resetSchedule.setEnabled(false);
            }

            bindPreferenceSummaryToValue(namePreference);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    // Notification Preferences disabled for now
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static Main getMain() {
        return main;
    }
}
