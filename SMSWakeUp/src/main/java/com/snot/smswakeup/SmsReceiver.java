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

/**
 * @author snot
 *
 * TODO: show notification when unauthorized user sends command
 * TODO: move sharedpreferences population into constructor so it's not called each time an sms is received
 * TODO: refactor alarm handling into a method of it's own
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// TODO: dont hard code strings
		boolean customAlarm = prefs.getBoolean("custom_alarm", false);
		String alarmSound = prefs.getString("alarm_sound", "default ringtone");
		String wakeUpCommand = prefs.getString("wakeup_cmd", "WAKE UP");
		boolean CaseSensetiveCompare = prefs.getBoolean("case_sensetive_cmp", true);
		boolean vibrate = prefs.getBoolean("vibrate", true);
		int vibrateTime = Integer.parseInt(prefs.getString("vibrate_time", "3000"));
//vibrate_timeboolean flash = prefs.getBoolean("flash", true);
//flashboolean flashScreen = prefs.getBoolean("flash_screen", true);


		Bundle pudsBundle = intent.getExtras();
		Object[] pdus = (Object[]) pudsBundle.get("pdus");
		SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

		String phoneNumber = sms.getOriginatingAddress();
		String message = sms.getMessageBody().trim();

		// TODO: implement whitelist database
		// if(wl.has(phoneNumber))

		if(!CaseSensetiveCompare)
		{
			wakeUpCommand = wakeUpCommand.toLowerCase();
			message = message.toLowerCase();
		}

		if(message.equals(wakeUpCommand))
		{
			//http://android.konreu.com/developer-how-to/vibration-examples-for-android-phone-development/
			if(vibrate)
			{
				// Get instance of Vibrator from current Context
				Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(vibrateTime);
			}

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
}

