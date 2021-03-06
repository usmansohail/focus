package com.proflow.focus_v2.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.proflow.focus_v2.data.Global;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 */

public class Schedule implements Serializable  {

    private static final String TAG = "Schedule";

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

    public Schedule(String name, Vector<TimeBlock> timeBlocks, Vector<Profile> profiles ,boolean repeatWeekly, int id){
        mName = name;
        mTimeBlocks = timeBlocks;
        mRepeatWeekly = repeatWeekly;
        mProfiles = profiles;
        this.id = id;
    }

    public Schedule(DataSnapshot snapshot){
        mName = snapshot.child("name").getValue(String.class);
        id = snapshot.child("id").getValue(Integer.class);
        ArrayList<TimeBlock> result1 = (ArrayList<TimeBlock>) snapshot.child("timeBlocks").getValue();
        Vector<TimeBlock> timeBlocks = new Vector<>();
        for(int i = 0; i < result1.size(); i++){
            timeBlocks.add(result1.get(i));
        }
        mTimeBlocks = timeBlocks;
        ArrayList<Profile> result2 = (ArrayList<Profile>) snapshot.child("profiles").getValue();
        Vector<Profile> profiles = new Vector<>();
        for(int i = 0; i < result2.size(); i++){
            profiles.add(result2.get(i));
        }
        mProfiles = profiles;
    }

    /*
    MODIFIERS
     */

    //Checks that the TimeBlock you're trying to add doesn't overlap any existing TimeBlocks,
    //and if not, attempts to add it.
    public boolean addTimeBlock(TimeBlock block){
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
    public boolean start(Context context){
        //TODO Add a start_profile(Profile p) to the "running thread" or utilize global activeProfiles?
        //Issue is with how we store/get data. If the phone is randomly pulling data over the network
        //we're going to see random hickups in performance whenever the user does something that
        //calls our onEvent listener (due to network checks.) So we need a file, or similar, to check
        //Yeah, file I/O is slow, but it's lightning relative to a network connection.
        isActive = true;

        update(context);
        return false;
    }

    public boolean stop(Context context){
        //TODO Add a stop_profile(Profile p) to the "running thread" -- see above.
        isActive = false;
        update(context);
        return false;
    }

    private void update(Context context) {
        Global.getInstance().modifySchedule(context, this);
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

    public void setActive(boolean active){
        isActive = active;
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

    public Integer getId(){return id;}

    public boolean isBlocking() {
        if(isActive) {
            GregorianCalendar gc = new GregorianCalendar();
            Log.d(TAG, getName() + ": IsActive");
            for (TimeBlock tb : mTimeBlocks) {

                Vector<Integer> days = new Vector<>();
                for(TimeBlock.day d : tb.getDays()){
                    int dayInt =TimeBlock.day.toInteger(d) + 1;
                    days.add(dayInt);
                    Log.d(TAG, "Added " + dayInt);
                }
                int gcDay = gc.get(Calendar.DAY_OF_WEEK);

                Log.d(TAG, "Looking for gcDay: " + gcDay);

                if(days.contains(gcDay)) {
                    int currentHour = gc.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = gc.get(Calendar.MINUTE);

                    Log.d(TAG, "CurrentHour: " + currentHour + " CurrentMinute: " + currentMinute);
                    Log.d(TAG, "startTime: " + tb.getStartTime().toString());
                    Log.d(TAG, "endTime: " + tb.getEndTime());

                    time ct = new time(currentHour, currentMinute);
                    if (ct.isBetween(tb.getStartTime(), tb.getEndTime())) {
                        Log.d(TAG, "Block Notification!");
                        return true;
                    }
                }
            }
        }
        //Log.d(TAG, "DONT Block Notification!");
        return false;
    }
}