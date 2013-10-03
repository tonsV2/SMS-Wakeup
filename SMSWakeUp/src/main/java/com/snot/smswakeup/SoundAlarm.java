package com.snot.smswakeup;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;


/**
 * @author snot
 *
 */
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
		mediaPlayer = MediaPlayer.create(context, musicId);
	}
	
	public void start(){
		mediaPlayer.start();
	}
	
	public void stop(){
		mediaPlayer.stop();
	}
}

