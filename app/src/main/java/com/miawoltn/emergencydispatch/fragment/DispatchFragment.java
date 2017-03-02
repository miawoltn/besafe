package com.miawoltn.emergencydispatch.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.miawoltn.emergencydispatch.core.Logger;
import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.core.Contact;
import com.miawoltn.emergencydispatch.core.Contact.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DispatchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DispatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class DispatchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    static ContactListAdapter contactListAdapter, dispatchNumbersAdapter;
    static ListView contactListView, dispatchListView;
    static List<UserContactModel> contactsTempList, dispatchTempList;
    static ArrayList<UserContactModel> contactsListArray, dispatchList;
    static UserContactModel userContactModel;
    static Cursor phone, dispatch;
    SearchView searchView;
    ContentResolver contentResolver;
    Logger contactsDbHelper;
    static DispatchListLoader dispatchListLoader;
    static ContactListLoader contactListLoader;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private List<Fragment> sectionViews = new ArrayList<>();
    private final int PAGES = 2;
    private final String PAGE_1_TITLE = "Dispatch Numbers";
    private final String PAGE_2_TITLE = "Contact List";
    private ViewPager mViewPager;
    FloatingActionButton contactListFab, dispatchListFab;
    int currentPage = 0;
    private Contact contact;

    private OnFragmentInteractionListener mListener;

    public DispatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DispatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DispatchFragment newInstance(String param1, String param2) {
        DispatchFragment fragment = new DispatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dispatch_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
         super.onActivityCreated(savedInstanceState);

        View view = getView();
        contact = Contact.getInstance(getContext());
        contactsTempList = new ArrayList<>();
        dispatchTempList = new ArrayList<>();
        sectionViews.addAll(Arrays.asList( DispatchNumbersFragment.newInstance(),  ContactListFragment.newInstance()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.container);
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
                    // updateAdapter(historyListView, dispatchTempList);
                }
                if(fragment instanceof ContactListFragment) {
                    dispatchListFab.setVisibility(View.INVISIBLE);
                    contactListFab.setVisibility(View.VISIBLE);
                    currentPage = position;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchView = (SearchView)view.findViewById(R.id.search);
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

        contactListFab = (FloatingActionButton) view.findViewById(R.id.fab);
        contactListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsTempList.clear();
                dispatchTempList.clear();
                contactsTempList.addAll(contactsListArray);
                dispatchTempList.addAll(dispatchList);
                for (UserContactModel userContact : contactsListArray) {
                    if(userContact.getSelected()) {

                        long id = contactsDbHelper.insertContact( userContact.getName(),userContact.getNumber(), System.currentTimeMillis());

                        Log.i("TAG:","Selected item found. "+ id);
                        contactsTempList.remove(userContact);
                        userContact.setSelected(!userContact.getSelected());
                        dispatchTempList.add(userContact);
                    }
                }

                updateAdapter(contactListView, contactsTempList);
                updateAdapter(dispatchListView, dispatchTempList);
            }
        });

        dispatchListFab = (FloatingActionButton) view.findViewById(R.id.fab1);
        dispatchListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTempList.clear();
                contactsTempList.clear();
                dispatchTempList.addAll(dispatchList);
                contactsTempList.addAll(contactsListArray);
                for (UserContactModel userContact : dispatchList) {
                    if(userContact.getSelected()) {

                        contactsDbHelper.deleteContact(userContact.getName());
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


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        dispatchListLoader = new DispatchListLoader();
        contactListLoader = new ContactListLoader();

        contactsListArray = new ArrayList<>();
        dispatchList = new ArrayList<>();
        contentResolver = getActivity().getContentResolver();
        contactsDbHelper = (Logger)contact.getContactDbHelper();
        phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

    }

    @Override
    public void onDestroyView() {
        ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(R.id.container);
        viewGroup.removeAllViews();
        super.onDestroyView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

            dispatchNumbersAdapter = new ContactListAdapter(dispatchList, getContext());
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

            dispatch = contactsDbHelper.selectAllContacts();
            if(dispatch != null) {
                if(dispatch.getCount() > 0) {
                    while (dispatch.moveToNext()) {
                        String name = dispatch.getString(dispatch.getColumnIndex(Logger.ContactEntry.COLUMN_NAME_NAME));
                        String number = dispatch.getString(dispatch.getColumnIndex(Logger.ContactEntry.COLUMN_NAME_NUMBER));

                        userContactModel = new UserContactModel();
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
                    if(uc.getNumber().equals(ucModel.getNumber())) {
                        Log.i("Tag","found added contact");
                        contactsTempList.remove(uc);
                    }
                }

            }
            contactsListArray.clear();
            contactsListArray.addAll(contactsTempList);

            contactListAdapter = new ContactListAdapter(contactsListArray, getContext());
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
                LayoutInflater inflater = getActivity().getLayoutInflater();
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
                for (Contact.UserContactModel userContact : userContacts) {
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
            ContactListAdapter contactListAdapter = new  ContactListAdapter(userContactModels, getContext());
            baseAdapter.setAdapter(contactListAdapter);
        }
    }

    static class ContactHolder {

        // private TextView id = null;
        private TextView name = null;
        private TextView number = null;
        private CheckBox selected = null;
        private View row = null;

        ContactHolder(View row) {
            this.row = row;
            //id = (TextView) row.findViewById(R.id.contact_id);
            name = (TextView) row.findViewById(R.id.contact_name);
            number = (TextView) row.findViewById(R.id.contact_number);
            selected = (CheckBox) row.findViewById(R.id.selected);
        }

        void populateFrom(Contact.UserContactModel helper) {
            name.setText(helper.getName());
            number.setText(helper.getNumber());
            selected.setChecked(helper.getSelected());
        }
    }
}
