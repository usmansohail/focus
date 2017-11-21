package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.ScheduleAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

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

        if(getArguments() != null){
            int scheduleToDeleteID = getArguments().getInt(getString(R.string.schedule_id_to_delete));
            Schedule scheduleToDelete = Global.getInstance().getScheduleById(scheduleToDeleteID);
            Global.getInstance().removeSchedule(getContext(), scheduleToDelete);
            resetNotificationFlags();
            this.setArguments(null);
        }

        resetToolbar();

        //instantiate global views
        scheduleListView = layout.findViewById(R.id.schedule_list_view);
        addScheduleButton = addItemButton;

        Log.d("SchedulesFragment", "starting global getSchedules");

        Log.d("SchedulesFragment", "Creating new Schedule Adapter");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Schedules");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<Schedule> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Schedule schedule = new Schedule(id);
                    temp.add(schedule);
                }
                mAdapter = new ScheduleAdapter(getActivity(), temp);
                // attach the adapter to the expandable list view
                scheduleListView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.getInstance().synchAll(getContext());
                Fragment frag = CreateScheduleFragment.newInstance();
                Bundle args = new Bundle();
                Schedule sched =
                        new Schedule("", new Vector<TimeBlock>(), false);
                Global.getInstance().addSchedule(getContext(), sched);
                Global.getInstance().synchSchedules(getContext());
                Log.d("SchedulesFragment", "Adding scheduleID: " + sched.getId() + " to args");
                args.putInt(getString(R.string.scheduleKey), sched.getId());
                args.putBoolean(getString(R.string.schedule_is_new), true);
                frag.setArguments(args);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return layout;
    }

    public void resetNotificationFlags(){
        Vector<Boolean> blockingProfiles = new Vector<>();
        Vector<Schedule> schedules = Global.getInstance().getSchedules();
        for(int i=0; i<schedules.size(); i++){
            blockingProfiles.add(schedules.get(i).isBlocking());
        }
        Global.getInstance().setScheduleFlags(getContext(), blockingProfiles);
    }
}