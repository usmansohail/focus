package com.fdunlap.focus_v2.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.adapters.ProfileAdapter;

public class ProfilesFragment extends BaseFragment {

    //Fragment globals
    RecyclerView profilesRecyclerView;
    ImageButton addProfileButton;

    //RecyclerView Adapter
    ProfileAdapter mAdapter;

    //Activity Toolbar
    Toolbar toolbar;



    public ProfilesFragment() {
        // Required empty public constructor
    }

    public static ProfilesFragment newInstance() {
        ProfilesFragment fragment = new ProfilesFragment();
        return fragment;
    }

    public interface OnSwitchToModifyFragment {
        public void onHideBottomBar();
        public void onShowBottomBar();
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

        //TODO Implement profilesListView based off of some global profiles class
        mAdapter = new ProfileAdapter();
        profilesRecyclerView.setAdapter(mAdapter);

        return layout;
    }

}
