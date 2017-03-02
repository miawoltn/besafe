package com.miawoltn.emergencydispatch.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.os.*;
import android.util.Log;

import com.miawoltn.emergencydispatch.util.Operations;

import java.util.List;

/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class Logger extends SQLiteOpenHelper {

    static Context context;
    private static Logger instance = null;
    private long lastMessageId = 0;
    private long timeOfLastSOS = 0;
    private Handler delayHandler;
    private long DELAY_DURATION = 1000 * 60;
    private String FAIL_SAFE_DISABLED = "FAIL_SAFE_DISABLED";
    private String rawQuery = "Select * from "+ ContactEntry.TABLE_NAME + " c INNER JOIN "
            + MessageContactEntry.TABLE_NAME + " mc on c._id = mc.contact_id JOIN "
            + MessageEntry.TABLE_NAME + " m on m._id = mc.message_id WHERE m._id = ?";

    private class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "dipatch_messages";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_COORDINATES = "coordinates";
        public static final String COLUMN_NAME_LOCATION_DETAILS = "location_details";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +  TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_MESSAGE + " TEXT," +
                        COLUMN_NAME_COORDINATES + " TEXT,"+
                        COLUMN_NAME_LOCATION_DETAILS + " TEXT,"+
                        COLUMN_NAME_TIME_STAMP + " LONG);";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "dipatch_contacts";
        public static final String COLUMN_NAME_NAME = "contact_name";
        public static final String COLUMN_NAME_NUMBER = "contact_number";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_NUMBER + " TEXT,"+
                        COLUMN_NAME_TIME_STAMP + " LONG);";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    private class MessageContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages_contacts";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_DISPATCH_ID = "contact_id";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +  TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_MESSAGE_ID + " LONG," +
                        COLUMN_DISPATCH_ID + " INTEGER);";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "app.db"; //context.getString(R.string.db_name);
    public static final int MESSAGE_ENTRY = 1;
    public static final int MESSAGE_CONTACT_ENTRY = 2;


    private Logger(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static Logger getInstance(Context context) {
        if(instance == null) {
            instance = new Logger(context);
            return instance;
        }

        return instance;
    }

    public boolean log(com.miawoltn.emergencydispatch.util.Message message, List<Integer> contactIds) {
        long message_id = insert(MESSAGE_ENTRY,message.getDistressType(), String.format("%f,%f",message.getLongitude(),message.getLatitude()), message.getLocationDetails(), System.currentTimeMillis());
        for(int contactId : contactIds) {
            insert(MESSAGE_CONTACT_ENTRY,message_id,contactId);
        }
        lastMessageId = message_id;
        timeOfLastSOS = System.currentTimeMillis();
        delayHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                resetFailSafe();
                Operations.sendBroadcast(context, FAIL_SAFE_DISABLED); //context.sendBroadcast(new Intent("FAIL_SAFE_DISABLED"));
                Log.i("Fail Safe Reset","fail safe has been reset");
            }
        };

        delayHandler.postDelayed(runnable, DELAY_DURATION);

        if(message_id != -1)
            return true;
        else
            return false;
    }



    public void resetFailSafe() {
        lastMessageId = 0;
        timeOfLastSOS = 0;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public long getTimeOfLastSOS() {
        return timeOfLastSOS;
    }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ContactEntry.SQL_CREATE_ENTRIES);
            db.execSQL(MessageEntry.SQL_CREATE_ENTRIES);
            db.execSQL(MessageContactEntry.SQL_CREATE_ENTRIES);
            Log.i("Logger:","tables created");
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(ContactEntry.SQL_DELETE_ENTRIES);
            db.execSQL(MessageEntry.SQL_DELETE_ENTRIES);
            db.execSQL(MessageContactEntry.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        private long insert(int baseColumns, Object... objectses) {
            // Gets the data repository in write mode
            SQLiteDatabase db = getWritableDatabase();

            // Create a new map of values, where column names are the keys
            if(baseColumns == MESSAGE_ENTRY) {
                ContentValues values = new ContentValues();
                values.put(MessageEntry.COLUMN_NAME_MESSAGE, String.valueOf(objectses[0]));
                values.put(MessageEntry.COLUMN_NAME_COORDINATES, String.valueOf(objectses[1]));
                values.put(MessageEntry.COLUMN_NAME_LOCATION_DETAILS, String.valueOf(objectses[2]));
                values.put(MessageEntry.COLUMN_NAME_TIME_STAMP, Long.valueOf(objectses[3].toString()));

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(MessageEntry.TABLE_NAME, null, values);
                return newRowId;
            } else  {
                ContentValues values = new ContentValues();
                values.put(MessageContactEntry.COLUMN_MESSAGE_ID, Integer.valueOf(objectses[0].toString()));
                values.put(MessageContactEntry.COLUMN_DISPATCH_ID, Integer.valueOf(objectses[1].toString()));

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(MessageContactEntry.TABLE_NAME, null, values);
                return newRowId;
            }
        }

        public Cursor selectAll(int tableName) {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = {};
            String TABLE_NAME = "";
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            if(tableName == MESSAGE_ENTRY) {
                TABLE_NAME = MessageEntry.TABLE_NAME;
                projection = new String[] {
                        MessageEntry._ID,
                        MessageEntry.COLUMN_NAME_MESSAGE,
                        MessageEntry.COLUMN_NAME_COORDINATES,
                        MessageEntry.COLUMN_NAME_LOCATION_DETAILS,
                        MessageEntry.COLUMN_NAME_TIME_STAMP
                };
            }else {
                TABLE_NAME = MessageContactEntry.TABLE_NAME;
                projection = new String[] {
                        MessageContactEntry._ID,
                        MessageContactEntry.COLUMN_MESSAGE_ID,
                        MessageContactEntry.COLUMN_DISPATCH_ID
                };
            }



            // How you want the results sorted in the resulting Cursor
            String sortOrder =   MessageEntry.COLUMN_NAME_TIME_STAMP + " ASC";

            Cursor cursor = db.query(
                    TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            return cursor;
        }

        public Cursor queryMessageContact(String id) {
            SQLiteDatabase db = getReadableDatabase();

           Cursor cursor = db.rawQuery(rawQuery, new String[]{id});

            return cursor;
        }

        public void delete(String id) {
            SQLiteDatabase db = getWritableDatabase();
            // Define 'where' part of query.
            String selection =  MessageContactEntry._ID + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { id };
            // Issue SQL statement.
            db.delete( MessageContactEntry.TABLE_NAME, selection, selectionArgs);
        }


    public long insertContact( String name, String number, long timeStamp) {
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

    public Cursor selectAllContacts() {
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

    public Cursor queryContact(String name) {
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
        String selection = ContactEntry.COLUMN_NAME_NAME + " = ? or" + ContactEntry._ID + " = ?" ;
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

    public void deleteContact(String name) {
        SQLiteDatabase db = getWritableDatabase();
        // Define 'where' part of query.
        String selection = ContactEntry.COLUMN_NAME_NAME + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { name };
        // Issue SQL statement.
        db.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
    }
}
