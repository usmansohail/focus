package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class ProfilesFragment extends BaseFragment {

    //Fragment globals
    RecyclerView profilesRecyclerView;
    ImageButton addProfileButton;

    //RecyclerView Adapter
    ProfileAdapter mAdapter;

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User1").child("Profiles");
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

        return layout;
    }

}