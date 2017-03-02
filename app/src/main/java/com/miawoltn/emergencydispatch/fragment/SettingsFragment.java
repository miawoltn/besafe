package com.miawoltn.emergencydispatch.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.miawoltn.emergencydispatch.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Switch sms_notification;
    Switch control_unit_notification;
    Switch fail_safe_notification;
    Switch tracking_notification;

    private static boolean isEnable_sms_notification = true;
    private static boolean isEnable_control_unit = true;
    private static boolean isEnable_fail_safe = true;
    private static boolean isTracking_enabled = true;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        sms_notification = (Switch) v.findViewById(R.id.sms_notification);
        control_unit_notification = (Switch) v.findViewById(R.id.control_unit_notification);
        fail_safe_notification = (Switch) v.findViewById(R.id.fail_safe_notification);
        tracking_notification = (Switch) v.findViewById(R.id.tracking_notification);

        sms_notification.setChecked(isEnable_sms_notification);
        control_unit_notification.setChecked(isEnable_control_unit);
        fail_safe_notification.setChecked(isEnable_fail_safe);
        tracking_notification.setChecked(isTracking_enabled);

        sms_notification.setOnClickListener(onClickListener);
        control_unit_notification.setOnClickListener(onClickListener);
        fail_safe_notification.setOnClickListener(onClickListener);
        tracking_notification.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sms_notification:
                  isEnable_sms_notification = sms_notification.isChecked();
                    break;
                case R.id.control_unit_notification:
                    isEnable_control_unit = control_unit_notification.isChecked();
                    break;
                case R.id.fail_safe_notification:
                    isEnable_fail_safe = fail_safe_notification.isChecked();
                    break;
                case R.id.tracking_notification:
                    isTracking_enabled = tracking_notification.isEnabled();

            }
        }
    };

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


    public static boolean isSMSNotificationEnabled() {
        return isEnable_sms_notification;
    }

    public static boolean isControUnitNotificationEnabled() {
        return isEnable_control_unit;
    }

    public static boolean isFailSafeNotificationEnabled() {
        return isEnable_fail_safe;
    }

    public static boolean isTrackingNotificationEnabled() {
        return isTracking_enabled;
    }
}
