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
    private static SoundAlarm reference = null;
    private MediaPlayer mediaPlayer;

    public static SoundAlarm getInstance() {
        if(reference == null)
        {
            Log.d(TAG, "refrence == null");
            reference = new SoundAlarm();
        }
        return reference;
    }

    public void initializeMediaPlayer(Context context){
        initializeMediaPlayer(context, R.raw.alarm_sound);
    }

    public void initializeMediaPlayer(Context context, int musicId){
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

