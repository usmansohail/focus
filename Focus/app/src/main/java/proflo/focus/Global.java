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

    private Context mContext;

    //TODO What the fuck is this precisely
    private Calendar schedule;

    public boolean loaded = false;

    private Global() {
        if(mContext == null)
            mContext = App.get();
    }

    public Vector<Profile> getAllProfiles() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.AllProfileKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " total profiles");
            return obj;
        } else {
            return (new Vector<Profile>());
        }
    }

    public Vector<Profile> getActiveProfiles() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.ActiveProfilesKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " active profiles");
            return obj;
        } else {
            return (new Vector<Profile>());
        }
    }

    public Vector<Schedule> getSchedules() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.SchedulesKey), "");
        Vector<Schedule> vec = new Vector<>();
        Vector<Schedule> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " schedules");
            return obj;
        } else {
            return (new Vector<Schedule>());
        }
    }

    public Calendar getSchedule() {
        return schedule;
    }

    public Vector<Timer> getTimers() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.TimersKey), "");
        Vector<Timer> vec = new Vector<>();
        Vector<Timer> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " timers");
            return obj;
        } else {
            return (new Vector<Timer>());
        }
    }

    public Vector<ApplicationInfo> getAllApps() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.AppsKey), "");
        Vector<ApplicationInfo> vec = new Vector<>();
        Vector<ApplicationInfo> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " apps");
            return obj;
        } else {
            return (new Vector<ApplicationInfo>());
        }
    }

    public void setAllApps(Vector<ApplicationInfo> apps){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(apps);
        prefsEditor.putString(mContext.getString(R.string.AppsKey), json);
        prefsEditor.commit();
    }

    public void addProfile(Profile profile){
        allProfiles.add(profile);
    }

    public Vector<Profile> getActiveProfilesForApp(ApplicationInfo appID){
        Vector<Profile> activeProfiles = getActiveProfiles();

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
    public Boolean createProfile(String name, Vector<ApplicationInfo> apps){
        Profile tprofile = new Profile(name, apps, false);
        return true;
    }

    public Boolean modifyProfile(String name, Vector<ApplicationInfo> apps){

        Vector<Profile> activeProfiles = getActiveProfiles();

        for (int i= 0; i < activeProfiles.size(); i++){
            if (activeProfiles.get(i).getName() == name ){
                activeProfiles.get(i).setProfileApps(apps);
            }
        }

        return true;
    }


    public Vector<ApplicationInfo> getActiveApps() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.ActiveAppsKey), "");
        Vector<ApplicationInfo> vec = new Vector<>();
        Vector<ApplicationInfo> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global", "There are " + obj.size() + " active apps");
            return obj;
        } else {
            return new Vector<ApplicationInfo>();
        }
    }

    public void setActiveApps(Vector<ApplicationInfo> activeApps) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activeApps);
        prefsEditor.putString(mContext.getString(R.string.ActiveAppsKey), json);
        prefsEditor.commit();
    }

    public boolean removeProfile(Profile profile){
        Vector<Profile> allProfiles = getAllProfiles();
        if(allProfiles.remove(profile)){
            setAllProfiles(allProfiles);
            return true;
        } else {
            return false;
        }
    }

    public Boolean addActiveProfile(Profile profile){
        Vector<Profile> activeProfiles = getActiveProfiles();
        Vector<Profile> allProfiles = getAllProfiles();

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
            setActiveProfiles(activeProfiles);
            return true;
        }
        return false;
    }

    public Boolean removeActiveProfile(Profile profile){
        Vector<Profile> activeProfiles = getActiveProfiles();
        if(activeProfiles.remove(profile)){
            setActiveProfiles(activeProfiles);
            return true;
        }
        return false;
    }

    public Boolean createSchedule(String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly){
        Vector<Schedule> schedules = getSchedules();

        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
        schedules.add(schedule);

        setSchedules(schedules);

        return true;
    }

    public Boolean modifySchedule(String name, TimeBlock timeBlocks, Boolean repeatWeekly){
        Vector<Schedule> schedules = getSchedules();

        for (int i = 0; i < schedules.size(); i++){
            if (schedules.get(i).getName() == name){
                schedules.get(i).addTimeBlock(timeBlocks);
                schedules.get(i).setRepeatWeekly(repeatWeekly);

            }
        }
        setSchedules(schedules);

        return true;
    }

    public Boolean removeSchedule(Schedule schedule){
        Vector<Schedule> schedules = getSchedules();
        if(schedules.remove(schedule)){
            setSchedules(schedules);
            return true;
        }
        return false;
    }

    public Boolean createTimer(String name, Long initialDuration, Vector<Profile> timerProfiles){
        Vector<Timer> timers = getTimers();
        Timer timer = new Timer(name, initialDuration, timerProfiles);
        timers.add(timer);
        setTimers(timers);
        return true;
    }

    public Boolean modifyTimer(String name, Long initialDuration, Profile newProfile){
        Vector<Timer> timers = getTimers();

        for (int i = 0; i < timers.size(); i++){
            if (timers.get(i).getName() == name){
                timers.get(i).setInitialDuration(initialDuration);
                timers.get(i).addProfile(newProfile);
            }
        }

        setTimers(timers);
        return true;
    }

    public Boolean removeTimer(Timer timer){
        Vector<Timer> timers = getTimers();
        if(timers.remove(timer)){
            setTimers(timers);
            return true;
        }
        return false;
    }

    public Boolean createNotification(Vector<Profile> profiles, android.app.Notification notification){
        Vector<Notification> notifications = getNotifications();
        Notification not = new Notification(notification, profiles);
        if(notifications.add(not)) {
            setNotifications(notifications);
            return true;
        }
        return false;
    }

    public Boolean removeNotification(Notification notification){
        Vector<Notification> notifications = getNotifications();
        if(notifications.remove(notification)){
            setNotifications(notifications);
            return true;
        }
        return false;
    }


    public Vector<Notification> getNotifications() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.NotificationsKey), "");
        Vector<Notification> vec = new Vector<>();
        Vector<Notification> obj = gson.fromJson(json, vec.getClass());

        if(obj != null) {
            Log.d("Global-popAllProf", "There are " + obj.size() + " active apps");
            return obj;
        } else {
            return new Vector<Notification>();
        }
    }


    public void setAllProfiles(Vector<Profile> allProfiles) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allProfiles);
        prefsEditor.putString(mContext.getString(R.string.AllProfileKey), json);
        prefsEditor.commit();
    }

    public void setActiveProfiles(Vector<Profile> activeProfiles) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activeProfiles);
        prefsEditor.putString(mContext.getString(R.string.ActiveAppsKey), json);
        prefsEditor.commit();
    }

    public void setSchedules(Vector<Schedule> allSchedules) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allSchedules);
        prefsEditor.putString(mContext.getString(R.string.SchedulesKey), json);
        prefsEditor.commit();
    }

    public void setTimers(Vector<Timer> timers) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timers);
        prefsEditor.putString(mContext.getString(R.string.TimersKey), json);
        prefsEditor.commit();
    }

    public void setNotifications(Vector<Notification> notifications) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notifications);
        prefsEditor.putString(mContext.getString(R.string.NotificationsKey), json);
        prefsEditor.commit();
    }

    public void storeContext(Context applicationContext) {
        mContext = applicationContext;
    }
}
