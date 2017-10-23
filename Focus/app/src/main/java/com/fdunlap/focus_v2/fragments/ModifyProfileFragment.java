package com.fdunlap.focus_v2.fragments;


import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.adapters.AppAdapter;
import com.fdunlap.focus_v2.data.Global;
import com.fdunlap.focus_v2.models.Profile;

import java.util.Vector;

public class ModifyProfileFragment extends BaseFragment {

    private AppAdapter mAdapter;
    private RecyclerView mAppRecyclerView;
    private EditText mProfileNameEditText;

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
        View layout = inflater.inflate(R.layout.fragment_create_profile, container, false);

        mAppRecyclerView = layout.findViewById(R.id.create_profile_recycler_view);
        mProfileNameEditText = layout.findViewById(R.id.profile_name_edit_text);

        Bundle args = getArguments();
        int profileIndex = 0;
        if(args != null){
            profileIndex = args.getInt("ProfileIndex");
        }

        Vector<PackageInfo> apps = Global.getInstance().getAllApps(getContext());
        for(PackageInfo pi: apps){
            Log.d("CreateProfileFragment", "Name: " + pi.applicationInfo.name);
        }

        Profile currentProfile = Global.getInstance().getAllProfiles(getContext()).get(profileIndex);

        mAdapter = new AppAdapter(currentProfile, apps, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mAppRecyclerView.setLayoutManager(mLayoutManager);
        mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAppRecyclerView.setAdapter(mAdapter);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //So right now this just gives a toast
                //TODO implement confirmation & validation of input.
                String profileName = mProfileNameEditText.getText().toString();
                Vector<PackageInfo> selectedPackages = getSelectedPackages();

                Profile newProfile = new Profile(profileName, selectedPackages);
                Global.getInstance().addProfile(getContext(), newProfile);
                getActivity().onBackPressed();
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

    private Vector<PackageInfo> getSelectedPackages(){
        Vector<PackageInfo> packages = new Vector<>();

        for(int i = 0; i < mAppRecyclerView.getChildCount(); i++){
            AppAdapter.AppViewHolder AVH = (AppAdapter.AppViewHolder) mAppRecyclerView.findViewHolderForAdapterPosition(i);
            if(AVH.appCheckbox.isChecked()){
                packages.add(AVH.thisPackage);
            }
        }

        return packages;
    }

}
