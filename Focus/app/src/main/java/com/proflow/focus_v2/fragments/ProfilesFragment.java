package com.proflow.focus_v2.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.LoginActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class ProfilesFragment extends BaseFragment {

    //Fragment globals
    RecyclerView profilesRecyclerView;
    ImageButton addProfileButton;
    ImageButton expandMenuButton;

    //RecyclerView Adapter
    ProfileAdapter mAdapter;

    private Animation mEnterAnimation, mExitAnimation;


    public ProfilesFragment() {
        // Required empty public constructor
    }

    public static ProfilesFragment newInstance() {
        ProfilesFragment fragment = new ProfilesFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showAddButton(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_profiles, container, false);
        profilesRecyclerView = layout.findViewById(R.id.profile_recycler_view);
        expandMenuButton = largerMenuButton;

        resetToolbar();
        //Gotta love the builtins for the BaseFragment
        addProfileButton = addItemButton;
        addProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.getInstance().getAllProfiles(getContext()).size() < 20) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.Main_Frame, CreateProfileFragment.newInstance());
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast.makeText(getContext(), getString(R.string.tooManyProfiles), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> temp = new ArrayList<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    String profileID = id.getKey();
                    Log.e("Database", profileID);
                    String name = id.child("name").getValue(String.class);
                    boolean active = id.child("active").getValue(boolean.class);
                    /*Vector<PackageInfo> packages = new Vector<PackageInfo>();
                    for(DataSnapshot apps : id.child("apps").getChildren()){
                        PackageInfo packageInfo = apps.getValue(PackageInfo.class);
                        packages.add(packageInfo);
                    }*/
                    Profile profile = new Profile(id);
                    temp.add(profile);
                    Log.e("Profile name", profile.getName());
                    Log.e("Profile id", String.valueOf(profile.getId()));
                }
                mAdapter = new ProfileAdapter(temp, getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                profilesRecyclerView.setLayoutManager(mLayoutManager);
                profilesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                profilesRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(!prefs.contains(getString(R.string.firstProfilesKey))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstProfilesKey), false).apply();
        }
    }

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
                        .setTitle("BottomBar")
                        .setDescription("The bottom bar is the primary navigation tool in the app." +
                                " Use it to switch functionality.")
                        .setGravity(Gravity.TOP)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(bottomBar);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Add a Profile")
                        .setDescription("This button takes you to a screen where you can create Profiles." +
                                " Profiles are simply a set of apps to block, and an associated name.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(addItemButton);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Menu")
                        .setDescription("This is the menu button - it will have different context" +
                                " tools based on your location in the app.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(largerMenuButton);

        ChainTourGuide tourGuide4 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Profile Area")
                        .setDescription("This is where profiles will show up once they have been created. ")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(profilesRecyclerView);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3, tourGuide4)
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