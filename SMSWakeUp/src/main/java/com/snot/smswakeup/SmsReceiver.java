package com.snot.smswakeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.snot.smswakeup.database.Blacklist;
import com.snot.smswakeup.database.Provider;

/**
 * @author snot
 *
 * TODO: refactor alarm handling into a method of it's own
 */
public class SmsReceiver extends BroadcastReceiver {

    private final static String TAG = "SmsReceiver";
    Context context;

    SharedPreferences prefs;
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String wakeUpCommand = prefs.getString(Preferences.WAKEUP_CMD, Preferences.WAKEUP_CMD_DEFAULT);
        boolean caseSensitiveCompare = prefs.getBoolean(Preferences.CASE_SENSITIVE_CMP, false);

        Bundle pudsBundle = intent.getExtras();
        assert pudsBundle != null;
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

        String phoneNumber = sms.getOriginatingAddress();
        String message = sms.getMessageBody().trim();

        if(!caseSensitiveCompare)
        {
            wakeUpCommand = wakeUpCommand.toLowerCase();
            message = message.toLowerCase();
        }

        Log.d(TAG, phoneNumber);
        boolean isBlacklisted = isBlacklisted(phoneNumber);
        Log.d(TAG, "isBlacklisted: " + isBlacklisted);

        if(message.equals(wakeUpCommand) && !isBlacklisted)
        {
            // TODO: display DISPLAY_NAME instead of phone number
            AlarmNotification(context.getString(R.string.notification_msg_from) + phoneNumber);
            soundAlarm();
        }
        if(isBlacklisted)
        {
            BlacklistNotification(context.getString(R.string.notification_msg_from_blacklisted) + phoneNumber);
        }
    }


    private void soundAlarm()
    {
        boolean maximizeVolume = prefs.getBoolean(Preferences.MAXIMIZE_VOLUME, Preferences.MAXIMIZE_VOLUME_DEFAULT);
        // Maximize volume
        if(maximizeVolume)
        {
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        }
        // Sound alarm
        SoundAlarm.getInstance().initializeMediaPlayer(context);
        SoundAlarm.getInstance().start();
    }

    private boolean isBlacklisted(String phoneNumber)
    {
        // get contact id from phonenumber
        long[] ids = ContactUtil.getContactIdsByPhoneNumber(context, phoneNumber);
        // see if contact id is listed in our db
        // TODO: don't query for each... use sql IN clause
        for(long id : ids)
        {
            Cursor cursor = context.getContentResolver().query(Provider.URI_BLACKLIST,
                    new String[] { Blacklist.COL_CONTACT_ID},
                    Blacklist.COL_CONTACT_ID + " = ?",
                    new String[] { String.valueOf(id) },
                    null);
            assert cursor != null;
            if(cursor.getCount() > 0)
            {
                return(true);
            }
        }
        return false;
    }

    private void BlacklistNotification(String msg) {
        Log.d(TAG, "BlacklistNotification");
        Intent intent = new Intent(context, BlacklistActivity.class);
        sendNotification(msg, intent);
    }

    private void AlarmNotification(String msg) {
        Log.d(TAG, "AlarmNotification");
        Intent intent = new Intent(context, MainActivity.class);
        // no longer need since we kill the sound no  matter what in onCreate
        //intent.putExtra(MainActivity.INTENT_SILENCE, true);
        sendNotification(msg, intent);
    }

    private void sendNotification(String msg, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
