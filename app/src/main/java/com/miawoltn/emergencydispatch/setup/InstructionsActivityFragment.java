package com.miawoltn.emergencydispatch.setup;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miawoltn.emergencydispatch.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class InstructionsActivityFragment extends Fragment {

    public InstructionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instructions, container, false);
    }
}
