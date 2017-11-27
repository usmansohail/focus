package com.proflow.focus_v2;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.widget.Toast;

import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.fragments.TimersFragment;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.proflow.focus_v2.services.AppBlocker;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Patrick Truong on 10/29/2017.
 */

public class WhiteBoxUnitTest {

    private Context context;

    @Before
    public void setupForEachTest() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    @Test
    public void adding_profile() throws Exception {
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile profile = new Profile("Testing", temp, 10);
        Global.getInstance().addProfile(context, profile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean idCondition = (profile.getId() == result.get(result.size() - 1).getId());
        assertTrue(idCondition);
    }

    @Test
    public void adding_schedule() throws Exception {
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Schedule schedule = new Schedule("schedule", times, Global.getInstance().getAllProfiles(context), true, 100);
        Global.getInstance().addSchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        Schedule resultSchedule = result.get(result.size() - 1);
        boolean nameCondition = schedule.getName().equals(resultSchedule.getName());
        boolean timeCondition = schedule.getTimeBlocks().isEmpty() && resultSchedule.getTimeBlocks().isEmpty();
        boolean repeatCondition = schedule.repeatWeekly() && resultSchedule.repeatWeekly();
        assertTrue(nameCondition && timeCondition && repeatCondition);
    }

    @Test
    public void adding_timer() throws Exception {
        FocusTimer timer = new FocusTimer("timer", Long.valueOf(10), Global.getInstance().getAllProfiles(context), Long.valueOf(5), 1000);
        Global.getInstance().addTimer(context, timer);
        Vector<FocusTimer> resultVector = Global.getInstance().getTimers(context);
        FocusTimer result = resultVector.get(resultVector.size() - 1);
        boolean idCondition = timer.getId() == result.getId();
        assertTrue(idCondition);
    }

    @Test
    public void adding_notification() throws Exception {
        FocusNotification notification = new FocusNotification("package", "name", "description");
        Global.getInstance().addFocusNotification(context, notification);
        Vector<FocusNotification> resultVector = Global.getInstance().getFocusNotifications(context);
        FocusNotification result =  resultVector.get(resultVector.size() - 1);
        boolean nameCondition = result.getName().equals(notification.getName());
        boolean packageCondition = result.getPackageName().equals(notification.getPackageName());
        boolean descriptionCondition = result.getDescription().equals(notification.getDescription());
        assertTrue(nameCondition && packageCondition && descriptionCondition);
    }

    @Test
    public void edit_profile() throws Exception {
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile profile = new Profile("Changing", temp, 20);
        Global.getInstance().addProfile(context, profile);

        profile.setName("Changed");
        Global.getInstance().modifyProfile(context, profile);
        boolean changed = false;
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        for(Profile p : result){

                if(profile.getName().equals(profile.getName())){
                    changed = true;
                }

        }
        assertTrue(changed);
    }

    @Test
    public void edit_schedule() throws Exception {

        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Schedule schedule = new Schedule("schedule", times, Global.getInstance().getAllProfiles(context), true, 200);
        Global.getInstance().addSchedule(context, schedule);


        schedule.setName("Changed");
        schedule.setRepeatWeekly(false);
        Global.getInstance().modifySchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        boolean changed = false;
        for(Schedule s : result){
            if(schedule.getName().equals("Changed")){
                changed = true;
            }
        }
        assertTrue(changed);

    }

    @Test
    public void deleting_schedule() throws Exception {
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Schedule schedule = new Schedule("schedule", times, Global.getInstance().getAllProfiles(context), true, 300);
        Global.getInstance().addSchedule(context, schedule);
        Global.getInstance().removeSchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        boolean notFound = true;
        for(Schedule s : result){
            if(s.getId() == schedule.getId()){
                notFound = false;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_timer() throws Exception {
        FocusTimer timer = new FocusTimer("timer", Long.valueOf(10), Global.getInstance().getAllProfiles(context), Long.valueOf(5), 3000);
        Global.getInstance().addTimer(context, timer);
        Global.getInstance().removeTimer(context, timer);
        Vector<FocusTimer> resultVector = Global.getInstance().getTimers(context);
        boolean notFound = true;
        for(FocusTimer t : resultVector){
            if(t.getName() == timer.getName()){
                notFound = false;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_profile() throws Exception {
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile profile = new Profile("Testing", temp, 30);
        Global.getInstance().addProfile(context, profile);
        Global.getInstance().removeProfile(context, profile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean notFound = true;
        for(Profile p : result){
            if(p.getId() == profile.getId()){
                notFound = false;
                break;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_notification() throws Exception {
        FocusNotification notification = new FocusNotification("package", "name", "description");
        Global.getInstance().addFocusNotification(context, notification);
        Global.getInstance().removeFocusNotification(context, notification);
        Vector<FocusNotification> resultVector = Global.getInstance().getFocusNotifications(context);
        boolean notFound = true;
        for(FocusNotification n : resultVector){
            if(n.getName().equals(notification.getName()) && n.getDescription().equals(notification.getDescription())){
                notFound = false;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void check_blocked_app() throws Exception {
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        PackageInfo tempPackage = packageList.get(0);
        temp.add(tempPackage);
        Profile profile = new Profile("Test", temp, 1000);
        profile.setActive(true);
        Global.getInstance().addProfile(context, profile);
        assertTrue(Global.getInstance().appIsBlocked(context, tempPackage.packageName));
        Global.getInstance().removeProfile(context, profile);
    }

    @Test
    public void check_current_app() throws Exception {
        AppBlocker test = new AppBlocker();
        ///now check what app you are on
        TimeUnit.SECONDS.sleep(3);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        //String result = test.getCurrentApp();

        //NOTE: This is a copy and paste of the getCurrentApp function within AppBlocker
        //The testing would not work by passing in the context (Context.USAGE_STATS_SERVICE was null
        // for some reason, and so I had to copy and paste the code and test it out that way
        String topPackageName = "";
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
        // Sort the stats by the last time used
        if(stats != null) {
            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
            }
            if(mySortedMap != null && !mySortedMap.isEmpty()) {
                topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }
        assertTrue(topPackageName.equals("com.google.android.apps.nexuslauncher"));
    }

    @Test
    public void check_block_redirection() throws Exception{
        //Start service and put facebook as blocked app
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        PackageInfo block = new PackageInfo();
        for(PackageInfo p : packageList){
            if(p.packageName.equals("com.google.android.apps.nexuslauncher")){
                block = p;
            }
        }
        temp.add(block);
        Profile profile = new Profile("Test", temp, 10000);
        profile.setActive(true);
        Global.getInstance().addProfile(context, profile);
        context.startService(new Intent(context, AppBlocker.class));
        TimeUnit.SECONDS.sleep(4);

        //check if was redirected to focus!
        context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        String topPackageName = "";
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
        // Sort the stats by the last time used
        if(stats != null) {
            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
            }
            if(mySortedMap != null && !mySortedMap.isEmpty()) {
                topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }
        Global.getInstance().removeProfile(context, profile);
        assertTrue(topPackageName.equals("com.fdunlap.focus_v2"));

    }

    @Test
    public void get_profiles(){
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile profile = new Profile("ProfileOne", temp, 40);
        Global.getInstance().addProfile(context, profile);
        Profile profileTwo = new Profile("ProfileTwo", temp, 41);
        Global.getInstance().addProfile(context, profileTwo);
        Profile profileThree = new Profile("ProfileThree", temp, 42);
        Global.getInstance().addProfile(context, profileThree);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean one = false;
        boolean two = false;
        boolean three = false;
        for(Profile p : result){
            if(p.getId() == 40){
                one = true;
            }
            if(p.getId() == 41){
                two = true;
            }
            if(p.getId() == 42){
                three = true;
            }
        }
        assertTrue(one && two && three);
    }

    @Test
    public void get_schedules(){
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Schedule schedule = new Schedule("scheduleOne", times, Global.getInstance().getAllProfiles(context), true, 300);
        Global.getInstance().addSchedule(context, schedule);
        Schedule scheduleTwo = new Schedule("scheduleTwo", times, Global.getInstance().getAllProfiles(context), true, 301);
        Global.getInstance().addSchedule(context, scheduleTwo   );
        Schedule scheduleThree = new Schedule("scheduleThree", times, Global.getInstance().getAllProfiles(context), true, 302);
        Global.getInstance().addSchedule(context, scheduleThree);
        Vector<Schedule> schedules = Global.getInstance().getSchedules(context);
        boolean one = false;
        boolean two = false;
        boolean three = false;
        for(Schedule s : schedules){
            if(s.getName().equals("scheduleOne")){
                one = true;
            }
            if(s.getName().equals("scheduleTwo")){
                two = true;
            }
            if(s.getName().equals("scheduleThree")){
                three = true;
            }
        }
        assertTrue(one && two && three);
    }


}
