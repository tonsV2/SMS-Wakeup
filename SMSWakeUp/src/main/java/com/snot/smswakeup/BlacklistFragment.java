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
import android.app.Activity;
import android.content.ContentResolver;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.database.sqlite.SQLiteConstraintException;



import com.snot.smswakeup.database.DatabaseHandler;
import com.snot.smswakeup.database.Blacklist;
import com.snot.smswakeup.database.Provider;


// TODO:
//	swipe to dismiss


public class BlacklistFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "BlacklistFragment";
	private static final int PICK_CONTACT_REQUEST = 1; // The request code

	public BlacklistFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
			android.R.layout.simple_list_item_1,
			null,
			new String[] { Blacklist.COL_ID },
			new int[] { android.R.id.text1 },
			0) {
				@Override
				public View getView(int position, View view, ViewGroup parent)
				{
					View row = super.getView(position, view, parent);
					TextView text1 = (TextView)row.findViewById(android.R.id.text1);
		
					// Get cursor
					Cursor c = getCursor();
					// Move to relevant place
					c.moveToPosition(position);
					// Calculate index
					int idx = c.getColumnIndex(Blacklist.COL_CONTACT_ID);
					// Get id
					long id = c.getLong(idx);
					// Get name of contact
					String name = ContactUtil.getContactName(getActivity(), id);
		
					text1.setText(name);
		
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
		// TODO
		Toast.makeText(getActivity(), ContactUtil.getContactName(getActivity(), blacklist.contactId) + " removed.", Toast.LENGTH_SHORT).show();
		Uri uri = Uri.withAppendedPath(Provider.URI_BLACKLIST, String.valueOf(blacklist.id));
		getActivity().getContentResolver().delete(uri, null, null);
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
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == PICK_CONTACT_REQUEST) {
				blacklistContact(intent);
			}
		}
	}

	private void blacklistContact(Intent intent)
	{
		Uri uri = intent.getData();
		final long id = ContactUtil.getContactIdByUri(getActivity(), uri);
		Log.d(TAG, "blacklist.contactId: " + id);
		Blacklist blacklist = new Blacklist();
		blacklist.contactId = id;
//		// TODO: use provider
		try
		{
			DatabaseHandler.getInstance(getActivity()).putBlacklist(blacklist);
		}
		catch(SQLiteConstraintException e)
		{
			Toast.makeText(getActivity(), getActivity().getString(R.string.contact_already_balcklisted), Toast.LENGTH_SHORT).show();
		}
	}
}

