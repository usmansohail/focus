package proflo.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 *
 * Modified to be a SharedPreference wrapper on 10/18/17 by Forrest.
 */

class Global {
    private static final Global instance = new Global();

    static Global getInstance() { return instance; }

    //TODO What the fuck is this precisely
    private Calendar schedule;


    private Global() {
    }

    public Vector<Profile> getAllProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.AllProfileKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " total profiles");
            return obj;
        } else {
            return (new Vector<Profile>());
        }
    }

    public Vector<Profile> getActiveProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveProfilesKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());

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
        Vector<Schedule> vec = new Vector<>();
        Vector<Schedule> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " schedules");
            return obj;
        } else {
            return (new Vector<Schedule>());
        }
    }

    public Calendar getSchedule(Context context) {
        return schedule;
    }

    public Vector<Timer> getTimers(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.TimersKey), "");
        Vector<Timer> vec = new Vector<>();
        Vector<Timer> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " timers");
            return obj;
        } else {
            return (new Vector<Timer>());
        }
    }

    public Vector<ApplicationInfo> getAllApps(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.AppsKey), "");
        Vector<ApplicationInfo> vec = new Vector<>();
        Vector<ApplicationInfo> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " apps");
            return obj;
        } else {
            return (new Vector<ApplicationInfo>());
        }
    }

    public void setAllApps(Context context, Vector<ApplicationInfo> apps){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(apps);
        prefsEditor.putString(context.getString(R.string.AppsKey), json);
        prefsEditor.commit();
    }

    public Vector<Profile> getActiveProfilesForApp(Context context, ApplicationInfo appID){
        Vector<Profile> activeProfiles = getActiveProfiles(context);

        for (int i = 0; i < activeProfiles.size(); i++){
            for (int j = 0; j < activeProfiles.get(i).getProfileApps().size(); j++){
                if (activeProfiles.get(i).getProfileApps().get(j) == appID){
                    activeProfiles.add(activeProfiles.get(i));
                }
            }
        }

        return activeProfiles;
    }

    //new profiles isActive set to false
    public Boolean createProfile(Context context, String name, Vector<ApplicationInfo> apps){
        Vector<Profile> allProfiles = getAllProfiles(context);

        Profile tprofile = new Profile(name, apps, false);
        if(allProfiles.add(tprofile)){
            setAllProfiles(context, allProfiles);
            return true;
        }
        return false;
    }

    public void addProfile(Context context, Profile profile){

        Vector<Profile> allProfiles = getAllProfiles(context);

        allProfiles.add(profile);

    }

    public Boolean modifyProfile(Context context, String name, Vector<ApplicationInfo> apps){

        Vector<Profile> activeProfiles = getActiveProfiles(context);

        for (int i= 0; i < activeProfiles.size(); i++){
            if (activeProfiles.get(i).getName() == name ){
                activeProfiles.get(i).setProfileApps(apps);
            }
        }

        return true;
    }


    public Vector<ApplicationInfo> getActiveApps(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveAppsKey), "");
        Vector<ApplicationInfo> vec = new Vector<>();
        Vector<ApplicationInfo> obj = gson.fromJson(json, vec.getClass());

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

    public Boolean createSchedule(Context context, String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly){
        Vector<Schedule> schedules = getSchedules(context);

        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
        schedules.add(schedule);

        setSchedules(context, schedules);

        return true;
    }

    public Boolean modifySchedule(Context context, String name, TimeBlock timeBlocks, Boolean repeatWeekly){
        Vector<Schedule> schedules = getSchedules(context);

        for (int i = 0; i < schedules.size(); i++){
            if (schedules.get(i).getName() == name){
                schedules.get(i).addTimeBlock(timeBlocks);
                schedules.get(i).setRepeatWeekly(repeatWeekly);

            }
        }
        setSchedules(context, schedules);

        return true;
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
        Vector<Timer> timers = getTimers(context);
        Timer timer = new Timer(name, initialDuration, timerProfiles);
        timers.add(timer);
        setTimers(context, timers);
        return true;
    }

    public Boolean modifyTimer(Context context, String name, Long initialDuration, Profile newProfile){
        Vector<Timer> timers = getTimers(context);

        for (int i = 0; i < timers.size(); i++){
            if (timers.get(i).getName() == name){
                timers.get(i).setInitialDuration(initialDuration);
                timers.get(i).addProfile(newProfile);
            }
        }

        setTimers(context, timers);
        return true;
    }

    public Boolean removeTimer(Context context, Timer timer){
        Vector<Timer> timers = getTimers(context);
        if(timers.remove(timer)){
            setTimers(context, timers);
            return true;
        }
        return false;
    }

    public Boolean createNotification(Context context, Vector<Profile> profiles, android.app.Notification notification){
        Vector<Notification> notifications = getNotifications(context);
        Notification not = new Notification(notification, profiles);
        if(notifications.add(not)) {
            setNotifications(context, notifications);
            return true;
        }
        return false;
    }

    public Boolean removeNotification(Context context, Notification notification){
        Vector<Notification> notifications = getNotifications(context);
        if(notifications.remove(notification)){
            setNotifications(context, notifications);
            return true;
        }
        return false;
    }


    public Vector<Notification> getNotifications(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.NotificationsKey), "");
        Vector<Notification> vec = new Vector<>();
        Vector<Notification> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " active apps");
            return obj;
        } else {
            return new Vector<Notification>();
        }
    }


    public void setAllProfiles(Context context, Vector<Profile> allProfiles) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allProfiles);
        prefsEditor.putString(context.getString(R.string.AllProfileKey), json);
        prefsEditor.commit();
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

    public void setNotifications(Context context, Vector<Notification> notifications) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notifications);
        prefsEditor.putString(context.getString(R.string.NotificationsKey), json);
        prefsEditor.commit();
    }
}
