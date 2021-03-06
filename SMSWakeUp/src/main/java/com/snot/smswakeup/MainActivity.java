package com.snot.smswakeup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	protected static final String TAG = "SmsWakeup";

	private SharedPreferences prefs;
	private String wakeUpCommand;
	private String info;
	private TextView tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(SoundAlarm.getInstance().isPlaying())
		{
			SoundAlarm.getInstance().stop();
			Toast.makeText(this, getString(R.string.silence_toast), Toast.LENGTH_SHORT).show();
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		wakeUpCommand = prefs.getString(Preferences.WAKEUP_CMD, Preferences.WAKEUP_CMD_DEFAULT);
		info = getString(R.string.info, wakeUpCommand);
		tv = (TextView)findViewById(R.id.info);
		tv.setText(info);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(Preferences.WAKEUP_CMD))
		{
			wakeUpCommand = prefs.getString(Preferences.WAKEUP_CMD, Preferences.WAKEUP_CMD_DEFAULT);
			info = getString(R.string.info, wakeUpCommand);
			tv.setText(info);
		}
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
		case R.id.action_blacklist:
			Intent blacklist = new Intent(this, BlacklistActivity.class);
			startActivity(blacklist);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
