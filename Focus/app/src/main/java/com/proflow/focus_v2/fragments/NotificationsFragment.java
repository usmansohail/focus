package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.NotificationAdapter;

public class NotificationsFragment extends BaseFragment {
    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showClearButton(true);
        showMenuButton(true);
    }

    //Global views for fragment
    RecyclerView notificationRecyclerView;
    ImageButton clearButton;
    ImageButton expandMenuButton;

    //RecyclerView Adapter
    NotificationAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_notifications, container, false);

        //Global view assignment
        notificationRecyclerView = layout.findViewById(R.id.notification_recycler_view);
        expandMenuButton = largerMenuButton;
        clearButton = clearNotificationsButton;

        //TODO Implement NotificationListView
        mAdapter = new NotificationAdapter();
        notificationRecyclerView.setAdapter(mAdapter);

        //Actionbar Listeners
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Clear notifications (Likely just a clear of the global object, however that's implemented)
            }
        });

        expandMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO figure out what would even need to go into a menu, and then add that here
                //Recommend looking for a dropdown library?
            }
        });

        return layout;
    }

}
