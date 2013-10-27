package com.snot.smswakeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author snot
 *
 */

// Credits: http://stackoverflow.com/questions/14728945/play-stop-sound-from-another-activity

public class SoundAlarm {
	private static final String TAG = "SoundAlarm";
	private static SoundAlarm refrence = null;
	private MediaPlayer mediaPlayer;

	public static SoundAlarm getInstance() {
		if(refrence == null)
		{
			Log.d(TAG, "refrence == null");
			refrence = new SoundAlarm();
		}
		return refrence;
	}

	public void initalizeMediaPlayer(Context context){
		initalizeMediaPlayer(context, R.raw.alarm_sound);
	}

	public void initalizeMediaPlayer(Context context, int musicId){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mediaPlayer = MediaPlayer.create(context, musicId);
		boolean loop = prefs.getBoolean(Preferences.KEEP_PLAYING, true);
		mediaPlayer.setLooping(loop);
	}
	
	public void start(){
		mediaPlayer.start();
	}
	
	public void stop(){
		mediaPlayer.stop();
	}

	public boolean isPlaying(){
		return mediaPlayer != null && mediaPlayer.isPlaying();
	}
}

