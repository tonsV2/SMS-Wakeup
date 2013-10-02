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
import android.app.ListActivity;

import com.snot.smswakeup.database.Blacklist;
import com.snot.smswakeup.database.Provider;


// TODO:
//TODOswipe to dismiss
//dismissdelete entire hist


public class BlacklistActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public BlacklistActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
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
//			String name = MainActivity.getContactName(this, phoneNumber);
			String name = phoneNumber;

			text1.setText(name);
			text2.setText(phoneNumber);

			return row;
		}
	    };

	setListAdapter(adapter);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    return new CursorLoader(BlacklistActivity.this, Provider.URI_BLACKLIST, Blacklist.FIELDS, null, null, null);
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
	Cursor c = ((SimpleCursorAdapter)list.getAdapter()).getCursor();
	// move to the desired position
	c.moveToPosition(position);
	// pass it to our history object
	Blacklist blacklist = new Blacklist(c);
	Toast.makeText(this, blacklist.phoneNumber, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//inflaterinflater.inflate(R.menu.exercise_list, menu);
//    }
}

