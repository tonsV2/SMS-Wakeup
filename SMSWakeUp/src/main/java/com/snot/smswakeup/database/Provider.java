package com.snot.smswakeup.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by snot on 6/3/13.
 */
public class Provider extends ContentProvider {

	private static final String TAG = "Provider";

	private static final String AUTHORITY = "com.snot.smswakeup.database.provider";

	public static final Uri URI_BLACKLIST = (new Uri.Builder())
						.scheme(ContentResolver.SCHEME_CONTENT)
						.authority(AUTHORITY)
						.appendPath("blacklist")
						.build();


	private static final int BLACKLIST = 1;
	private static final int BLACKLISTS = 2;

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		uriMatcher.addURI(AUTHORITY, "blacklist", BLACKLISTS);
		uriMatcher.addURI(AUTHORITY, "blacklist/#", BLACKLIST);
	}


	public Provider() {
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public boolean onCreate() {
		// TODO: Implement this to initialize your content provider on startup.
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.v(TAG, "URI: " + uri);
		Cursor result = null;
		int match = uriMatcher.match(uri);
		switch(match)
		{
			case BLACKLISTS:
				result = DatabaseHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Blacklist.TABLE_NAME, Blacklist.FIELDS, selection, selectionArgs, null, null, sortOrder);
				result.setNotificationUri(getContext().getContentResolver(), URI_BLACKLIST);
				break;
			case BLACKLIST:
				final long eid = Long.parseLong(uri.getLastPathSegment());
				result = DatabaseHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Blacklist.TABLE_NAME, Blacklist.FIELDS,
							Blacklist.COL_ID + " IS ?",
							new String[] { String.valueOf(eid) }, null, null, sortOrder);
				result.setNotificationUri(getContext().getContentResolver(), URI_BLACKLIST);
				break;
			default:
				throw new UnsupportedOperationException("Unmatched(" + match + ") URI: " + uri.toString());
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = -1;
		int match = uriMatcher.match(uri);
		switch(match)
		{
			case BLACKLISTS:
				throw new UnsupportedOperationException("Batch delete not yet implemented");
			case BLACKLIST:
				final long id = Long.parseLong(uri.getLastPathSegment());
				result = DatabaseHandler
					.getInstance(getContext())
					.getWritableDatabase()
					.delete(Blacklist.TABLE_NAME,
							Blacklist.COL_ID + " IS ?",
							new String[] { String.valueOf(id) });
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			default:
				throw new UnsupportedOperationException("Unmatched(" + match + ") URI: " + uri.toString());
		}
		return result;
	}
}

