package com.miawoltn.emergencydispatch.setup;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.util.Operations;
import com.miawoltn.emergencydispatch.util.UserData;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NameNumberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NameNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameNumberFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String name, number, address;
    boolean nameFlag, numberFlag, addressFlag;
    public static UserData userData;
    private OnFragmentInteractionListener mListener;

    private TextView txtName, txtNumber, txtAddress;
    private Button next;
    private TextView skip, prev;

    Fragment fragment;
    FragmentTransaction fragmentTransaction;

    public NameNumberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NameNumberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NameNumberFragment newInstance(String param1, String param2) {
        NameNumberFragment fragment = new NameNumberFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameFlag = numberFlag = addressFlag = false;
        View v = getView();
        txtName = (EditText) v.findViewById(R.id.name);
        txtNumber = (EditText) v.findViewById(R.id.number);
        txtAddress = (EditText) v.findViewById(R.id.address);
        next = (Button) v.findViewById(R.id.done);
        skip = (TextView) v.findViewById(R.id.skip);
        prev = (TextView) v.findViewById(R.id.back);

        if(userData != null) {
            txtName.setText(userData.getName());
            txtNumber.setText(userData.getNumber());
            txtAddress.setText(userData.getAddress());
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String text = nameNumber.getText().toString();
                Operations.writeLog(getContext(), Operations.NUMBER_USER, text);*/
                if(!Operations.isDeviceConnected(getContext())) {
                    Toast.makeText(getContext(),"Device is offline.", Toast.LENGTH_SHORT).show();
                    return;
                }
                name = txtName.getText().toString();
                number = txtNumber.getText().toString();
                address = txtAddress.getText().toString();
                if(name.trim().length() > 0) {
                    if(number.trim().length() > 0) {
                        if(address.trim().length() > 0) {
                            DispatchFetchFragment.userData = new UserData(name,number,address);
                            nextFragment();
                        }else {
                            txtAddress.setError("Name is required");
                        }
                    }else {
                        txtNumber.setError("Name is required");
                    }
                } else {
                    txtName.setError("Name is required");
                }


            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Operations.requestDispatchNumbers(getContext());
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousFragment();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.toString();
            if(str.trim().length() > 0) {
                next.setEnabled(true);
            }
            else {
                next.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name_number, container, false);
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

    public void nextFragment() {
        fragment = new DispatchFetchFragment();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment, fragment, "fetch_dispatch");
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void previousFragment() {
        fragment = new InstructionsActivityFragment();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment, fragment, "instructions");
        fragmentTransaction.commitAllowingStateLoss();
    }
}
