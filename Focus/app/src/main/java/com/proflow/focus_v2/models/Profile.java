package com.proflow.focus_v2.models;

import android.content.pm.PackageInfo;
import android.os.Parcelable;

import com.proflow.focus_v2.data.Global;

import java.io.Serializable;
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
    private int id;

    public Profile(String name, Vector<PackageInfo> packageIds){
        mName = name;
        mPackages = packageIds;
        setUniqueId();
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
        /*
        TODO Add profile to indefinite list? Not sure this is strictly necessary. We could have a setting
        such that a profile is indefinitely active if we call activate? Yeah. I'm gonna go off of that
        assumption.
         */
        mIsActive = true;
    }

    public void deactivate(){
        //TODO As activate() remove profile from service indefinite active list. Safely.
        //(Possible issues w/ respect to active schedules that contain this profile. Iterative check)
        mIsActive = false;
    }

    public boolean isActive(){
        return mIsActive;
    }

    public int getId(){
        return id;
    }

    private void setUniqueId(){
        id = Global.getInstance().getProfileUniqueID();
    }

}