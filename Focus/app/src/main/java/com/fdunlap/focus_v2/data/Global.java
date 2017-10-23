package com.fdunlap.focus_v2.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.activities.MainActivity;
import com.fdunlap.focus_v2.models.Profile;
import com.fdunlap.focus_v2.models.Schedule;
import com.fdunlap.focus_v2.models.Timer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Vector;

/**
 * Modified to be a SharedPreference wrapper on 10/18/17 by Forrest.
 */

public class Global {
    private static final Global instance = new Global();

    public static Global getInstance() { return instance; }

    static Vector<PackageInfo> packageList = new Vector<>();
    static Vector<Profile> profileList = new Vector<>();

    private Global() {
    }

    public Vector<Profile> getAllProfiles(Context context) {

        return profileList;

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        Gson gson = new Gson();
//        String json = mPrefs.getString(context.getString(R.string.AllProfileKey), "");
//        Type typeOfVector = new TypeToken<Vector<Profile>>(){}.getType();
//        Vector<Profile> obj = gson.fromJson(json, typeOfVector);
//
//        if(obj != null) {
//            Log.d("Global-popAllProf", "There are " + obj.size() + " total profiles");
//            return obj;
//        } else {
//            return (new Vector<Profile>());
//        }
    }

    public Vector<Profile> getActiveProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveProfilesKey), "");
        Type typeOfVector = new TypeToken<Vector<Profile>>(){}.getType();
        Vector<Profile> obj = gson.fromJson(json, typeOfVector);

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " active profiles");
            return obj;
        } else {
            return (new Vector<Profile>());
        }
    }

    public Vector<Schedule> getSchedules(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.SchedulesKey), "");
        Type typeOfVector = new TypeToken<Vector<Schedule>>(){}.getType();
        Vector<Schedule> obj = gson.fromJson(json, typeOfVector);

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " schedules");
            return obj;
        } else {
            return (new Vector<Schedule>());
        }
    }

    public Calendar getSchedule(Context context) {
        return null;
    }

    public Vector<Timer> getTimers(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.TimersKey), "");
        Type typeOfVector = new TypeToken<Vector<Timer>>(){}.getType();
        Vector<Timer> obj = gson.fromJson(json, typeOfVector);

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " timers");
            return obj;
        } else {
            return (new Vector<Timer>());
        }
    }

    public Vector<PackageInfo> getAllApps(Context context) {

        if(MainActivity.packageList1.isEmpty()){
            Log.d("getAllApps", "Package List empty?");
        }

        return packageList;

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        Gson gson = new Gson();
//        String json = mPrefs.getString(context.getString(R.string.AppsKey), "");
//        Type typeOfVectorOfApplicationInfo = new TypeToken<Vector<PackageInfo>>(){}.getType();
//        Vector<PackageInfo> obj = gson.fromJson(json, typeOfVectorOfApplicationInfo);
//
//        if(obj != null) {
//            Log.d("Global-popAllProf", "There are " + obj.size() + " apps");
//            return obj;
//        } else {
//            return (new Vector<PackageInfo>());
//        }
    }

    public void setAllApps(Context context, Vector<PackageInfo> apps){

        packageList = apps;

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(apps);
//        prefsEditor.putString(context.getString(R.string.AppsKey), json);
//        prefsEditor.commit();
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

    public Boolean modifyProfile(Context context, String name, Vector<PackageInfo> apps){

        Vector<Profile> activeProfiles = getActiveProfiles(context);

        for (int i= 0; i < activeProfiles.size(); i++){
            if (activeProfiles.get(i).getName() == name ){
                activeProfiles.get(i).setApps(apps);
            }
        }

        return true;
    }


    public Vector<ApplicationInfo> getActiveApps(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveAppsKey), "");
        Type typeOfVectorOfApplicationInfo = new TypeToken<Vector<ApplicationInfo>>(){}.getType();
        Vector<ApplicationInfo> obj = gson.fromJson(json, typeOfVectorOfApplicationInfo);

        if(obj != null) {
            Log.d("Global", "There are " + obj.size() + " active apps");
            return obj;
        } else {
            return new Vector<ApplicationInfo>();
        }
    }

    public void setActiveApps(Context context, Vector<ApplicationInfo> activeApps) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activeApps);
        prefsEditor.putString(context.getString(R.string.ActiveAppsKey), json);
        prefsEditor.commit();
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

//    public Boolean createSchedule(Context context, String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly){
//        Vector<Schedule> schedules = getSchedules(context);
//
//        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
//        schedules.add(schedule);
//
//        setSchedules(context, schedules);
//
//        return true;
//    }
//
//    public Boolean modifySchedule(Context context, String name, TimeBlock timeBlocks, Boolean repeatWeekly){
//        Vector<Schedule> schedules = getSchedules(context);
//
//        for (int i = 0; i < schedules.size(); i++){
//            if (schedules.get(i).getName() == name){
//                schedules.get(i).addTimeBlock(timeBlocks);
//                schedules.get(i).setRepeatWeekly(repeatWeekly);
//
//            }
//        }
//        setSchedules(context, schedules);
//
//        return true;
//    }

    public Boolean removeSchedule(Context context, Schedule schedule){
        Vector<Schedule> schedules = getSchedules(context);
        if(schedules.remove(schedule)){
            setSchedules(context, schedules);
            return true;
        }
        return false;
    }

//    public Boolean createTimer(Context context, String name, Long initialDuration, Vector<Profile> timerProfiles){
//        Vector<Timer> timers = getTimers(context);
//        Timer timer = new Timer(name, initialDuration, timerProfiles);
//        timers.add(timer);
//        setTimers(context, timers);
//        return true;
//    }
//
//    public Boolean modifyTimer(Context context, String name, Long initialDuration, Profile newProfile){
//        Vector<Timer> timers = getTimers(context);
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

    public Boolean removeTimer(Context context, Timer timer){
        Vector<Timer> timers = getTimers(context);
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

//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(allProfiles);
//        prefsEditor.putString(context.getString(R.string.AllProfileKey), json);
//        prefsEditor.commit();
    }

    public void setActiveProfiles(Context context, Vector<Profile> activeProfiles) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveProfilesKey), "");
        prefsEditor.putString(context.getString(R.string.ActiveAppsKey), json);
        prefsEditor.commit();
    }

    public void setSchedules(Context context, Vector<Schedule> allSchedules) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allSchedules);
        prefsEditor.putString(context.getString(R.string.SchedulesKey), json);
        prefsEditor.commit();
    }

    public void setTimers(Context context, Vector<Timer> timers) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timers);
        prefsEditor.putString(context.getString(R.string.TimersKey), json);
        prefsEditor.commit();
    }

//    public void setNotifications(Context context, Vector<Notification> notifications) {
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(notifications);
//        prefsEditor.putString(context.getString(R.string.NotificationsKey), json);
//        prefsEditor.commit();
//    }
}
