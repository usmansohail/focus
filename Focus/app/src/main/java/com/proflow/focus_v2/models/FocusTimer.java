package com.proflow.focus_v2.models;

import android.app.Notification;
import android.content.pm.PackageInfo;

import com.proflow.focus_v2.activities.ContextActivity;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.helpers.NotificationUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 *
 * I've got to think of how the damn timer is actually supposed to be used... I know we've got an
 * actual 'timer' in the app, but we also need to use some semblance of an actual java.util.timer
 * for actively running schedules... Hmmm...
 *
 * For now this is the model of an actual timer. IE the thing we're displaying. It's start and stop
 * methods effectively just tell the 'running thread' (However we implement that) that
 */


public class FocusTimer {

    private String mName;

    private Long mInitialDuration;
    private Long mCurrentDuration;
    private int id;

    private boolean paused = true;
    private boolean finished = false;

    Timer mTimer = new Timer(true);

    public int mPeriod = 1000;

    private Vector<Profile> mProfiles;
    private String mNotifMessage;

    public FocusTimer(String name, Long initialDuration, Vector<Profile> timerProfiles){
        mName = name;
        mInitialDuration = initialDuration;
        mCurrentDuration = initialDuration;
        mProfiles = timerProfiles;
        mNotifMessage = "A timer blocking "+ mProfiles.size()+ " profile(s) has ended";

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!paused){
                    if(mCurrentDuration <= 0){
                        mCurrentDuration = (long)0;
                        finished = true;
                        NotificationUtils mNotificationUtils = new NotificationUtils(ContextActivity.getAppContext());
                        Notification.Builder nb = mNotificationUtils.
                                getNotification("Timer Ended", mNotifMessage);
                        mNotificationUtils.notify(101, nb);
                    }
                    else{
                        mCurrentDuration -= mPeriod;
                    }
                }
            }
        }, 0, mPeriod);

        id = Global.getInstance().getUniqueTimerID();
    }

    public FocusTimer(String name, Long initialDuration, Vector<Profile> timerProfiles,long currentDuration, int id){
        mName = name;
        mInitialDuration = initialDuration;
        mCurrentDuration = currentDuration;
        mProfiles = timerProfiles;
        mNotifMessage = "A timer blocking "+ mProfiles.size()+ " profile(s) has ended";

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!paused && ! finished){
                    if(mCurrentDuration <= 0){
                        togglePause();
                        finished = true;
                        NotificationUtils mNotificationUtils = new NotificationUtils(ContextActivity.getAppContext());
                        Notification.Builder nb = mNotificationUtils.
                                getNotification("Timer Ended", mNotifMessage);
                        mNotificationUtils.notify(101, nb);
                    }
                    else{
                        mCurrentDuration -= mPeriod;
                    }
                }
            }
        }, 0, mPeriod);

        id = Global.getInstance().getUniqueTimerID();
    }

// May need later
//    public String formatNotifMessage(Vector<Profile> mProfiles){
//        String profileNames = "A timer blocking the ";
//        if(mProfiles.size() > 1){
//            profileNames += " profiles ";
//            for (int i=0; i<mProfiles.size()-1; i++){
//                profileNames += mProfiles.get(i).getName()+", ";
//            }
//            profileNames += "and "+mProfiles.get(mProfiles.size()-1);
//        }else {
//            profileNames += " profile "+mProfiles.get(0);
//        }
//        profileNames += " has just ended.";
//        return profileNames;
//    }

    public String getRemainingTimeString(){
        long hours = (mCurrentDuration/1000) / 3600;
        long minutes = ((mCurrentDuration / 1000)  / 60) % 60;
        long seconds = (mCurrentDuration / 1000) % 60;

        String retString = "";
        if(hours > 0){
            retString += "" + hours + ":";
        }
        if(minutes < 10){
            retString += "0" + minutes + ":";
        } else {
            retString += minutes + ":";
        }
        if(seconds < 10){
            retString += "0" + seconds;
        } else {
            retString += "" + seconds;
        }

        return retString;
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause(){
        paused = !paused;
    }

    public Vector<Profile> getProfiles() {
        return mProfiles;
    }

    public Vector<String> getApps(){
        Vector<String> appBucket = new Vector<>();

        for(Profile p : mProfiles){
            for(PackageInfo pi : p.getApps()){
                if(!appBucket.contains(pi.packageName)){
                    appBucket.add(pi.packageName);
                }
            }
        }
        return appBucket;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return mName;
    }

    public long getCurrentDuration() {
        return mCurrentDuration;
    }

    public long getInitialDuration() {
        return mInitialDuration;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished){
        this.finished = finished;
    }

        // Step 1 - This interface defines the type of messages I want to communicate to my owner
        public interface MyCustomObjectListener {
            // These methods are the different events and
            // need to pass relevant arguments related to the event triggered
            public void onObjectReady(String title);
            // or when data has been loaded
            public void onDataLoaded(int data);
        }

}