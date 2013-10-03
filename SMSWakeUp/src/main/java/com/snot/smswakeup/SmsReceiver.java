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

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// TODO: dont hard code strings
		boolean customAlarm = prefs.getBoolean("custom_alarm", false);
		String alarmSound = prefs.getString("alarm_sound", "default ringtone");
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

		if(message.equals(wakeUpCommand) && !isBlacklisted(phoneNumber))
		{
			Log.d(TAG, "" + isBlacklisted(phoneNumber));
			Log.d(TAG, phoneNumber);

			//http://android.konreu.com/developer-how-to/vibration-examples-for-android-phone-development/
//			if(vibrate)
//			{
//				// Get instance of Vibrator from current Context
//				Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//				v.vibrate(vibrateTime);
//			}

			Uri alarm = null;
			if(customAlarm)
			{
				alarm = Uri.parse(alarmSound);
			}
			else
			{
				alarm = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound);
			}

			Ringtone r = RingtoneManager.getRingtone(context, alarm);
			r.play();
		}
		// TODO: loop alarm
		// TODO: notification to stop alarm
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
}

