package com.proflow.focus_v2.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.FocusTimer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proflow.focus_v2.models.TimeBlock;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

/**
 * Modified to be a SharedPreference wrapper on 10/18/17 by Forrest.
 */

public class Global {
    private static final Global instance = new Global();

    public static Global getInstance() { return instance; }

    static Vector<PackageInfo> packageList = new Vector<>();
    static Vector<Profile> profileList = new Vector<>();
    static Vector<Profile> activeProfiles = new Vector<>();
    static Vector<Schedule> scheduleList = new Vector<>();
    static Vector<FocusTimer> timerList = new Vector<>();
    static Vector<PackageInfo> activeApps = new Vector<>();

    private Global() {
    }

    public Vector<Profile> getAllProfiles(Context context) {

        return profileList;
    }

    public Vector<Profile> getActiveProfiles(Context context) {

        return activeProfiles;
    }

    public Vector<Schedule> getSchedules(Context context) {

        return scheduleList;
    }

    public Vector<FocusTimer> getTimers(Context context) {
        return timerList;
    }

    public Vector<PackageInfo> getAllApps(Context context) {

        if(MainActivity.packageList1.isEmpty()){
            Log.d("getAllApps", "Package List empty?");
        }

        return packageList;
    }

    public void setAllApps(Context context, Vector<PackageInfo> apps){

        packageList = apps;
    }

    public Vector<Profile> getActiveProfilesForApp(Context context, PackageInfo appID){
        Vector<Profile> activeProfiles = getActiveProfiles(context);

        for (int i = 0; i < activeProfiles.size(); i++){
            for (int j = 0; j < activeProfiles.get(i).getApps().size(); j++){
                if (activeProfiles.get(i).getApps().get(j) == appID){
                    activeProfiles.add(activeProfiles.get(i));
                }
            }
        }

        return activeProfiles;
    }

    //new profiles isActive set to false
    public Boolean createProfile(Context context, String name, Vector<PackageInfo> apps){
        Vector<Profile> allProfiles = getAllProfiles(context);

        Profile tprofile = new Profile(name, apps);
        if(allProfiles.add(tprofile)){
            setAllProfiles(context, allProfiles);
            return true;
        }
        return false;
    }

    public void addProfile(Context context, Profile profile){

        Vector<Profile> allProfiles = getAllProfiles(context);

        allProfiles.add(profile);

        setAllProfiles(context, allProfiles);
    }

    public void addSchedule(Context context, Schedule schedule){

        Vector<Schedule> allSchedules = getSchedules(context);

        allSchedules.add(schedule);

        setSchedules(context, allSchedules);
    }

    public Boolean modifyProfile(Context context, Profile p){

        Vector<Profile> allProfiles = getAllProfiles(context);
        boolean found = false;

        for (int i= 0; i < allProfiles.size(); i++){
            if (allProfiles.get(i).getId() == p.getId()){

                //Note - you HAVE to pass in a COPY of the profile you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.
                allProfiles.get(i).setApps(p.getApps());
                allProfiles.get(i).setName(p.getName());
                found = true;
                break;
            }
        }

        return found;
    }

    public Boolean modifyProfile(Context context, Profile p, int id){

        Vector<Profile> allProfiles = getAllProfiles(context);
        boolean found = false;

        for (int i= 0; i < allProfiles.size(); i++){
            if (allProfiles.get(i).getId() == id){

                //Note - in this version you can pass in an arbitrary profile assuming you know the
                //id of the profile you're looking to change.
                //FURTHER: Note that the profile's id doesn't change.
                allProfiles.get(i).setApps(p.getApps());
                allProfiles.get(i).setName(p.getName());
                found = true;
                break;
            }
        }

        return found;
    }


    public Vector<PackageInfo> getActiveApps(Context context) {
        return activeApps;
    }

    public void setActiveApps(Context context, Vector<PackageInfo> activeApps) {
        this.activeApps = activeApps;
    }

    public boolean removeProfile(Context context, Profile profile){
        Vector<Profile> allProfiles = getAllProfiles(context);
        if(allProfiles.remove(profile)){
            setAllProfiles(context, allProfiles);
            return true;
        } else {
            return false;
        }
    }

    public Boolean addActiveProfile(Context context, Profile profile){
        Vector<Profile> activeProfiles = getActiveProfiles(context);
        Vector<Profile> allProfiles = getAllProfiles(context);

        for(Profile p: activeProfiles) {
            if (profile == p) {
                //Profile is already active
                return false;
            }
        }
        boolean isInAll = false;
        for(Profile p: allProfiles){
            if(p == profile){
                isInAll = true;
                break;
            }
        }
        if(isInAll){
            activeProfiles.add(profile);
            setActiveProfiles(context, activeProfiles);
            return true;
        }
        return false;
    }

    public Boolean removeActiveProfile(Context context, Profile profile){
        Vector<Profile> activeProfiles = getActiveProfiles(context);
        if(activeProfiles.remove(profile)){
            setActiveProfiles(context, activeProfiles);
            return true;
        }
        return false;
    }

    public Boolean createSchedule(Context context, String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly){
        Vector<Schedule> schedules = getSchedules(context);

        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
        schedules.add(schedule);

        setSchedules(context, schedules);

        return true;
    }

    public Boolean modifySchedule(Context context, Schedule s){

        Vector<Schedule> allSchedules = getSchedules(context);
        boolean found = false;

        for (int i= 0; i < allSchedules.size(); i++){
            if (allSchedules.get(i).getId() == s.getId()){

                //Note - you HAVE to pass in a COPY of the Schedule you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.
                allSchedules.get(i).setRepeatWeekly(s.repeatWeekly());
                allSchedules.get(i).setTimeBlocks(s.getTimeBlocks());
                allSchedules.get(i).setName(s.getName());
                allSchedules.get(i).setProfiles(s.getProfiles());
                found = true;
                break;
            }
        }

        return found;
    }

    public Boolean modifySchedule(Context context, Schedule s, int id){

        Vector<Schedule> allSchedules = getSchedules(context);
        boolean found = false;

        for (int i= 0; i < allSchedules.size(); i++){
            if (allSchedules.get(i).getId() == id){

                //Note - you HAVE to pass in a COPY of the profile you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.
                allSchedules.get(i).setRepeatWeekly(s.repeatWeekly());
                allSchedules.get(i).setTimeBlocks(s.getTimeBlocks());
                allSchedules.get(i).setName(s.getName());
                allSchedules.get(i).setProfiles(s.getProfiles());
                found = true;
                break;
            }
        }

        return found;
    }

    public Boolean removeSchedule(Context context, Schedule schedule){
        Vector<Schedule> schedules = getSchedules(context);
        if(schedules.remove(schedule)){
            setSchedules(context, schedules);
            return true;
        }
        return false;
    }

    public Boolean createTimer(Context context, String name, Long initialDuration, Vector<Profile> timerProfiles){
        Vector<FocusTimer> timers = getTimers(context);
        FocusTimer timer = new FocusTimer(name, initialDuration, timerProfiles);
        timers.add(timer);
        setTimers(context, timers);
        return true;
    }

    //TODO Implement timers... Yay
//    public Boolean modifyTimer(Context context, String name, Long initialDuration, Profile newProfile){
//        Vector<FocusTimer> timers = getTimers(context);
//
//        for (int i = 0; i < timers.size(); i++){
//            if (timers.get(i).getName() == name){
//                timers.get(i).setInitialDuration(initialDuration);
//                timers.get(i).addProfile(newProfile);
//            }
//        }
//
//        setTimers(context, timers);
//        return true;
//    }

    public Boolean removeTimer(Context context, FocusTimer timer){
        Vector<FocusTimer> timers = getTimers(context);
        if(timers.remove(timer)){
            setTimers(context, timers);
            return true;
        }
        return false;
    }
//
//    public Boolean createNotification(Context context, Vector<Profile> profiles, android.app.Notification notification){
//        Vector<Notification> notifications = getNotifications(context);
//        Notification not = new Notification(notification, profiles);
//        if(notifications.add(not)) {
//            setNotifications(context, notifications);
//            return true;
//        }
//        return false;
//    }
//
//    public Boolean removeNotification(Context context, Notification notification){
//        Vector<Notification> notifications = getNotifications(context);
//        if(notifications.remove(notification)){
//            setNotifications(context, notifications);
//            return true;
//        }
//        return false;
//    }


//    public Vector<Notification> getNotifications(Context context) {
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        Gson gson = new Gson();
//        String json = mPrefs.getString(context.getString(R.string.NotificationsKey), "");
//        Type typeOfVector = new TypeToken<Vector<Notification>>(){}.getType();
//        Vector<Notification> obj = gson.fromJson(json, typeOfVector);
//
//        if(obj != null) {
//            Log.d("Global-popAllProf", "There are " + obj.size() + " active apps");
//            return obj;
//        } else {
//            return new Vector<Notification>();
//        }
//    }


    public void setAllProfiles(Context context, Vector<Profile> allProfiles) {

        profileList = allProfiles;

    }

    public void setActiveProfiles(Context context, Vector<Profile> activeProfiles) {
        Global.activeProfiles = activeProfiles;
    }

    public void setSchedules(Context context, Vector<Schedule> allSchedules) {

        scheduleList = allSchedules;

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(allSchedules);
//        prefsEditor.putString(context.getString(R.string.SchedulesKey), json);
//        prefsEditor.commit();
    }

    public void setTimers(Context context, Vector<FocusTimer> timers) {
        Global.timerList = timers;
    }

//    public void setNotifications(Context context, Vector<Notification> notifications) {
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(notifications);
//        prefsEditor.putString(context.getString(R.string.NotificationsKey), json);
//        prefsEditor.commit();
//    }


    //This could almost certainly be optimized, but it works... So...
    public int getProfileUniqueID(){
        int possibleId = new Random().nextInt();

        Vector<Integer> existingIds = new Vector<>();

        for(Profile p : profileList){
            existingIds.add(p.getId());
        }

        while(existingIds.contains(possibleId)){
            possibleId = new Random().nextInt();
        }
        return possibleId;
    }

    public int getUniqueScheduleID(){
        int possibleId = new Random().nextInt();

        Vector<Integer> existingIds = new Vector<>();

        for(Schedule s : scheduleList){
            existingIds.add(s.getId());
        }

        while(existingIds.contains(possibleId)){
            possibleId = new Random().nextInt();
        }
        return possibleId;
    }
}
