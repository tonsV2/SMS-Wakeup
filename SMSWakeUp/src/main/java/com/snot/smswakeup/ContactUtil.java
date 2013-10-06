package com.snot.smswakeup;

import android.content.Context;
import android.util.Log;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.database.Cursor;

/**
 * @author snot
 *
 */


public class ContactUtil {
	private static final String TAG = "ContactUtil";

/* Get name of contact by phone number
 * http://stackoverflow.com/questions/3079365/android-retrieve-contact-name-from-phone-number
 */
	public static String getContactName(Context context, String phoneNumber) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = contentResolver.query(uri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if(cursor.moveToFirst()) {
			contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		if(cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return contactName;
	}
}

