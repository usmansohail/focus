package com.fdunlap.focus_v2.models;

import android.content.pm.PackageInfo;

import java.util.Vector;

/**
 * Created by Forrest on 10/19/2017.
 *
 * This is the profile model - anything that needs to be displayed here should be done through the
 * ProfileAdapter.
 */

public class Profile {

    private String mName;
    private Vector<PackageInfo> mPackages;
    private boolean mIsActive;

    public Profile(String name, Vector<PackageInfo> packageIds){
        mName = name;
        mPackages = packageIds;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public Vector<PackageInfo> getApps(){
        return mPackages;
    }

    public void setApps(Vector<PackageInfo> apps){
        mPackages = apps;
    }

    public void activate(){
        mIsActive = true;
    }

    public void deactivate(){
        mIsActive = false;
    }

    public boolean isActive(){
        return mIsActive;
    }


}