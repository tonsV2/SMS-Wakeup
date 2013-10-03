package com.snot.smswakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.database.Cursor;
import android.util.Log;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.PendingIntent;


import com.snot.smswakeup.database.Blacklist;
import com.snot.smswakeup.database.Provider;

/**
 * @author snot
 *
 * TODO: show notification when unauthorized user sends command
 * TODO: move sharedpreferences population into constructor so it's not called each time an sms is received
 * TODO: refactor alarm handling into a method of it's own
 */
public class SmsReceiver extends BroadcastReceiver {

	private final static String TAG = "SmsReceiver";
	Context context;

	SharedPreferences prefs;
	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// TODO: dont hard code strings
		String wakeUpCommand = prefs.getString("wakeup_cmd", "WAKE UP");
		boolean CaseSensetiveCompare = prefs.getBoolean("case_sensetive_cmp", false);
//		boolean vibrate = prefs.getBoolean("vibrate", true);
//		int vibrateTime = Integer.parseInt(prefs.getString("vibrate_time", "3000"));
//boolean flash = prefs.getBoolean("flash", true);
//boolean flashScreen = prefs.getBoolean("flash_screen", true);

		Bundle pudsBundle = intent.getExtras();
		Object[] pdus = (Object[]) pudsBundle.get("pdus");
		SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

		String phoneNumber = sms.getOriginatingAddress();
		String message = sms.getMessageBody().trim();

		if(!CaseSensetiveCompare)
		{
			wakeUpCommand = wakeUpCommand.toLowerCase();
			message = message.toLowerCase();
		}

		Log.d(TAG, phoneNumber);
		boolean isBlacklisted = isBlacklisted(phoneNumber);
		Log.d(TAG, "isBlacklisted: " + isBlacklisted);

		if(message.equals(wakeUpCommand) && !isBlacklisted)
		{
			AlarmNotification("Wake up msg from " + phoneNumber);
			soundAlarm();
			//http://android.konreu.com/developer-how-to/vibration-examples-for-android-phone-development/
//			if(vibrate)
//			{
//				// Get instance of Vibrator from current Context
//				Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//				v.vibrate(vibrateTime);
//			}

		}
		if(isBlacklisted)
		{
			BlacklistNotification("Message from blacklisted number: " + phoneNumber);
		}
	}


	private void soundAlarm()
	{
		SoundAlarm.getInstance().initalizeMediaPlayer(context);
		SoundAlarm.getInstance().start();

//		boolean customAlarm = prefs.getBoolean("custom_alarm", false);
//		String alarmSound = prefs.getString("alarm_sound", "default ringtone");
//		// TODO: loop alarm
//		Uri alarm = null;
//		if(customAlarm)
//		{
//			alarm = Uri.parse(alarmSound);
//		}
//		else
//		{
//			alarm = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound);
//		}
//
//		Ringtone r = RingtoneManager.getRingtone(context, alarm);
//		r.play();
	}
	
	private boolean isBlacklisted(String phoneNumber)
	{
		Cursor cursor = context.getContentResolver().query(Provider.URI_BLACKLIST,
			new String[] { Blacklist.COL_PHONE_NUMBER },
			Blacklist.COL_PHONE_NUMBER + " = ?",
			new String[] { phoneNumber },
			null);
		return(cursor.getCount() > 0);
	}

	private void BlacklistNotification(String msg) {
		Log.d(TAG, "BlacklistNotification");
		Intent intent = new Intent(context, BlacklistActivity.class);
		sendNotification(msg, intent);
	}

	private void AlarmNotification(String msg) {
		Log.d(TAG, "AlarmNotification");
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(MainActivity.INTENT_SILENCE, true);
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

