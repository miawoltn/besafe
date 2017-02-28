package com.miawoltn.emergencydispatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Amin on 2/24/2017.
 */

public class Contact {

    static Logger contactsDbHelper;
    static Cursor phone, dispatch;
    private Context context;
    private static Contact contact = null;
    Cursor cur;
    private Contact(Context context) {
        this.context = context;
        phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        contactsDbHelper = Logger.getInstance(context);
        cur = ((Logger)this.getContactDbHelper()).selectAllContacts();
    }

    public static Contact getInstance(Context context) {
        if(contact == null) {
            contact = new Contact(context);
            return  contact;
        }
        return contact;
    }


    public SQLiteOpenHelper getContactDbHelper() {
        return contactsDbHelper;
    }

    public List<String> getNumbers() {
        List<String> numbers = new ArrayList<>();
        cur = ((Logger)this.getContactDbHelper()).selectAllContacts();
        while (cur.moveToNext()) {

            numbers.add(cur.getString(2));

        }

        return numbers;
    }

    public List<Integer> getNumberIds() {
        List<Integer> numberIds = new ArrayList<>();
        cur = ((Logger)this.getContactDbHelper()).selectAllContacts();
        while (cur.moveToNext()) {
            Log.i("getNumbers","has number");
            numberIds.add(cur.getInt(0));
            Log.i("The id",Integer.toString(cur.getInt(0)));
        }

        return numberIds;
    }
    public static class UserContactModel {
        String name;
        String number;
        boolean selected;

        public void setName(String name) {
            this.name = name;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }

        public boolean getSelected() {
            return selected;
        }
    }



/*

    public class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "dipatch_contacts";
        public static final String COLUMN_NAME_NAME = "contact_name";
        public static final String COLUMN_NAME_NUMBER = "contact_number";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";
    }

    public class ContactsDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "app.db";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                        ContactEntry._ID + " INTEGER PRIMARY KEY," +
                        ContactEntry.COLUMN_NAME_NAME + " TEXT," +
                        ContactEntry.COLUMN_NAME_NUMBER + " TEXT,"+
                        ContactEntry.COLUMN_NAME_TIME_STAMP + " LONG);";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

        public ContactsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            Log.i("TAG:",DATABASE_NAME+" db created");
          // Toast.makeText(context,"db created",Toast.LENGTH_SHORT).show();

        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public long insert( String name, String number, long timeStamp) {
            // Gets the data repository in write mode
            SQLiteDatabase db = getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(ContactEntry.COLUMN_NAME_NAME, name);
            values.put(ContactEntry.COLUMN_NAME_NUMBER, number);
            values.put(ContactEntry.COLUMN_NAME_TIME_STAMP, timeStamp);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(ContactEntry.TABLE_NAME, null, values);
            return newRowId;
        }

        public Cursor selectAll() {
            SQLiteDatabase db = getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ContactEntry._ID,
                    ContactEntry.COLUMN_NAME_NAME,
                    ContactEntry.COLUMN_NAME_NUMBER,
                    ContactEntry.COLUMN_NAME_TIME_STAMP
            };


            // How you want the results sorted in the resulting Cursor
            String sortOrder =   ContactEntry.COLUMN_NAME_NAME + " ASC";

            Cursor cursor = db.query(
                    ContactEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            return cursor;
        }

        public Cursor query(String name) {
            SQLiteDatabase db = getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ContactEntry._ID,
                    ContactEntry.COLUMN_NAME_NAME,
                    ContactEntry.COLUMN_NAME_NUMBER,
                    ContactEntry.COLUMN_NAME_TIME_STAMP
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = ContactEntry.COLUMN_NAME_NAME + " = ?" ;
            String[] selectionArgs = { name, name };

            // How you want the results sorted in the resulting Cursor
            String sortOrder =  ContactEntry.COLUMN_NAME_NAME + " DESC";

            Cursor cursor = db.query(
                    ContactEntry.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            return cursor;
        }

        public void delete(String name) {
            SQLiteDatabase db = getWritableDatabase();
            // Define 'where' part of query.
            String selection = ContactEntry.COLUMN_NAME_NAME + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { name };
            // Issue SQL statement.
            db.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
        }
    }
*/


}
