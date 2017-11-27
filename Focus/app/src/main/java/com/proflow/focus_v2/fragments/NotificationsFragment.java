package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.FocusFinder;
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
import com.proflow.focus_v2.adapters.NotificationAdapter;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    ListView notificationRecyclerView;
    ImageButton clearButton;
    ImageButton expandMenuButton;

    //RecyclerView Adapter
    NotificationAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_notifications, container, false);

        resetToolbar();
        //Global view assignment
        notificationRecyclerView = layout.findViewById(R.id.notification_recycler_view);
        expandMenuButton = largerMenuButton;
        expandMenuButton.setVisibility(View.VISIBLE);
        clearButton = clearNotificationsButton;

        //TODO Implement NotificationListView

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User1").child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<FocusNotification> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    FocusNotification notification = new FocusNotification(id);
                    temp.add(notification);
                }
                Log.e("Reading notification data", "Size: " + temp.size());
                mAdapter = new NotificationAdapter(getContext(), temp);
                notificationRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //Actionbar Listeners
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.clearAll();
            }
        });

        //TODO
        expandMenuButton.setVisibility(View.GONE);
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
