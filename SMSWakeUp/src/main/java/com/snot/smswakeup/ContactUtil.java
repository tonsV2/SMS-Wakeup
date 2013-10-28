package com.snot.smswakeup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

/**
 * @author snot
 *
 */


public class ContactUtil {
	private static final String TAG = "SmsWakeup.ContactUtil";

/**
 * Returns an array of CONTACT_ID's associated with phoneNumber
 *
 * @param context Application context
 * @param phoneNumber The phone number used to query
 * @return An array of CONTACT_ID's
 *
 */
	public static long[] getContactIdsByPhoneNumber(Context context, String phoneNumber) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		String[] projection = new String[] { PhoneLookup._ID };
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			return null;
		}

		int count = cursor.getCount();
		Log.d(TAG, "cursor.getCount: " + count);
		// TODO: is getCount zero indexed... ???
		long[] ids = new long[count];
		for(int i = 0; i < count; i++, cursor.moveToNext())
		{
			ids[i] = cursor.getLong(0);
		}
		return ids;
	}

// TODO: perhaps rename to getContactIdByDataUri
	public static long getContactIdByUri(Context context, Uri uri)
	{
		String[] projection = new String[] { Data.CONTACT_ID };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		long id = -1;
		if(cursor.moveToFirst()) {
			id = cursor.getLong(0);
		}
		return id;
	}

	public static String getContactName(Context context, long id) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(id));
		String[] projection = new String[] { Contacts.DISPLAY_NAME };
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		if(cursor == null) {
			return null;
		}
		String contactName = null;
		if(cursor.moveToFirst()) {
			//contactName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
			contactName = cursor.getString(0);
		}
		if(!cursor.isClosed()) {
			cursor.close();
		}
		return contactName;
	}
}
