package com.proflow.focus_v2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.proflow.focus_v2.data.Global;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 */

public class Schedule implements Serializable {

    private String mName;
    private Vector<TimeBlock> mTimeBlocks;

    private Vector<Profile> mProfiles = new Vector<>();

    private boolean mRepeatWeekly;
    private boolean isActive = false;

    private int id;

    public Schedule(String name, Vector<TimeBlock> timeBlocks, boolean repeatWeekly){
        mName = name;
        mTimeBlocks = timeBlocks;
        mRepeatWeekly = repeatWeekly;
        setUniqueId();
    }



    /*
    MODIFIERS
     */

    //Checks that the TimeBlock you're trying to add doesn't overlap any existing TimeBlocks,
    //and if not, attempts to add it.
    public boolean addTimeBlock(TimeBlock block){
        for(TimeBlock tb : mTimeBlocks){
            if(tb.overlaps(block)){
                return false;
            }
        }
        return mTimeBlocks.add(block);
    }

    //SO this isn't ideal because timeblocks don't contain unique identifiers. Thus if a timeblock
    //gets created and isn't the same block, removal isn't a simple vector.remove();

    //This fixes that by first checking if the vector contains the exact object, and if not, it
    //iterates through the blocks and compares start and end times.
    public boolean removeTimeBlock(TimeBlock block){
        if(mTimeBlocks.contains(block)){
            return mTimeBlocks.remove(block);
        } else {
            for (TimeBlock tb : mTimeBlocks) {
                //The start and end times are the same...
                if(tb.equals(block)){
                    return mTimeBlocks.remove(tb);
                }
            }
            return false;
        }
    }

    //I'm going to assume that you're not adding new profiles willy-nilly.
    //CANNOT USE 'NEW' PROFILES. You've got to have a reference to profiles later if you want to
    //be able to remove them.
    public boolean addProfile(Profile p) {
        if (!mProfiles.contains(p)) {
            return mProfiles.add(p);
        }
        return false;
    }

    //See note above
    public boolean removeProfile(Profile p){
        return mProfiles.remove(p);
    }

    //Start and stop are where we tell the updater service that we need to worry about THIS profile
    //Basically, we need to have some background service telling the profiles when to run and when
    //not to run, and also which apps are being blocked, etc.
    public boolean start(){
        //TODO Add a start_profile(Profile p) to the "running thread" or utilize global activeProfiles?
        //Issue is with how we store/get data. If the phone is randomly pulling data over the network
        //we're going to see random hickups in performance whenever the user does something that
        //calls our onEvent listener (due to network checks.) So we need a file, or similar, to check
        //Yeah, file I/O is slow, but it's lightning relative to a network connection.
        return false;
    }

    public boolean stop(){
        //TODO Add a stop_profile(Profile p) to the "running thread" -- see above.
        return false;
    }

    /*
    SETTERS
     */
    public void setName(String name) {
        this.mName = name;
    }

    public void setProfiles(Vector<Profile> profiles){
        this.mProfiles = profiles;
    }

    public void setTimeBlocks(Vector<TimeBlock> timeBlocks) {
        this.mTimeBlocks = timeBlocks;
    }

    public void setRepeatWeekly(boolean weeklyRepeat) {
        this.mRepeatWeekly = weeklyRepeat;
    }

    private void setUniqueId() {
        id = Global.getInstance().getUniqueScheduleID();
    }

    /*
     GETTERS
     */
    public String getName() {
        return mName;
    }

    public Vector<Profile> getProfiles(){
        return mProfiles;
    }

    public Vector<TimeBlock> getTimeBlocks() {
        return mTimeBlocks;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean repeatWeekly() {
        return mRepeatWeekly;
    }

    public Integer getId() {
        return id;
    }
}