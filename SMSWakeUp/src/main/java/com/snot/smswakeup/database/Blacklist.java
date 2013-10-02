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
    // These fields can be anything you want.
    public static final String COL_PHONE_NUMBER = "phone_number";

    // For database projection so order is consistent
    public static final String[] FIELDS = {
    	COL_ID,
	COL_PHONE_NUMBER,
	};

    /*
     * The SQL code that creates a Table for storing Exercises in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_PHONE_NUMBER + " TEXT NOT NULL,"
                    + ")";

    // Fields corresponding to database columns
    public long id = -1;
    public String phoneNumber;

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
        this.phoneNumber = cursor.getString(3);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContent() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_PHONE_NUMBER, phoneNumber);

        return values;
    }
}

