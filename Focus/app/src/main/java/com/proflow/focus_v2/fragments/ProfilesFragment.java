package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class ProfilesFragment extends BaseFragment {

    //Fragment globals
    RecyclerView profilesRecyclerView;
    ImageButton addProfileButton;
    ImageButton expandMenuButton;

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
        }
    }

    private void runProfileTutorial() {

    }
}