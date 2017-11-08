package com.proflow.focus_v2.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.proflow.focus_v2.models.time;
import com.proflow.focus_v2.models.FocusNotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Modified to be a SharedPreference wrapper on 10/18/17 by Forrest.
 */

public class Global {
    private static final Global instance = new Global();

    public static Global getInstance() {
        return instance;
    }

    public static final String apps_file_name = "APPS";
    public static final String profiles_file_name = "PROFILES";
    public static final String schedules_file_name = "SCHEDULES";
    public static final String timers_file_name = "TIMERS";
    public static final String notifications_file_name = "NOTIFICATIONS";

    public void clearPreferences(Context context) {
        context.getSharedPreferences(apps_file_name, 0).edit().clear().commit();
        context.getSharedPreferences(profiles_file_name, 0).edit().clear().commit();
        context.getSharedPreferences(schedules_file_name, 0).edit().clear().commit();
    }

    private static Vector<PackageInfo> packageList = new Vector<>();
    private static Vector<Profile> profileList = new Vector<>();
    private static Vector<Schedule> scheduleList = new Vector<>();
    private static Vector<FocusTimer> timerList = new Vector<>();
    private static Vector<FocusNotification> notifications = new Vector<>();

    private Global() {
    }


    private boolean appsValid = false;
    private boolean profilesValid = false;
    private boolean schedulesValid = false;
    private boolean timersValid = false;
    private boolean notificationsValid = false;

    public void invalidateApps() {
        appsValid = false;
    }

    public void invalidateProfiles() {
        profilesValid = false;
    }

    public void invalidateSchedules() {
        schedulesValid = false;
    }

    public void invalidateTimers() {
        timersValid = false;
    }

    public void invalidateFocusNotifications() {
        notificationsValid = false;
    }

    //Used only as a last-resort in the no-context methods. Avoid.
    private Context mMostRecentContext = null;


    public void synchAll(Context context) {
        mMostRecentContext = context;

        if (!appsValid) {
            synchApps(context);
        }
        if (!profilesValid) {
            synchProfiles(context);
        }
        if (!schedulesValid) {
            synchSchedules(context);
        }
        if (!timersValid) {
            synchTimers(context);
        }
        if (!notificationsValid) {
            synchFocusNotifications(context);
        }
    }

    /*
    PROFILES
     */

    public void setProfiles(Context context, Vector<Profile> allProfiles) {

        SharedPreferences sp = context.getSharedPreferences(profiles_file_name, 0);
        Vector<Integer> profileIDs = new Vector<>();

        for (Profile p : allProfiles) {

            int id = p.getId();
            profileIDs.add(id);

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("" + id + "_isActive", p.isActive());
            editor.putString("" + id + "_name", p.getName());

            editor.putInt("" + id + "_size", p.getApps().size());

            Log.d(TAG, "setAllPro p.size: " + p.getApps().size() + "name; " + p.getName());

            for (int i = 0; i < p.getApps().size(); i++) {
                String name = p.getApps().get(i).packageName;
                editor.putString("" + id + "_" + i, name);
            }
            editor.commit();
        }

        sp.edit().putInt("profileIDs_size", profileIDs.size()).commit();
        for (int i = 0; i < profileIDs.size(); i++) {
            sp.edit().putInt("profileIDs_" + i, profileIDs.get(i)).commit();
        }
        invalidateProfiles();
    }

    //For this portion of the project, this is naive and will slowdown over time. So in next version
    //implement db.
    public Vector<Profile> getAllProfiles(Context context) {
        if (!profilesValid) {
            synchProfiles(context);
        }
        return profileList;
    }

    public void synchProfiles(Context context) {
        Vector<Profile> allProfiles = new Vector<>();
        PackageManager pm = context.getPackageManager();

        SharedPreferences sp = context.getSharedPreferences(profiles_file_name, 0);
        Vector<Integer> profileIDs = new Vector<>();

        int profileIDSize = sp.getInt("profileIDs_size", 0);
        profileIDs.ensureCapacity(profileIDSize);

        for (int i = 0; i < profileIDSize; i++) {
            profileIDs.add(i, sp.getInt("profileIDs_" + i, 0));
        }

        for (int i = 0; i < profileIDSize; i++) {
            int id = profileIDs.get(i);
            String pName = sp.getString("" + id + "_name", "");

            Log.d(TAG, "Found profile: " + pName);

            boolean pIsActive = sp.getBoolean("" + id + "_isActive", false);
            int numApps = sp.getInt("" + id + "_size", 0);

            Log.d(TAG, "getAllPro p.size: " + numApps + "name; " + pName);

            Vector<PackageInfo> pi_vec = new Vector<>();
            for (int j = 0; j < numApps; j++) {
                String packageName = sp.getString("" + id + "_" + j, "");
                try {
                    PackageInfo pi = pm.getPackageInfo(packageName, 0);
                    pi_vec.add(pi);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Profile pro = new Profile(pName, pi_vec, id);
            pro.setActive(pIsActive);
            allProfiles.add(pro);
        }

        profileList = allProfiles;
        profilesValid = true;
    }

    public void addProfile(Context context, Profile profile) {

        Vector<Profile> allProfiles = getAllProfiles(context);

        allProfiles.add(profile);

        setProfiles(context, allProfiles);
    }

    public Boolean modifyProfile(Context context, Profile p) {

        Vector<Profile> allProfiles = getAllProfiles(context);
        boolean found = false;

        for (int i = 0; i < allProfiles.size(); i++) {
            if (allProfiles.get(i).getId() == p.getId()) {

                //Note - you HAVE to pass in a COPY of the profile you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.

                Log.d(TAG, "FOUND PROFILE IN MODIFY PROFILE");

                for (PackageInfo pi : p.getApps()) {
                    Log.d(TAG, pi.packageName);
                }

                allProfiles.get(i).setApps(p.getApps());
                allProfiles.get(i).setName(p.getName());
                allProfiles.get(i).setActive(p.isActive());
                setProfiles(context, allProfiles);
                found = true;
                break;
            }
        }

        return found;
    }

    public Boolean modifyProfile(Context context, Profile p, int id) {

        Vector<Profile> allProfiles = getAllProfiles(context);
        boolean found = false;

        for (int i = 0; i < allProfiles.size(); i++) {
            if (allProfiles.get(i).getId() == id) {

                //Note - in this version you can pass in an arbitrary profile assuming you know the
                //id of the profile you're looking to change.
                //FURTHER: Note that the profile's id doesn't change.
                allProfiles.get(i).setApps(p.getApps());
                allProfiles.get(i).setName(p.getName());
                allProfiles.get(i).setActive(p.isActive());
                setProfiles(context, allProfiles);
                found = true;
                break;
            }
        }


        return found;
    }

    public boolean removeProfile(Context context, Profile p) {

        Vector<Profile> allProfiles = getAllProfiles(context);
        boolean found = false;

        for (int i = 0; i < allProfiles.size(); i++) {
            if (allProfiles.get(i).getId() == p.getId()) {

                //Note - you HAVE to pass in a COPY of the profile you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.

                Log.d(TAG, "FOUND PROFILE IN REMOVE PROFILE");

                allProfiles.remove(i);
                setProfiles(context, allProfiles);
                found = true;
                break;
            }
        }

        return found;
    }

    //This could almost certainly be optimized, but it works... So...
    public int getProfileUniqueID() {
        int possibleId;
        if(profileList.size() == 0){
            possibleId = 0;
        }
        else{
            Vector<Integer> existingIds = new Vector<>();

            for (Profile p : profileList) {
                existingIds.add(p.getId());
            }
            possibleId = existingIds.get(existingIds.size() - 1) + 1;
        }
        if (!profilesValid) {
            synchProfiles(mMostRecentContext);
        }
        return possibleId;
    }

    public Profile getProfileByID(Context context, int id) {
        if (!profilesValid) {
            synchProfiles(context);
        }

        for (Profile p : profileList) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /*
    SCHEDULES
     */

    public Vector<Schedule> getSchedules() {
        if (!schedulesValid) {
            synchSchedules(mMostRecentContext);
        }

        return scheduleList;
    }

    //Yeah wow that's not a small function.
    public void synchSchedules(Context context) {
        Vector<Schedule> allSchedules = new Vector<>();
        PackageManager pm = context.getPackageManager();
        SharedPreferences sp = context.getSharedPreferences(schedules_file_name, 0);

        Vector<Integer> scheduleIDs = new Vector<>();

        int scheduleIDSize = sp.getInt("scheduleIDs_size", 0);

        Log.d(TAG, "getSchedules: IDSize" + scheduleIDSize);

        for (int i = 0; i < scheduleIDSize; i++) {
            scheduleIDs.add(i, sp.getInt("scheduleIDs_" + i, 0));
        }


        for (int i = 0; i < scheduleIDSize; i++) {

            //Setup the id var.
            int id = scheduleIDs.get(i);

            //get the schedule primitives
            String sName = sp.getString(id + "_name", null);
            boolean sRepeat = sp.getBoolean(id + "_repeatWeekly", false);
            boolean sIsActive = sp.getBoolean(id + "_isActive", false);
            Vector<Profile> sProfiles = new Vector<>();

            int numProfiles = sp.getInt(id + "_numProfiles", 0);
            for (int j = 0; j < numProfiles; j++) {

                int pID = sp.getInt(id + "_profile_" + j, 0);
                boolean pIsActive = sp.getBoolean(id + "_profile_" + pID + "_isActive", false);

                Profile p = getProfileByID(context, pID);
                Log.d(TAG, "Found profile: " + p.getId() + " while populating schedule " + sName);

                sProfiles.add(p);
            }
            //Now on to reconstructing timeblocks!
            Vector<TimeBlock> timeBlocks = new Vector<>();
            int numTimeBlocks = sp.getInt(id + "_numTimeBlocks", 0);
            for (int j = 0; j < numTimeBlocks; j++) {
                int startHour = sp.getInt(id + "_timeBlock_" + j + "_startHour", 0);
                int startMinute = sp.getInt(id + "_timeBlock_" + j + "_startMinute", 0);
                int endHour = sp.getInt(id + "_timeBlock_" + j + "_endHour", 0);
                int endMinute = sp.getInt(id + "_timeBlock_" + j + "_endMinute", 0);

                ArrayList<String> dayIntStrings =
                        new ArrayList<>(Arrays.asList(sp.getString(id + "_timeBlock_" + j + "_dayString", "").split("\\s*,\\s*")));

                Vector<TimeBlock.day> days = new Vector<>();

                for(String dayInt : dayIntStrings){
                    if(!dayInt.isEmpty()) {
                        int d = Integer.parseInt(dayInt);
                        days.add(TimeBlock.day.fromInteger(d));
                    }
                }

                timeBlocks.add(
                        new TimeBlock(
                                new time(startHour, startMinute),
                                new time(endHour, endMinute),
                                days
                        )
                );
            }

            Schedule schedule = new Schedule(sName, timeBlocks, sProfiles, sRepeat, id);
            schedule.setActive(sIsActive);

            allSchedules.add(schedule);
        }
        scheduleList = allSchedules;
        schedulesValid = true;
    }

    public Vector<Schedule> getSchedules(Context context) {
        if (!schedulesValid) {
            synchSchedules(context);
        }
        return scheduleList;
    }

    public void setSchedules(Context context, Vector<Schedule> allSchedules) {
        SharedPreferences sp = context.getSharedPreferences(schedules_file_name, 0);
        Vector<Integer> scheduleIDs = new Vector<>();

        for (Schedule schedule : allSchedules) {
            //store ids so we can write a list of ids
            int id = schedule.getId();
            scheduleIDs.add(id);
            //open editor
            SharedPreferences.Editor editor = sp.edit();
            //put primitives in sp associated with id:
            editor.putString(id + "_name", schedule.getName());
            editor.putBoolean(id + "_repeatWeekly", schedule.repeatWeekly());
            editor.putBoolean(id + "_isActive", schedule.isActive());

            //Now parse the list of profiles associated with a schedule
            //Which unfortunately means writing a list of profiles...

            //store the number of profiles
            Vector<Profile> sProfiles = schedule.getProfiles();
            int numProfiles = sProfiles.size();
            editor.putInt(id + "_numProfiles", numProfiles);
            for (int i = 0; i < numProfiles; i++) {
                Profile p = sProfiles.get(i);
                int pID = p.getId();
                editor.putInt(id + "_profile_" + i, pID);
                editor.putBoolean(id + "_profile_" + pID + "_isActive", p.isActive());
            }

            //BUT WAIT THERE'S MORE!
            Vector<TimeBlock> sTimeBlocks = schedule.getTimeBlocks();
            int numTimeBlocks = sTimeBlocks.size();
            editor.putInt(id + "_numTimeBlocks", numTimeBlocks);
            for (int i = 0; i < numTimeBlocks; i++) {
                TimeBlock tb = sTimeBlocks.get(i);

                //YES IT'S ALL PRIMITIVES> HAHAHAHA
                int startHour = tb.getStartTime().hour;
                int startMinute = tb.getStartTime().minute;
                int endHour = tb.getEndTime().hour;
                int endMinute = tb.getEndTime().minute;

                editor.putInt(id + "_timeBlock_" + i + "_startHour", startHour);
                editor.putInt(id + "_timeBlock_" + i + "_startMinute", startMinute);
                editor.putInt(id + "_timeBlock_" + i + "_endHour", endHour);
                editor.putInt(id + "_timeBlock_" + i + "_endMinute", endMinute);

                StringBuilder sb = new StringBuilder();
                for(TimeBlock.day day : tb.getDays()){
                    sb.append(TimeBlock.day.toInteger(day)).append(",");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));

                editor.putString(id + "_timeBlock_" + i + "_dayString", sb.toString());
            }
            editor.commit();
        }

        sp.edit().putInt("scheduleIDs_size", scheduleIDs.size()).commit();
        for (int i = 0; i < scheduleIDs.size(); i++) {
            sp.edit().putInt("scheduleIDs_" + i, scheduleIDs.get(i)).commit();
        }

        invalidateSchedules();
    }

    public void addSchedule(Context context, Schedule schedule) {

        Vector<Schedule> allSchedules = getSchedules(context);
        Log.d(TAG, "Adding schedule with ID: " + schedule.getId());
        allSchedules.add(schedule);

        setSchedules(context, allSchedules);
    }


    public Boolean createSchedule(Context context, String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly) {
        Vector<Schedule> schedules = getSchedules(context);

        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
        schedules.add(schedule);

        setSchedules(context, schedules);

        return true;
    }

    public Boolean modifySchedule(Context context, Schedule s) {

        Log.d(TAG, "Modifying Schedule:" + s.getName());

        Vector<Schedule> allSchedules = getSchedules(context);
        boolean found = false;

        Log.d(TAG, "Number of schedules to check in modifySchedule: " + allSchedules.size());
        for (int i = 0; i < allSchedules.size(); i++) {
            if (allSchedules.get(i).getId().equals(s.getId())) {
                Log.d(TAG, "Found schedule: " + s.getName() + " ID: " + s.getId());
                //Note - you HAVE to pass in a COPY of the Schedule you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.
                allSchedules.get(i).setRepeatWeekly(s.repeatWeekly());
                allSchedules.get(i).setTimeBlocks(s.getTimeBlocks());
                allSchedules.get(i).setName(s.getName());
                allSchedules.get(i).setProfiles(s.getProfiles());
                setSchedules(context, allSchedules);
                found = true;
                break;
            }
        }
        if(!found){
            Log.d(TAG, "Schedule not found: " + s.getName() + " ID: " + s.getId());
        }

        return found;
    }

    public Boolean modifySchedule(Context context, Schedule s, int id) {

        Vector<Schedule> allSchedules = getSchedules(context);
        boolean found = false;

        for (int i = 0; i < allSchedules.size(); i++) {
            if (allSchedules.get(i).getId().equals(id)) {

                //Note - you HAVE to pass in a COPY of the profile you modified for this to work.
                //This is due to ID matching, and allows you to change the name.
                //FURTHER: Note that the profile's id doesn't change.
                allSchedules.get(i).setRepeatWeekly(s.repeatWeekly());
                allSchedules.get(i).setTimeBlocks(s.getTimeBlocks());
                allSchedules.get(i).setName(s.getName());
                allSchedules.get(i).setProfiles(s.getProfiles());
                setSchedules(context, allSchedules);
                found = true;
                break;
            }
        }
        return found;
    }

    public Boolean removeSchedule(Context context, Schedule schedule) {
        Vector<Schedule> schedules = getSchedules(context);
        if (schedules.remove(schedule)) {
            setSchedules(context, schedules);
            return true;
        }
        return false;
    }

    //Wow this is a terrible implementation and I hate myself for it.
    //TODO Optimize this entire fucking TERRIBLE class.
    public int getUniqueScheduleID() {
        int possibleId = new Random().nextInt();
        if (!schedulesValid) {
            synchSchedules(mMostRecentContext);
        }

        Vector<Integer> existingIds = new Vector<>();

        for (Schedule s : scheduleList) {
            existingIds.add(s.getId());
        }

        while (existingIds.contains(possibleId)) {
            possibleId = new Random().nextInt();
        }

        return possibleId;
    }

    public Schedule getScheduleById(int schedID) {
        if (!schedulesValid) {
            synchSchedules(mMostRecentContext);
        }
        Vector<Schedule> schedules = getSchedules();
        for (Schedule s : schedules) {
            if (s.getId() == schedID) return s;
        }
        return null;
    }

    /*
    TIMERS
     */

    private void synchTimers(Context context) {

        Vector<FocusTimer> timers = new Vector<>();
        PackageManager pm = context.getPackageManager();
        SharedPreferences sp = context.getSharedPreferences(timers_file_name, 0);

        Vector<Integer> timerIDs = new Vector<>();

        int timerIDSize = sp.getInt("timerIDs_size", 0);

        Log.d(TAG, "getSchedules: IDSize" + timerIDSize);

        for (int i = 0; i < timerIDSize; i++) {
            timerIDs.add(i, sp.getInt("timerIDs_" + i, 0));
        }


        for (int i = 0; i < timerIDSize; i++) {

            //Setup the id var.
            int id = timerIDs.get(i);

            //get the timer primitives
            String name = sp.getString(id + "_name", "");
            long currentDuration = sp.getLong(id + "_currentDuration", 10000);
            long initialDuration = sp.getLong(id + "_initialDuration", 10000);
            boolean isPaused = sp.getBoolean(id + "_paused", true);
            int period = sp.getInt(id + "_mPeriod", 1000);
            boolean isFinished = sp.getBoolean(id + "_finished", false);

            Vector<Profile> tProfiles = new Vector<>();

            int numProfiles = sp.getInt(id + "_numProfiles", 0);
            for (int j = 0; j < numProfiles; j++) {
                int profileID = sp.getInt(id + "_profile_" + j, 0);
                Profile p = getProfileByID(context, profileID);
                tProfiles.add(p);
            }

            FocusTimer ft = new FocusTimer(name, initialDuration, tProfiles, currentDuration, id);
            timers.add(ft);
        }
        timerList = timers;
        timersValid = true;
    }

    public Vector<FocusTimer> getTimers(Context context) {
        if (!timersValid) {
            synchTimers(context);
        }
        return timerList;
    }

    public Boolean addTimer(Context context, FocusTimer ft) {
        Vector<FocusTimer> timers = getTimers(context);
        timers.add(ft);
        setTimers(context, timers);
        return true;
    }

    public Boolean removeTimer(Context context, FocusTimer timer) {
        Vector<FocusTimer> timers = getTimers(context);
        if (timers.remove(timer)) {
            setTimers(context, timers);
            return true;
        }
        return false;
    }

    public void setTimers(Context context, Vector<FocusTimer> timers) {
        SharedPreferences sp = context.getSharedPreferences(timers_file_name, 0);
        Vector<Integer> timerIDs = new Vector<>();
        for (FocusTimer t : timers) {
            //store ids so we can write a list of ids
            int id = t.getId();
            timerIDs.add(id);
            //open editor
            SharedPreferences.Editor editor = sp.edit();
            //put primitives in sp associated with id:
            editor.putString(id + "_name", t.getName());
            editor.putLong(id + "_currentDuration", t.getCurrentDuration());
            editor.putLong(id + "_initialDuration", t.getInitialDuration());
            editor.putBoolean(id + "_paused", t.isPaused());
            editor.putBoolean(id + "_finished", t.isFinished());
            editor.putInt(id + "_mPeriod", t.mPeriod);

            //Now parse the list of profiles associated with a schedule
            //Which unfortunately means writing a list of profiles...

            //store the number of profiles
            Vector<Profile> tProfiles = t.getProfiles();
            int numProfiles = tProfiles.size();
            editor.putInt(id + "_numProfiles", numProfiles);
            for (int i = 0; i < numProfiles; i++) {
                Profile p = tProfiles.get(i);
                editor.putInt(id + "_profile_" + i, p.getId());
            }
            editor.commit();
        }

        sp.edit().putInt("timerIDs_size", timerIDs.size()).commit();
        for (int i = 0; i < timerIDs.size(); i++) {
            sp.edit().putInt("timerIDs_" + i, timerIDs.get(i)).commit();
        }

        invalidateTimers();
    }

    //TODO Optimize this entire fucking TERRIBLE class.
    public int getUniqueTimerID() {
        int possibleId = new Random().nextInt();
        if (!timersValid) {
            synchSchedules(mMostRecentContext);
        }

        Vector<Integer> existingIds = new Vector<>();

        for (FocusTimer t : timerList) {
            existingIds.add(t.getId());
        }

        while (existingIds.contains(possibleId)) {
            possibleId = new Random().nextInt();
        }

        return possibleId;
    }


    /*
    APPS
     */

    private void synchApps(Context context) {
        Log.d(TAG, "synchApps: Synching apps");

        Set<String> appNameSet =
                context.getSharedPreferences(apps_file_name, 0).getStringSet(apps_file_name, null);

        packageList.clear();

//        Log.d(TAG, "synchApps: Raw set has " + appNameSet.size() + " entries.");
        if(appNameSet != null) {
            for (String s : appNameSet) {
                try {
                    packageList.add(context.getPackageManager().getPackageInfo(s, 0));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        appsValid = true;
    }

    public Vector<PackageInfo> getAllApps(Context context) {
        if (!appsValid) {
            Log.d(TAG, "getAllApps: Apps invalid - begining synch");
            synchApps(context);
        }
        Log.d(TAG, "getAllApps: returning apps. numApps: " + packageList.size());
        return packageList;
    }

    public void setAllApps(Context context, Vector<PackageInfo> apps) {
        Set<String> appNames = new HashSet<>();
        for (PackageInfo pi : apps) {
            appNames.add(pi.packageName);
        }
        Log.d(TAG, "setAllApps numApps: " + appNames.size());
        context.getSharedPreferences(apps_file_name, 0).edit().putStringSet(apps_file_name, appNames).commit();
        invalidateApps();
    }

    public Vector<PackageInfo> getAllApps() {
        if (mMostRecentContext == null) {
            return null;
        } else {
            if (!appsValid) {
                synchApps(mMostRecentContext);
            }
            return packageList;
        }
    }

    /*
    NOTIFICATIONS
    */

    public Boolean addFocusNotification(Context context, FocusNotification notification) {
        Vector<FocusNotification> nots = getFocusNotifications(context);
        if (nots.add(notification)) {
            setFocusNotifications(context, nots);
            return true;
        } else {
            return false;
        }
    }

    public Boolean removeFocusNotification(Context context, FocusNotification notification) {
        Vector<FocusNotification> nots = getFocusNotifications(context);

        for(FocusNotification fn : nots){
            if(fn.getDescription().compareToIgnoreCase(notification.getDescription()) == 0){
                nots.remove(fn);
                return true;
            }
        }
        return false;
    }

    public Vector<FocusNotification> getFocusNotifications(Context context) {
        if (!notificationsValid) {
            Log.d(TAG, "getFocusNotifications: notes invalid - begining synch");
            synchApps(context);
        }
        Log.d(TAG, "getFocusNotifications: returning notes. numApps: " + notifications.size());
        return notifications;
    }

    public void setFocusNotifications(Context context, Vector<FocusNotification> notifications) {

        int numNotes = notifications.size();
        SharedPreferences sp = context.getSharedPreferences(notifications_file_name, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("numNotifications", numNotes);
        for(int i = 0; i < numNotes; i++){
            FocusNotification currentNotification = notifications.get(i);

            editor.putString(i+"_packageName", currentNotification.getPackageName());
            editor.putString(i + "_description", currentNotification.getDescription());
            editor.putString(i+"_name", currentNotification.getName());
        }

        editor.commit();

        invalidateFocusNotifications();
    }

    public void synchFocusNotifications(Context context) {

        SharedPreferences sp = context.getSharedPreferences(notifications_file_name, 0);

        notifications.clear();

        int numNotes = sp.getInt("numNotifications", 0);
        for(int i = 0; i < numNotes; i++){
            String notePackage = sp.getString(i+"_packageName", null);
            String noteDescription = sp.getString(i+"_description", null);
            String noteName = sp.getString(i+"_name", null);

            notifications.add(new FocusNotification(notePackage, noteName, noteDescription));
        }

        notificationsValid = true;
    }

    public boolean appIsBlocked(Context context, String packageName) {
        Vector<PackageInfo> activeApps = new Vector<>();

        Log.d("NBL", "looking for: " + packageName);

        Vector<Profile> profiles = getAllProfiles(context);
        Vector<Schedule> schedules = getSchedules(context);
        Vector<FocusTimer> timers = getTimers(context);

        //Do this first cause it should be relatively quick.
        for(FocusTimer t : timers){
            if(!t.isPaused()){
                for(String pName : t.getApps()){
                    if(pName.compareToIgnoreCase(packageName) == 0){
                        return true;
                    }
                }
            }
        }

        for(Profile p : profiles){
            if(p.isActive()){
                activeApps.addAll(p.getApps());
            }
        }

        for(Schedule s: schedules){
            if(s.isBlocking()){
                Log.d(TAG, "Blocking. Num Profiles:" + s.getProfiles().size());
                for(Profile p : s.getProfiles()){
                    Log.d(TAG, "Adding apps from profile: " + p.getName());
                    activeApps.addAll(p.getApps());
                }
            }
        }

        for(PackageInfo pi : activeApps){
            Log.d("NBL", "Found: " + pi.packageName);
            if(pi.packageName.compareToIgnoreCase(packageName) == 0){
                return true;
            }
        }

        return false;
    }

    public void clearNotifications(Context context) {
        notifications.clear();
        context.getSharedPreferences(notifications_file_name,0).edit().clear().commit();
    }

    public void setScheduleFlags(Context context, Vector<Boolean> flags){
        SharedPreferences sp = context.getSharedPreferences(schedules_file_name, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("numFlags", getSchedules(context).size());
        for(int i = 0; i < flags.size(); i++){
            editor.putBoolean(i+"_val", flags.get(i));
        }

        editor.commit();
    }

    public Vector<Boolean> getNotificationFlags(Context context){
        SharedPreferences sp = context.getSharedPreferences(schedules_file_name, 0);
        Vector<Boolean> flags = new Vector<>();
        int numFlags = sp.getInt("numFlags", 0);
        for(int i = 0; i < numFlags; i++){
            flags.add(sp.getBoolean(i+"_val", false));
        }
        return flags;
    }
}
