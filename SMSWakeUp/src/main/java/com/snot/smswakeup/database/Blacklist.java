package com.snot.smswakeup.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by snot on 6/3/13.
 */
public class Blacklist {

    // SQL convention says Table name should be "singular", so not Exercises
    public static final String TABLE_NAME = "Blacklist";
    // Naming the id column with an underscore is good to be consistent
    // with other Android things. This is ALWAYS needed
    public static final String COL_ID = "_id";
    public static final String COL_CONTACT_ID = "contact_id";

    // For database projection so order is consistent
    public static final String[] FIELDS = {
    	COL_ID,
    	COL_CONTACT_ID
	};

    /*
     * The SQL code that creates a Table for storing Exercises in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
     // TODO: make phoneNumber col unique
     // http://stackoverflow.com/questions/7836561/add-unique-index-sqlite3
     // create index on phoneNumber for faster querying
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_CONTACT_ID + " INTEGER NOT NULL"
                    + ")";
//    public static final String CREATE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS UniqueIndex" + COL_CONTACT_ID + " ON " + TABLE_NAME + "(" + COL_CONTACT_ID + ");";
    public static final String CREATE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS UniqueIndexContactID ON " + TABLE_NAME + "(" + COL_CONTACT_ID + ")";

    // Fields corresponding to database columns
    public long id = -1;
    public long contactId;

    /**
     * No need to do anything, fields are already set to default values above
     */
    public Blacklist() {
    }

    /**
     * Convert information from the database into a Exercise object.
     */
    public Blacklist(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.id = cursor.getLong(0);
        this.contactId = cursor.getLong(1);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContent() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_CONTACT_ID, contactId);

        return values;
    }
}

