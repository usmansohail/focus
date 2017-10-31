package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;

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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, CreateProfileFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mAdapter = new ProfileAdapter(Global.getInstance().getAllProfiles(getContext()), getContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        profilesRecyclerView.setLayoutManager(mLayoutManager);
        profilesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        profilesRecyclerView.setAdapter(mAdapter);

        return layout;
    }

}