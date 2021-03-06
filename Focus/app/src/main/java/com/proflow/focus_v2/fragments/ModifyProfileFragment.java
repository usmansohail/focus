package com.proflow.focus_v2.fragments;


import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.concurrent.TimeUnit;

public class ModifyProfileFragment extends BaseFragment {

    private static final String TAG = "ModifyProfileFragment";

    private AppAdapter mAdapter;
    private RecyclerView mAppRecyclerView;
    private EditText mProfileNameEditText;
    private Button mDeleteProfileButton;

    public ModifyProfileFragment() {
        // Required empty public constructor
    }

    public static ModifyProfileFragment newInstance() {
        return new ModifyProfileFragment();
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
        final View layout = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mAppRecyclerView = layout.findViewById(R.id.create_profile_recycler_view);
        mProfileNameEditText = layout.findViewById(R.id.profile_name_edit_text);

        /*Bundle args = getArguments();
        int profileIndex = 0;
        if(args != null){
            profileIndex = args.getInt("ProfileIndex");
        }*/

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bundle args = getArguments();
                int profileIndex = 0;
                if(args != null){
                    profileIndex = args.getInt("ProfileIndex");
                }
                final Profile currentProfile = new Profile(dataSnapshot.child(Integer.toString(profileIndex)));
                Vector<PackageInfo> apps = Global.getInstance().getAllApps(getContext());
                Collections.sort(apps, new PackageInfoComparator(getContext()));
                mAdapter = new AppAdapter(currentProfile, apps, getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                mAppRecyclerView.setLayoutManager(mLayoutManager);
                mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mAppRecyclerView.setAdapter(mAdapter);
                mProfileNameEditText.setText(currentProfile.getName());

                mDeleteProfileButton = layout.findViewById(R.id.create_profile_delete_button);
                mDeleteProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Global.getInstance().removeProfile(getContext(), currentProfile);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(Global.getInstance().getUsername()).child("Profiles").child(String.valueOf(currentProfile.getId())).removeValue();

                        getActivity().onBackPressed();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //So right now this just gives a toast
                        //TODO implement confirmation & validation of input.
                        currentProfile.setName(mProfileNameEditText.getText().toString());
                        currentProfile.setApps(getSelectedPackages());
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(Global.getInstance().getUsername()).child("Profiles").child(String.valueOf(currentProfile.getId())).setValue(currentProfile);
                        boolean found = Global.getInstance().modifyProfile(getContext(), currentProfile);
                        if(!found){
                            Log.d(TAG, "MPF: profile not found");
                        }
                        getActivity().onBackPressed();
                    }
                });

                navBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //final Profile currentProfile = Global.getInstance().getAllProfiles(getContext()).get(profileIndex);

        return layout;
    }

    private Vector<String> getSelectedPackages(){
        return mAdapter.getSelectedApps();
    }

}
