package com.snot.smswakeup;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.view.MenuItem;
import android.content.Intent;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_settings:
			Intent settings = new Intent(this, Preferences.class);
			startActivity(settings);
			return true;
//		case R.id.action_add_contact:
//			Intent contact = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
//			startActivityForResult(contact, R.id.action_add_contact);
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

