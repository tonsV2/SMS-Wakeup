package com.snot.smswakeup.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by snot on 6/3/13.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler singleton;

    public static DatabaseHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new DatabaseHandler(context);
        }
        return singleton;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smswakeup.sqlite3";

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Blacklist.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public synchronized Blacklist getBlacklist(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(Blacklist.TABLE_NAME,
                Blacklist.FIELDS, Blacklist.COL_ID + " IS ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor == null || cursor.isAfterLast()) {
            return null;
        }

        Blacklist item = null;
        if (cursor.moveToFirst()) {
            item = new Blacklist(cursor);
        }
        cursor.close();

        return item;
    }

    public synchronized boolean putBlacklist(final Blacklist blacklist) {
        boolean success = false;
        int result = 0;
        final SQLiteDatabase db = this.getWritableDatabase();

        if (blacklist.id > -1) {
            result += db.update(Blacklist.TABLE_NAME, blacklist.getContent(),
                    Blacklist.COL_ID + " IS ?",
                    new String[] { String.valueOf(blacklist.id) });
        }

        if (result > 0) {
            success = true;
        } else {
            // Update failed or wasn't possible, insert instead
            final long id = db.insert(Blacklist.TABLE_NAME, null,
                    blacklist.getContent());

            if (id > -1) {
                blacklist.id = id;
                success = true;
            }
        }
        if(success) {
            notifyProviderOnBlacklistChange();
        }
        return success;
    }

    public synchronized int removeBlacklist(final Blacklist blacklist) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Blacklist.TABLE_NAME,
                Blacklist.COL_ID + " IS ?",
                new String[] { Long.toString(blacklist.id) });

        if (result > 0) {
            notifyProviderOnBlacklistChange();
        }
        return result;
    }

    private void notifyProviderOnBlacklistChange() {
        context.getContentResolver().notifyChange(Provider.URI_BLACKLIST, null, false);
    }
}

