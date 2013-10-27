package com.snot.smswakeup;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class BlacklistActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.blacklist_activity_title));

		// Create the ListFragment and add it as our sole content.
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(android.R.id.content) == null) {
			BlacklistFragment list = new BlacklistFragment();
			fm.beginTransaction().add(android.R.id.content, list).commit();
		}
	}
}
