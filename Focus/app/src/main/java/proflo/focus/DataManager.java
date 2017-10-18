package proflo.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Vector;

/**
 * Created by Forrest on 10/18/2017.
 */

class DataManager {
    private static DataManager instance;

    static DataManager getInstance() {
        if(instance == null) instance = getSync();
        return instance;
    }

    private static synchronized DataManager getSync() {
        if(instance == null) instance = new DataManager();
        return instance;
    }

    private DataManager() {
    }

    //TODO THIS IS NOT IDEAL. Should save parts of the application at a time based on what's been modified. Not all at once.

    //Currently saves all information in the app to the global variable. Yay.
    public void saveGlobal(Context context){
        //AllProfiles
        saveAllProfiles(context);
        //activeProfiles
        saveActiveProfiles(context);
        //activeApps
        saveActiveApps(context);
        //schedules
        saveSchedules(context);
        //TODO schedule (Calendar?)

        //timers
        saveTimers(context);
        //notifications
        saveNotifications(context);
    }


    //Stores AllProfiles from Global into SharedPreferences
    private void saveAllProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getAllProfiles());
        prefsEditor.putString(context.getString(R.string.AllProfileKey), json);
        prefsEditor.apply();
    }

    //Stores AllProfiles from Global into SharedPreferences
    private void saveActiveProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getActiveProfiles());
        prefsEditor.putString(context.getString(R.string.ActiveProfilesKey), json);
        prefsEditor.apply();
    }

    //Stores AllProfiles from Global into SharedPreferences
    private void saveActiveApps(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getActiveApps());
        prefsEditor.putString(context.getString(R.string.ActiveAppsKey), json);
        prefsEditor.apply();
    }

    //Stores AllProfiles from Global into SharedPreferences
    private void saveSchedules(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getSchedules());
        prefsEditor.putString(context.getString(R.string.SchedulesKey), json);
        prefsEditor.apply();
    }

    //Stores AllProfiles from Global into SharedPreferences
    private void saveTimers(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getTimers());
        prefsEditor.putString(context.getString(R.string.TimersKey), json);
        prefsEditor.apply();
    }

    private void saveNotifications(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Global.getInstance().getNotifications());
        prefsEditor.putString(context.getString(R.string.NotificationsKey), json);
        prefsEditor.apply();
    }

    //This is pretty okay. Populates the global data with data from shared preferences.
    //BE CAREFUL, HOWEVER, as it ensures that the globals are cleared before populating.
    public void populateGlobal(Context context){
        
        populateAllProfiles(context);
        populateActiveProfiles(context);
        populateActiveApps(context);
        populateSchedules(context);
        populateTimers(context);
        populateNotifications(context);
        
    }

    private void populateAllProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.AllProfileKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setAllProfiles(obj);
    }

    private void populateActiveProfiles(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveProfilesKey), "");
        Vector<Profile> vec = new Vector<>();
        Vector<Profile> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setActiveProfiles(obj);
    }

    private void populateActiveApps(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.ActiveAppsKey), "");
        Vector<String> vec = new Vector<>();
        Vector<String> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setActiveApps(obj);
    }

    private void populateSchedules(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.SchedulesKey), "");
        Vector<Schedule> vec = new Vector<>();
        Vector<Schedule> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setSchedules(obj);
    }

    private void populateTimers(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.TimersKey), "");
        Vector<Timer> vec = new Vector<>();
        Vector<Timer> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setTimers(obj);
    }

    private void populateNotifications(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(context.getString(R.string.NotificationsKey), "");
        Vector<Notification> vec = new Vector<>();
        Vector<Notification> obj = gson.fromJson(json, vec.getClass());
        Global.getInstance().setNotifications(obj);
    }


}
