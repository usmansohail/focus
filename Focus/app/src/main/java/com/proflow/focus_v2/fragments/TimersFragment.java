package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.adapters.TimerAdapter;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TimersFragment extends BaseFragment {
    public TimersFragment() {
        // Required empty public constructor
    }

    public static TimersFragment newInstance() {
        TimersFragment fragment = new TimersFragment();
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

    ListView timerListView;
    ImageButton addTimerButton;

    //RecyclerView Adapter
    TimerAdapter mAdapter;

    //updater runnable
    timerUpdaterRunnable mRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_timers, container, false);

        timerListView = layout.findViewById(R.id.timer_list_view);
        addTimerButton = addItemButton;

        resetToolbar();
        //TODO implement timerListView

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Timers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<FocusTimer> temp = new Vector<FocusTimer>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    FocusTimer timer = new FocusTimer(id);
                    Log.e("Adding timer!", "Adding timers: " + timer.getId());
                    temp.add(timer);
                }
                mAdapter = new TimerAdapter(getContext(), temp);
                timerListView.setAdapter(mAdapter);
                final long delayLoop = 500;

                mRunnable = new timerUpdaterRunnable();
                mRunnable.adapter = mAdapter;
                mRunnable.list = timerListView;
                mRunnable.delay = (int)delayLoop;

                timerListView.postDelayed(mRunnable, delayLoop);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, CreateTimerFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRunnable.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Timers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<FocusTimer> temp = new Vector<FocusTimer>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    FocusTimer timer = new FocusTimer(id);
                    Log.e("Adding timer!", "Adding timers: " + timer.getId());
                    temp.add(timer);
                }
                mAdapter = new TimerAdapter(getContext(), temp);
                timerListView.setAdapter(mAdapter);
                final long delayLoop = 500;

                mRunnable = new timerUpdaterRunnable();
                mRunnable.adapter = mAdapter;
                mRunnable.list = timerListView;
                mRunnable.delay = (int)delayLoop;

                timerListView.postDelayed(mRunnable, delayLoop);

                mRunnable.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

class timerUpdaterRunnable implements Runnable {

    boolean running = true;
    public TimerAdapter adapter;
    public int delay;
    public ListView list;

    @Override
    public void run() {
        adapter.notifyDataSetChanged();
        if(running) {
            list.postDelayed(this, delay);

        }
    }

    public void stop(){
        running = false;
    }

    public void start(){
        running = true;
        run();
    }
}
