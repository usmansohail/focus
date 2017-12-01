package com.proflow.focus_v2.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.activities.LoginActivity;
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
    ImageButton expandMenuButton;

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
        expandMenuButton = largerMenuButton;

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
                mAdapter = new TimerAdapter(getContext(), Global.getInstance().getTimers(getContext()));
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

        //TODO
        expandMenuButton.setVisibility(View.VISIBLE);



        expandMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO figure out what would even need to go into a menu, and then add that here
                //Recommend looking for a dropdown library?
                PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.container_menu));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.options_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Toast toast = Toast.makeText(getContext(), "clicked button", Toast.LENGTH_SHORT);
                        toast.show();
                        switch (menuItem.getItemId())
                        {
                            case R.id.logout:
                                Intent logout = new Intent(getContext(), LoginActivity.class);
                                startActivity(logout);



                                return true;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });



        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();

        // TODO: Check if this is ok Forrest
        if(mRunnable != null) {
            mRunnable.stop();
        }
        else
        {
            Log.d("NULL:", "mRunnable is null on line 161");
        }
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
