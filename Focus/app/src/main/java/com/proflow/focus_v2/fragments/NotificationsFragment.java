package com.proflow.focus_v2.fragments;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.FocusFinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.proflow.focus_v2.activities.LoginActivity;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.NotificationAdapter;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

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
        setHasOptionsMenu(true);

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_notifications, container, false);

        setHasOptionsMenu(true);



        resetToolbar();
        //Global view assignment
        notificationRecyclerView = layout.findViewById(R.id.notification_recycler_view);
        expandMenuButton = largerMenuButton;
        expandMenuButton.setVisibility(View.VISIBLE);
        clearButton = clearNotificationsButton;

        //TODO Implement NotificationListView

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
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

        final View menuContainer  = layout.findViewById(R.id.container_menu);
        final Context c = getContext();

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
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(!prefs.contains(getString(R.string.firstNotifications))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstNotifications), false).apply();
        }
    }

    Animation mEnterAnimation, mExitAnimation;

    private void runProfileTutorial() {
        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        runOverlay_ContinueMethod();
    }

    private void runOverlay_ContinueMethod(){
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("Notifications recieved from blocked apps will appear here.")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(notificationRecyclerView);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("Tap here to clear all blocked notifications.")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(clearButton);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();


        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }


}
