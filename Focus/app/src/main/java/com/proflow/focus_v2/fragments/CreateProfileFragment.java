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
import android.widget.Toast;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.adapters.AppAdapter;
import com.proflow.focus_v2.comparators.PackageInfoComparator;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;

import java.util.Collections;
import java.util.Vector;

public class CreateProfileFragment extends BaseFragment {

    private final String TAG = "CreateProfileFragment";

    private AppAdapter mAdapter;
    private RecyclerView mAppRecyclerView;
    private EditText mProfileNameEditText;
    private Button mDeleteButton;

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
                //TODO implement confirmation & validation of input.
                if (validate()){
                    String profileName = mProfileNameEditText.getText().toString();
                    Vector<PackageInfo> selectedPackages = getSelectedPackages();
    
                    Profile newProfile = new Profile(profileName, selectedPackages);
                    Global.getInstance().addProfile(getContext().getApplicationContext(), newProfile);
                    getActivity().onBackPressed();
                }
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

    private Vector<PackageInfo> getSelectedPackages(){
        return mAdapter.getSelectedApps();
    }

}
