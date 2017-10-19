package com.fdunlap.focus_v2.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.adapters.TimerAdapter;

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

    RecyclerView timerRecyclerView;
    ImageButton addTimerButton;

    //RecyclerView Adapter
    TimerAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_timers, container, false);

        timerRecyclerView = layout.findViewById(R.id.timer_recycler_view);
        addTimerButton = addItemButton;

        //TODO implement timerListView
        mAdapter = new TimerAdapter();
        timerRecyclerView.setAdapter(mAdapter);

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

}
