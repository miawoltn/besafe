package com.miawoltn.emergencydispatch;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DispatchNumbersActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private List<Fragment> sectionViews = new ArrayList<>();
    private final int PAGES = 2;
    private final String PAGE_1_TITLE = "Dispatch Numbers";
    private final String PAGE_2_TITLE = "Contact List";


    static ContactListAdapter contactListAdapter, dispatchNumbersAdapter;
    static ListView contactListView, dispatchListView;
    static List<UserContactModel> contactsTempList, dispatchTempList;
    static ArrayList<UserContactModel> contactsListArray, dispatchList;
    static UserContactModel userContactModel;
    static Cursor phone, dispatch;
    SearchView searchView;
    ContentResolver contentResolver;
    ContactsDbHelper contactsDbHelper;
    static DispatchListLoader dispatchListLoader;
    static ContactListLoader contactListLoader;
    FloatingActionButton contactListFab, dispatchListFab;
    int currentPage = 0;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_numbers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the contactListAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionViews.addAll(Arrays.asList(DispatchNumbersFragment.newInstance(), ContactListFragment.newInstance()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        contactsTempList = new ArrayList<>();
        dispatchTempList = new ArrayList<>();

        // Set up the ViewPager with the sections contactListAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = sectionViews.get(position);
                if(fragment instanceof DispatchNumbersFragment) {
                    contactListFab.setVisibility(View.INVISIBLE);
                    dispatchListFab.setVisibility(View.VISIBLE);
                    currentPage = position;
                   // updateAdapter(dispatchListView, dispatchTempList);
                }
                if(fragment instanceof  ContactListFragment) {
                    dispatchListFab.setVisibility(View.INVISIBLE);
                    contactListFab.setVisibility(View.VISIBLE);
                    currentPage = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




        dispatchListLoader = new DispatchListLoader();
        contactListLoader = new ContactListLoader();


        contactsListArray = new ArrayList<>();
        dispatchList = new ArrayList<>();
        contentResolver = getContentResolver();
        contactsDbHelper = new ContactsDbHelper(DispatchNumbersActivity.this);
        phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");


        searchView = (SearchView)findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(currentPage == 1) {
                    contactListAdapter.filter(newText);
                }
                else {
                    dispatchNumbersAdapter.filter(newText);
                }

                return false;
            }
        });



        contactListFab = (FloatingActionButton) findViewById(R.id.fab);
        contactListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsTempList.clear();
                dispatchTempList.clear();
                contactsTempList.addAll(contactsListArray);
                dispatchTempList.addAll(dispatchList);
                for (UserContactModel userContact : contactsListArray) {
                    if(userContact.getSelected()) {

                        long id = contactsDbHelper.insert(userContact.getId(),userContact.getName(),userContact.getNumber(), System.currentTimeMillis());

                        Snackbar.make(view, "Selected item found. "+ id, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        contactsTempList.remove(userContact);
                        userContact.setSelected(!userContact.getSelected());
                        dispatchTempList.add(userContact);
                    }
                }

                updateAdapter(contactListView, contactsTempList);
                updateAdapter(dispatchListView, dispatchTempList);
            }
        });

        dispatchListFab = (FloatingActionButton) findViewById(R.id.fab1);
        dispatchListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTempList.clear();
                contactsTempList.clear();
                dispatchTempList.addAll(dispatchList);
                contactsTempList.addAll(contactsListArray);
                for (UserContactModel userContact : dispatchList) {
                    if(userContact.getSelected()) {

                         contactsDbHelper.delete(userContact.getName());
                        Log.i("TAG:","Selected item found.");
                        dispatchTempList.remove(userContact);
                        userContact.setSelected(!userContact.getSelected());
                        contactsTempList.add(userContact);
                    }
                }
                updateAdapter(contactListView, contactsTempList);
                updateAdapter(dispatchListView, dispatchTempList);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dispatch_numbers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DispatchNumbersFragment extends Fragment {

        static UserContactModel userContact;
        public DispatchNumbersFragment() {
            userContact = new UserContactModel();
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DispatchNumbersFragment newInstance() {
            DispatchNumbersFragment fragment = new DispatchNumbersFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dispatch_numbers_fragment, container, false);
            dispatchListView = (ListView)rootView.findViewById(R.id.dispatch);

            dispatchListLoader.execute();
            if(dispatchList == null || dispatchList.size() == 0) {

            }
            return rootView;
        }



    }


    class DispatchListLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            dispatchNumbersAdapter = new ContactListAdapter(dispatchList, DispatchNumbersActivity.this);
            dispatchListView.setAdapter(dispatchNumbersAdapter);
            dispatchListView.setFastScrollEnabled(true);
            dispatchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserContactModel userContact = dispatchList.get(position);
                    userContact.setSelected(!userContact.getSelected());
                    dispatchTempList = new ArrayList<>();
                    dispatchTempList.addAll(dispatchList);
                    ((ContactListAdapter) dispatchListView.getAdapter()).update(dispatchTempList);
                }
            });



        }

        @Override
        protected Void doInBackground(Void... params) {

            dispatch = contactsDbHelper.selectAll();
            if(dispatch != null) {
                if(dispatch.getCount() > 0) {
                    while (dispatch.moveToNext()) {
                        String id = dispatch.getString(dispatch.getColumnIndex(ContactEntry.COLUMN_NAME_ID));
                        String name = dispatch.getString(dispatch.getColumnIndex(ContactEntry.COLUMN_NAME_NAME));
                        String number = dispatch.getString(dispatch.getColumnIndex(ContactEntry.COLUMN_NAME_NUMBER));

                        userContactModel = new UserContactModel();
                        userContactModel.setId(id);
                        userContactModel.setName(name);
                        userContactModel.setNumber(number);
                        userContactModel.setSelected(false);
                        dispatchList.add(userContactModel);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContactListFragment extends Fragment {



        public ContactListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactListFragment newInstance() {
            ContactListFragment fragment = new ContactListFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.contactlist_fragment, container, false);
            contactListView = (ListView)rootView.findViewById(R.id.contacts);

            contactListLoader.execute();
            return rootView;
        }
    }


    class ContactListLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            contactsTempList = new ArrayList<>();
            contactsTempList.addAll(contactsListArray);
            for (UserContactModel uc : contactsListArray){
                for(UserContactModel ucModel : dispatchList) {
                    if(uc.getId().equals(ucModel.getId())) {
                        Log.i("Tag","found added contact");
                            contactsTempList.remove(uc);
                    }
                }

            }
            contactsListArray.clear();
            contactsListArray.addAll(contactsTempList);

            contactListAdapter = new ContactListAdapter(contactsListArray, DispatchNumbersActivity.this);
            contactListView.setAdapter(contactListAdapter);
            contactListView.setFastScrollEnabled(true);
            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserContactModel userContact = contactsListArray.get(position);
                    userContact.setSelected(!userContact.getSelected());
                    contactsTempList = new ArrayList<>();
                    contactsTempList.addAll(contactsListArray);
                    ((ContactListAdapter) contactListView.getAdapter()).update(contactsTempList);
                }
            });




        }

        @Override
        protected Void doInBackground(Void... params) {
            if(phone != null) {
                if(phone.getCount() > 0) {
                    while (phone.moveToNext()) {
                        String id = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                        String name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        userContactModel = new UserContactModel();
                        userContactModel.setId(id);
                        userContactModel.setName(name);
                        userContactModel.setNumber(number);
                        userContactModel.setSelected(false);
                        contactsListArray.add(userContactModel);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ContactListFragment (defined as a static inner class below).
            return sectionViews.get(position);

        }

        @Override
        public int getCount() {
            // Show total pages.
            return PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return PAGE_1_TITLE;
                case 1:
                    return PAGE_2_TITLE;
            }
            return null;
        }
    }



    public class ContactListAdapter extends BaseAdapter {

        public List<UserContactModel> contacts;
        private ArrayList<UserContactModel> userContacts;
        Context context;
        ContactHolder contactHolder;

        public ContactListAdapter(List<UserContactModel> contacts, Context context) {
            this.contacts = contacts;
            this.context = context;
            userContacts = new ArrayList<>();
            userContacts.addAll(contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater=getLayoutInflater();
                view = inflater.inflate(R.layout.contact_layout, parent, false);
            }

            contactHolder = new ContactHolder(view);
            contactHolder.populateFrom(contacts.get(position));
            view.setTag(contactHolder);
            return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        public void filter(String text) {
            text = text.toLowerCase(Locale.getDefault());
            contacts.clear();
            if(text.length() == 0) {
                contacts.addAll(userContacts);
            }
            else {
                for (UserContactModel userContact : userContacts) {
                    if(userContact.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                        contacts.add(userContact);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void update(List<UserContactModel> userContact) {
            contacts.clear();
            contacts.addAll(userContact);
            notifyDataSetChanged();
        }
    }

    public void updateAdapter(ListView baseAdapter, List<UserContactModel> userContactModels) {
        if(baseAdapter.getAdapter() != null) {
            ((ContactListAdapter) baseAdapter.getAdapter()).update(userContactModels);
        }else {
            ContactListAdapter contactListAdapter = new ContactListAdapter( userContactModels, DispatchNumbersActivity.this);
            baseAdapter.setAdapter(contactListAdapter);
        }
    }

    static class ContactHolder {

        private TextView id = null;
        private TextView name = null;
        private TextView number = null;
        private CheckBox selected = null;
        private View row = null;

        ContactHolder(View row) {
            this.row = row;
            id = (TextView) row.findViewById(R.id.contact_id);
            name = (TextView) row.findViewById(R.id.contact_name);
            number = (TextView) row.findViewById(R.id.contact_number);
            selected = (CheckBox) row.findViewById(R.id.selected);
        }

        void populateFrom(UserContactModel helper) {
            id.setText(helper.getId());
            name.setText(helper.getName());
            number.setText(helper.getNumber());
            selected.setChecked(helper.getSelected());
        }
    }

    public static class UserContactModel {

        String id;
        String name;
        String number;
        boolean selected;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getId() {
            return  id;
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




    public class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "dipatch_contacts";
        public static final String COLUMN_NAME_ID = "contact_id";
        public static final String COLUMN_NAME_NAME = "contact_name";
        public static final String COLUMN_NAME_NUMBER = "contact_number";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";
    }

    public class ContactsDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "contacts.db";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                        ContactEntry._ID + " INTEGER PRIMARY KEY," +
                        ContactEntry.COLUMN_NAME_ID + " TEXT," +
                        ContactEntry.COLUMN_NAME_NAME + " TEXT," +
                        ContactEntry.COLUMN_NAME_NUMBER + " TEXT,"+
                        ContactEntry.COLUMN_NAME_TIME_STAMP + " LONG);";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

        public ContactsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
            Toast.makeText(DispatchNumbersActivity.this,"db created",Toast.LENGTH_SHORT).show();

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

        public long insert(String id, String name, String number, long timeStamp) {
            // Gets the data repository in write mode
            SQLiteDatabase db = getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(ContactEntry.COLUMN_NAME_ID, id);
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
                    ContactEntry.COLUMN_NAME_ID,
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
                    ContactEntry.COLUMN_NAME_ID,
                    ContactEntry.COLUMN_NAME_NAME,
                    ContactEntry.COLUMN_NAME_NUMBER,
                    ContactEntry.COLUMN_NAME_TIME_STAMP
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = ContactEntry.COLUMN_NAME_NAME + " = ? or " + ContactEntry.COLUMN_NAME_ID + " = ?" ;
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
}
