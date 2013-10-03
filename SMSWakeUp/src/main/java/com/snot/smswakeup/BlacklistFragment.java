package com.snot.smswakeup;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.content.ContentResolver;


import com.snot.smswakeup.database.DatabaseHandler;
import com.snot.smswakeup.database.Blacklist;
import com.snot.smswakeup.database.Provider;


// TODO:
//TODOswipe to dismiss


public class BlacklistFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	 private static final int PICK_CONTACT_REQUEST = 1;  // The request code

	public BlacklistFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
			android.R.layout.simple_list_item_2,
			null,
			new String[] { Blacklist.COL_PHONE_NUMBER, Blacklist.COL_PHONE_NUMBER },
			new int[] { android.R.id.text1, android.R.id.text2 },
			0) {
				@Override
				public View getView(int position, View view, ViewGroup parent)
				{
					View row = super.getView(position, view, parent);
					TextView text1 = (TextView)row.findViewById(android.R.id.text1);
					TextView text2 = (TextView)row.findViewById(android.R.id.text2);
		
					Cursor c = getCursor();
					c.moveToPosition(position);
					String phoneNumber = c.getString(c.getColumnIndex(Blacklist.COL_PHONE_NUMBER));
					String name = getContactName(phoneNumber);
		
					text1.setText(name);
					text2.setText(phoneNumber);
		
					return row;
				}
		};

		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), Provider.URI_BLACKLIST, Blacklist.FIELDS, null, null, null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
	// get cursor
		Cursor c = ((SimpleCursorAdapter)getListAdapter()).getCursor();
		// move to the desired position
		c.moveToPosition(position);
		// pass it to our blacklist object
		Blacklist blacklist = new Blacklist(c);
		Toast.makeText(getActivity(), blacklist.phoneNumber, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.blacklist, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_settings:
			Intent settings = new Intent(getActivity(), Preferences.class);
			startActivity(settings);
			return true;
		case R.id.action_add_contact:
			pickContact();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Shows contact picker dialog
	 */
	public void pickContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		intent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
		super.onActivityResult( requestCode, resultCode, intent );
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == PICK_CONTACT_REQUEST) {
				handleContact(intent);
			}
		}
	}

	private void handleContact(Intent intent)
	{
		final String phoneNumber = getPhoneNumber(intent.getData());
		Blacklist blacklist = new Blacklist();
		// trim whitespace
		blacklist.phoneNumber = phoneNumber.replaceAll("\\s+","");
		// TODO: use provider
		DatabaseHandler.getInstance(getActivity()).putBlacklist(blacklist);
	}

	private String getPhoneNumber(Uri contact)
	{
		String[] projection = {Phone.NUMBER};
		Cursor cursor = getActivity().getContentResolver().query(contact, projection, null, null, null);
		cursor.moveToFirst();
		// Retrieve the phone number from the NUMBER column
		int column = cursor.getColumnIndex(Phone.NUMBER);
		String phoneNumber = null;
		if(column != -1)
		{
				phoneNumber = cursor.getString(column);
		}
		cursor.close();
		return phoneNumber;
	}

/* Get name of contact by phone number
 * http://stackoverflow.com/questions/3079365/android-retrieve-contact-name-from-phone-number
 */
	public String getContactName(String phoneNumber) {
		ContentResolver cr = getActivity().getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
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

