package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusTimer;

public class CreateTimerFragment extends BaseFragment {

    Spinner mHourSpinner;
    Spinner mMinuteSpinner;
    Spinner mSecondSpinner;

    ArrayAdapter<CharSequence> mHourSpinnerAdapter;
    ArrayAdapter<CharSequence> mMinuteSpinnerAdapter;
    ArrayAdapter<CharSequence> mSecondSpinnerAdapter;

    RecyclerView mProfileRecycler;
    ProfileAdapter mProfileAdapter;


    public CreateTimerFragment() {
        // Required empty public constructor
    }

    public static CreateTimerFragment newInstance() {
        CreateTimerFragment fragment = new CreateTimerFragment();
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
        View layout = inflater.inflate(R.layout.fragment_create_timer, container, false);

        mHourSpinner = layout.findViewById(R.id.create_timer_hour_spinner);
        mMinuteSpinner = layout.findViewById(R.id.create_timer_minute_spinner);
        mSecondSpinner = layout.findViewById(R.id.create_timer_second_spinner);

        mHourSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.timer_hours,
                        android.R.layout.simple_spinner_item);
        mHourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mMinuteSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        mMinuteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mSecondSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        mSecondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mHourSpinner.setAdapter(mHourSpinnerAdapter);
        mMinuteSpinner.setAdapter(mMinuteSpinnerAdapter);
        mSecondSpinner.setAdapter(mSecondSpinnerAdapter);

        mProfileRecycler = layout.findViewById(R.id.create_timer_profile_recycler);
        mProfileAdapter = new ProfileAdapter(Global.getInstance().getAllProfiles(getContext()), getContext(), true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mProfileRecycler.setLayoutManager(mLayoutManager);
        mProfileRecycler.setItemAnimator(new DefaultItemAnimator());
        mProfileRecycler.setAdapter(mProfileAdapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    FocusTimer ft = new FocusTimer("", getDuration(), mProfileAdapter.getCheckedProfiles());
                    Global.getInstance().addTimer(getContext(), ft);
                    getActivity().onBackPressed();
                }
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

    private Long getDuration() {
        return (long)
                (mHourSpinner.getSelectedItemPosition() * 3600000) +
                (mMinuteSpinner.getSelectedItemPosition() * 60000) +
                (mSecondSpinner.getSelectedItemPosition() * 1000);
    }

    private boolean validate() {

        if(mProfileAdapter.getCheckedProfiles().isEmpty()){
            Toast.makeText(getContext(), getString(R.string.timerNoSelectedProfiles), Toast.LENGTH_SHORT).show();
        } else if(!allZero()){
            Toast.makeText(getContext(), getString(R.string.timerAllValuesZero), Toast.LENGTH_SHORT).show();
        }

        return (!mProfileAdapter.getCheckedProfiles().isEmpty() && allZero());
    }

    private boolean allZero() {
        return getDuration() > 10*1000;
    }

}
