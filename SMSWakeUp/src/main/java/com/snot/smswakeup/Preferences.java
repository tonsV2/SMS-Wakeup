package com.snot.smswakeup;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.preferences_activity_title));
		// Display the fragment as the main content.
//maingetFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
//PreferencesFragmentPreferenceManager.setDefaultValues(Preferences.this, R.menu.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
	}
/*
	public static class PreferencesFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.menu.preferences);
        }
    }
*/
}

