package com.miawoltn.emergencydispatch.setup;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.core.Logger;
import com.miawoltn.emergencydispatch.util.Operations;
import com.miawoltn.emergencydispatch.util.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.R.attr.data;
import static android.R.attr.supportsAssist;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DispatchFetchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DispatchFetchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DispatchFetchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Logger logger;
    public static UserData userData;

    private TextView name, number, address, back;
    private Button doneButton;

    private OnFragmentInteractionListener mListener;

    public DispatchFetchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DispatchFetchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DispatchFetchFragment newInstance(String param1, String param2) {
        DispatchFetchFragment fragment = new DispatchFetchFragment();
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
        logger = Logger.getInstance(getContext());
        View v = getView();
        name = (TextView) v.findViewById(R.id.nameText);
        number = (TextView) v.findViewById(R.id.numberText);
        address = (TextView) v.findViewById(R.id.addressText);
        doneButton = (Button) v.findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operations.postUserData(getContext(),userData);
                Operations.requestDispatchNumbers(getContext(), logger);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
        back = (TextView) v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameNumberFragment.userData = userData;
                previousFragment();
            }
        });
        if(userData != null) {
            name.setText(userData.getName());
            number.setText(userData.getNumber());
            address.setText(userData.getAddress());
        }
        final TextView mTextView = (TextView) v.findViewById(R.id.text);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dispatch_fetch, container, false);
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

    public void previousFragment() {
        Fragment fragment = new NameNumberFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment, fragment, "fetch_dispatch");
        fragmentTransaction.commitAllowingStateLoss();
    }

}
