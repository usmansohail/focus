package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.ScheduleAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Schedule;

import java.util.Vector;

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
    ListView scheduleListView;
    ImageButton addScheduleButton;

    //RecyclerView Adapter
    ScheduleAdapter mAdapter;

    //Schedule List
    Vector<Schedule> schedules;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_schedules, container, false);

        //instantiate global views
        scheduleListView = layout.findViewById(R.id.schedule_list_view);
        addScheduleButton = addItemButton;

        //TODO Implement schedulesRecyclerView

        schedules = Global.getInstance().getSchedules(getActivity());

        mAdapter = new ScheduleAdapter(getActivity(), schedules);
        // attach the adapter to the expandable list view
        scheduleListView.setAdapter(mAdapter);

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
