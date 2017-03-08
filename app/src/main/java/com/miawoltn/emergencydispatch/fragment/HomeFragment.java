package com.miawoltn.emergencydispatch.fragment;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.core.SOSDispatcher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton fire;
    ImageButton robbery;
    ImageButton terrorist_attack;
    ImageButton murder;
    ImageButton accident;
    ImageButton suicide;
    ImageButton natural_disaster;

    LocationManager locationManager;
    SOSDispatcher sosDispatcher;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sosDispatcher = new SOSDispatcher(getContext());
        View v = getView();
        fire = (ImageButton) v.findViewById(R.id.w_fire);
        robbery = (ImageButton) v.findViewById(R.id.w_robbery);
        terrorist_attack = (ImageButton) v.findViewById(R.id.w_terrorist_attack);
        murder = (ImageButton) v.findViewById(R.id.w_murder);
        accident = (ImageButton) v.findViewById(R.id.w_accident);
        suicide = (ImageButton) v.findViewById(R.id.w_suicide);
        natural_disaster = (ImageButton) v.findViewById(R.id.w_natural_disaster);

        fire.setOnClickListener(onClickListener);
        robbery.setOnClickListener(onClickListener);
        terrorist_attack.setOnClickListener(onClickListener);
        murder.setOnClickListener(onClickListener);
        accident.setOnClickListener(onClickListener);
        suicide.setOnClickListener(onClickListener);
        natural_disaster.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.w_fire:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Fire);
                    break;
                case R.id.w_robbery:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Robbery);
                    break;
                case R.id.w_terrorist_attack:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Terror_Attack);
                     break;
                case R.id.w_murder:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Murder);
                    break;
                case R.id.w_accident:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Accident);
                    break;
                case R.id.w_suicide:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Suicide);
                    break;
                case R.id.w_natural_disaster:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Natural_Disaster);
                    break;
                case R.id.emergency_others:
                    sosDispatcher.Dispatch(SOSDispatcher.DistressType.Emergency_Others);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_main, container, false);
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
}
