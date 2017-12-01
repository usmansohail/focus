package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.AppAdapter;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.comparators.PackageInfoComparator;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class CreateProfileFragment extends BaseFragment {

    private final String TAG = "CreateProfileFragment";

    private AppAdapter mAdapter;
    private RecyclerView mAppRecyclerView;
    private EditText mProfileNameEditText;
    private TextView mCreateProfileText;
    private Button mDeleteButton;


    private Animation mEnterAnimation, mExitAnimation;

    public CreateProfileFragment() {
        // Required empty public constructor
    }

    public static CreateProfileFragment newInstance() {
        CreateProfileFragment fragment = new CreateProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showBackButton(true);
        showConfirmButton(true);
        setShowBottomBar(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mAppRecyclerView = layout.findViewById(R.id.create_profile_recycler_view);
        mProfileNameEditText = layout.findViewById(R.id.profile_name_edit_text);
        mDeleteButton = layout.findViewById(R.id.create_profile_delete_button);
        mDeleteButton.setVisibility(View.GONE);
        mCreateProfileText = layout.findViewById(R.id.create_profile_text_view);

        Vector<PackageInfo> apps = Global.getInstance().getAllApps(getContext());

        for(PackageInfo pi: apps){
            Log.d("CreateProfileFragment", "Name: " + pi.applicationInfo.name);
            if(pi.applicationInfo.name == null){
                Log.d("CreateProfileFragment", "PackageName: " + pi.applicationInfo.packageName);
            }
        }


        Collections.sort(apps, new PackageInfoComparator(getContext()));
        mAdapter = new AppAdapter(apps, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAppRecyclerView.setLayoutManager(mLayoutManager);
        mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAppRecyclerView.setAdapter(mAdapter);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //So right now this just gives a toast

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean atLeastOneAppSelected = mAdapter.getSelectedApps().size() > 0;
                        boolean someName = !mProfileNameEditText.getText().toString().isEmpty();
                        boolean uniqueName = true;

                        List<Profile> temp = new ArrayList<>();
                        for(DataSnapshot id : dataSnapshot.getChildren()) {
                            Profile profile = new Profile(id);
                            temp.add(profile);
                        }

                        List<Profile> allProfiles = temp;
                        for(Profile p : allProfiles){
                            if(mProfileNameEditText.getText().toString().compareToIgnoreCase(p.getName()) == 0){
                                uniqueName = false;
                            }
                        }

                        if(!atLeastOneAppSelected){
                            Toast.makeText(getContext(), getString(R.string.noAppsSelected), Toast.LENGTH_SHORT).show();
                        } else if(!uniqueName){
                            Toast.makeText(getContext(), R.string.notUniqueName, Toast.LENGTH_SHORT).show();
                        } else if(!someName){
                            Toast.makeText(getContext(), R.string.pleaseEnterName, Toast.LENGTH_SHORT).show();
                        }
                        if(atLeastOneAppSelected && uniqueName && someName){
                            String profileName = mProfileNameEditText.getText().toString();
                            Vector<String> selectedPackages = getSelectedPackages();

                            Profile newProfile = new Profile(profileName, selectedPackages);
                            Global.getInstance().addProfile(getContext().getApplicationContext(), newProfile);
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child(Global.getInstance().getUsername()).child("Profiles").child(String.valueOf(newProfile.getId())).setValue(newProfile);
                            getActivity().onBackPressed();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*if (validate()){
                    String profileName = mProfileNameEditText.getText().toString();
                    Vector<String> selectedPackages = getSelectedPackages();
    
                    Profile newProfile = new Profile(profileName, selectedPackages);
                    //Global.getInstance().addProfile(getContext().getApplicationContext(), newProfile);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(Global.getInstance().getUsername()).child("Profiles").child(String.valueOf(newProfile.getId())).setValue(newProfile);
                    getActivity().onBackPressed();
                }*/
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return layout;
    }

    private boolean validate() {
        boolean atLeastOneAppSelected = mAdapter.getSelectedApps().size() > 0;
        boolean someName = !mProfileNameEditText.getText().toString().isEmpty();
        boolean uniqueName = true;

        Vector<Profile> allProfiles = Global.getInstance().getAllProfiles(getContext());
        for(Profile p : allProfiles){
            if(mProfileNameEditText.getText().toString().compareToIgnoreCase(p.getName()) == 0){
                uniqueName = false;
            }
        }

        if(!atLeastOneAppSelected){
            Toast.makeText(getContext(), getString(R.string.noAppsSelected), Toast.LENGTH_SHORT).show();
        } else if(!uniqueName){
            Toast.makeText(getContext(), R.string.notUniqueName, Toast.LENGTH_SHORT).show();
        } else if(!someName){
            Toast.makeText(getContext(), R.string.pleaseEnterName, Toast.LENGTH_SHORT).show();
        }

        return atLeastOneAppSelected && uniqueName && someName;
    }

    private Vector<String> getSelectedPackages(){
        return mAdapter.getSelectedApps();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(!prefs.contains(getString(R.string.firstCreateProfilesKey))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstCreateProfilesKey), false).apply();
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
                        .setTitle("Profiles")
                        .setDescription("Profiles are simply a set of apps to block, and an associated name." +
                                " You use them to tell Schedules which set(s) of apps to block.")
                        .setGravity(Gravity.TOP)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mCreateProfileText);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("You can select any number of apps by checking the boxes to" +
                                "the right of their names.")
                        .setGravity(Gravity.TOP)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mAppRecyclerView);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("When you're finished click here.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(confirmButton);

        ChainTourGuide tourGuide4 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("Or if you don't want to create a new profile, click here.")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(navBackButton);

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
