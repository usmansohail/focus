package com.fdunlap.focus_v2.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fdunlap.focus_v2.R;

public class CreateScheduleFragment extends BaseFragment {
    public CreateScheduleFragment() {
        // Required empty public constructor
    }

    public static CreateScheduleFragment newInstance() {
        CreateScheduleFragment fragment = new CreateScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showBackButton(true);
        showConfirmButton(true);
        setShowBottomBar(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_schedule, container, false);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //So right now this just gives a toast
                //TODO implement confirmation & validation of input.
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return layout;
    }

}
