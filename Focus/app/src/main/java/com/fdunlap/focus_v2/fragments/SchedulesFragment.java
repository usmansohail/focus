package com.fdunlap.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.adapters.ScheduleAdapter;

public class SchedulesFragment extends BaseFragment {
    public SchedulesFragment() {
        // Required empty public constructor
    }

    public static SchedulesFragment newInstance() {
        SchedulesFragment fragment = new SchedulesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showAddButton(true);
    }

    //Global views
    RecyclerView schedulesRecyclerView;
    ImageButton addScheduleButton;

    //RecyclerView Adapter
    ScheduleAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_schedules, container, false);

        //instantiate global views
        schedulesRecyclerView = layout.findViewById(R.id.schedule_recycler_view);
        addScheduleButton = addItemButton;

        //TODO Implement schedulesRecyclerView
        mAdapter = new ScheduleAdapter();
        schedulesRecyclerView.setAdapter(mAdapter);

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, CreateScheduleFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return layout;
    }

}
