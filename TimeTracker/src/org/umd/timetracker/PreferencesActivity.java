package org.umd.timetracker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Preference configuration screen.
 * 
 * Allows the user to enter their preferences and then update into a
 * sharedpreferences document.
 */
public class PreferencesActivity extends android.preference.PreferenceActivity {
    @Override                                                   
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
