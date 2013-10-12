package com.snot.smswakeup;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

// TODO: Add a few more defaults here
// TODO: Also is (or the values in preferences.xml) somewhat redundant... these strings are listed elsewhere as well... how to fix?
	public static final String WAKEUP_CMD = "wakeup_cmd";
	public static final String WAKEUP_CMD_DEFAULT = "WAKEUP";
	public static final String CASE_SENSITIVE_CMP = "case_sensitive_cmp";
	public static final String MAXIMIZE_VOLUME = "maximize_volume";
	public static final boolean MAXIMIZE_VOLUME_DEFAULT = true;
	public static final String KEEP_PLAYING = "keep_playing";

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

