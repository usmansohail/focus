package com.proflow.focus_v2.models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.data.Global;

import java.util.Vector;

/**
 * Created by Forrest on 10/19/2017.
 *
 * This is the profile model - anything that needs to be displayed here should be done through the
 * ProfileAdapter.
 */

public class Profile {

    private String mName;
    public Vector<String> mPackageNames = new Vector<>();
    private Vector<PackageInfo> mPackages;
    private boolean mIsActive;
    private int id;

    public Profile(){
        //for DataSnapshot.getValue
    }

    public Profile(DataSnapshot snapshot){
        mName = snapshot.child("name").getValue(String.class);
        mIsActive = snapshot.child("active").getValue(Boolean.class);
        id = snapshot.child("id").getValue(Integer.class);
        for(DataSnapshot packages : snapshot.child("mPackageNames").getChildren()){
            mPackageNames.add(packages.getValue(String.class));
        }
    }

    public Profile(String name, Vector<String> packageIds){
        mName = name;
        mPackageNames = packageIds;
        id = Global.getInstance().getProfileUniqueID();
    }

    public Profile(String name, Vector<String> packageIds, int _id){
        mName = name;
        mPackageNames = packageIds;
        id = _id;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public Vector<String> getApps(){ return mPackageNames; }

    public void setApps(Vector<String> apps){
        mPackageNames = apps;
    }

    public void activate(Context context){
        /*
        TODO Add profile to indefinite list? Not sure this is strictly necessary. We could have a setting
        such that a profile is indefinitely active if we call activate? Yeah. I'm gonna go off of that
        assumption.
         */
        mIsActive = true;
        update(context);
    }

    public void deactivate(Context context){
        //TODO As activate() remove profile from service indefinite active list. Safely.
        //(Possible issues w/ respect to active schedules that contain this profile. Iterative check)
        mIsActive = false;
        update(context);
    }

    public void setActive(boolean active){
        mIsActive = active;
    }

    public boolean isActive(){
        return mIsActive;
    }

    public int getId(){
        return id;
    }

    private void setUniqueId(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int uniqueId = -1;
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    uniqueId = id.child("id").getValue(Integer.class);
                }
                uniqueId++;
                id = uniqueId;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //id = Global.getInstance().getProfileUniqueID();
    }

    private void update(Context context){
        Global.getInstance().modifyProfile(context, this);
    }
}