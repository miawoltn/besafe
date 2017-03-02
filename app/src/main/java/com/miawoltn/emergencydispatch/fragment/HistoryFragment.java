package com.miawoltn.emergencydispatch.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miawoltn.emergencydispatch.core.Logger;
import com.miawoltn.emergencydispatch.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static HistoryListAdapter historyListAdapter;
    static ListView historyListView;
    static Cursor history, contact;
    static ArrayList<HistoryModel> historyModels;
    static HistoryModel historyModel;
    Logger historyDbHelper;
    static HistoryListLoader historyListLoader;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        container.removeAllViews();
        return inflater.inflate(R.layout.history_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyDbHelper = Logger.getInstance(getContext());
        historyModels = new ArrayList<>();
        historyListView = (ListView)getView().findViewById(R.id.dispatch_history);
        historyListLoader = new HistoryListLoader();
        historyListLoader.execute();
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
        /*if (context instanceof OnFragmentInteractionListener) {
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



    class HistoryListLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            historyListAdapter = new HistoryListAdapter(historyModels, getContext());
            historyListView.setAdapter(historyListAdapter);
            historyListView.setFastScrollEnabled(true);
            historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            history = historyDbHelper.selectAll(Logger.MESSAGE_ENTRY);


            if(history != null) {
                if(history.getCount() > 0) {
                    while (history.moveToNext()) {
                        int id = history.getInt(0);
                        String distress = history.getString(1);
                        String location = history.getString(2);
                        String location_details = history.getString(3);
                        long time_stamp = history.getLong(4);
                        StringBuilder stringBuilder = new StringBuilder();
                        contact = historyDbHelper.queryMessageContact(String.valueOf(id));
                    while (contact.moveToNext()) {
//                        int _id = contact.getInt(0);
//                        if(id == _id) {
                            stringBuilder.append(contact.getString(2));
                            if(!contact.isLast())
                            stringBuilder.append(",");
                        //}
                    }
                        historyModel = new HistoryModel();
                        historyModel.setId(id);
                        historyModel.setDate(time_stamp);
                        historyModel.setDistress(distress);
                        historyModel.setLocation(location);
                        historyModel.setLocation_details(location_details);
                        historyModel.setDispatch(stringBuilder.toString());
                        historyModels.add(historyModel);
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



    public class HistoryListAdapter extends BaseAdapter {

        public List<HistoryModel> historyModels;
        private ArrayList<HistoryModel> historyModelArrayList;
        Context context;
        HistoryHolder contactHolder;

        public HistoryListAdapter(List<HistoryModel> models, Context context) {
            this.historyModels = models;
            this.context = context;
            historyModelArrayList = new ArrayList<>();
            historyModelArrayList.addAll(models);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater=getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.history_item_layout, parent, false);
            }

            contactHolder = new HistoryHolder(view);
            contactHolder.populateFrom(historyModels.get(position));
            view.setTag(contactHolder);
            return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return historyModels.get(position);
        }

        @Override
        public int getCount() {
            return historyModels.size();
        }

        public void filter(String text) {
            text = text.toLowerCase(Locale.getDefault());
            historyModels.clear();
            if(text.length() == 0) {
                historyModels.addAll(historyModelArrayList);
            }
            else {
                for (HistoryModel historyModel : historyModelArrayList) {
                    if(historyModel.getDistress().toLowerCase(Locale.getDefault()).contains(text)) {
                        historyModels.add(historyModel);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void update(List<HistoryModel> historyModels) {
            this.historyModels.clear();
            this.historyModels.addAll(historyModels);
            notifyDataSetChanged();
        }
    }



    static class HistoryHolder {

         private TextView id = null;
        private TextView distress = null;
        private TextView location = null;
        private TextView details = null;
        private TextView dispatch = null;
        private TextView date = null;
        private TextView time = null;
        private View row = null;

        HistoryHolder(View row) {

            id = (TextView) row.findViewById(R.id.history_id);
            distress = (TextView) row.findViewById(R.id.sos);
            location = (TextView) row.findViewById(R.id.location);
            details = (TextView) row.findViewById(R.id.location_details);
            dispatch = (TextView) row.findViewById(R.id.dispatches);
            date =  (TextView) row.findViewById(R.id.date);
            time = (TextView) row.findViewById(R.id.time);
        }

        void populateFrom(HistoryModel helper) {
            id.setText(Integer.toString(helper.getId()));
            distress.setText(helper.getDistress());
            location.setText(helper.getLocation());
            details.setText(helper.getLocation_details());
            dispatch.setText(helper.getDispatch());
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(helper.getDate());
            int dt = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);
            int sec = calendar.get(Calendar.SECOND);
            date.setText(String.format("%d-%d-%d",dt,month+1,year));
            time.setText(String.format("%d:%d:%d",hour,min,sec));
        }
    }

    public static class HistoryModel {

        int id;
        String distress;
        String location;
        String location_details;
        String dispatch;
        long date;

        public void setId(int id) {
            this.id = id;
        }

       public void setDistress(String distress) {
           this.distress = distress;
       }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setLocation_details(String location_details) {
            this.location_details = location_details;
        }

        public void setDispatch(String dispatch) {
            this.dispatch = dispatch;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getId() {
            return  id;
        }

        public String getDistress() {
            return  distress;
        }

        public String getLocation() {
            return location;
        }

        public String getLocation_details() {
            return location_details;
        }

        public String getDispatch() {
            return dispatch;
        }
        public long getDate() {
            return  date;
        }
    }
}
