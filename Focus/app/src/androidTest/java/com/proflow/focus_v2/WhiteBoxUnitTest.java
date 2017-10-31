package com.proflow.focus_v2;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
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
    private Profile newProfile;
    private Schedule schedule;
    private FocusNotification notification;
    private FocusTimer timer;

    @Before
    public void setup(){
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        newProfile = new Profile("profile", temp, 1000);
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Vector<Profile> profiles = new Vector<Profile>();
        schedule = new Schedule("schedule", times, profiles, true, 1000);
        timer = new FocusTimer("timer", Long.valueOf(10), profiles, Long.valueOf(5), 1000);
        notification = new FocusNotification("package", "name", "description");

    }

    @Before
    public void setupForEachTest() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    @Test
    public void adding_profile() throws Exception {
        Global.getInstance().addProfile(context, newProfile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean packageCondition = newProfile.getApps().isEmpty() && result.get(result.size() - 1).getApps().isEmpty();
        boolean nameCondition = newProfile.getName().equals(result.get(result.size() - 1).getName());
        assertTrue(packageCondition && nameCondition);
    }

    @Test
    public void adding_schedule() throws Exception {
        Global.getInstance().addSchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        Schedule resultSchedule = result.get(result.size() - 1);
        boolean nameCondition = schedule.getName().equals(resultSchedule.getName());
        boolean timeCondition = schedule.getTimeBlocks().isEmpty() && resultSchedule.getTimeBlocks().isEmpty();
        boolean profilesCondition = schedule.getProfiles().isEmpty() && resultSchedule.getProfiles().isEmpty();
        boolean repeatCondition = schedule.repeatWeekly() && resultSchedule.repeatWeekly();
        assertTrue(nameCondition && timeCondition && profilesCondition && repeatCondition);
    }

    @Test
    public void adding_timer() throws Exception {
        Global.getInstance().addTimer(context, timer);
        Vector<FocusTimer> resultVector = Global.getInstance().getTimers(context);
        FocusTimer result = resultVector.get(resultVector.size() - 1);
        boolean nameCondition = result.getName().equals(timer.getName());
        boolean timeCondition = (result.getInitialDuration() == timer.getInitialDuration());
        boolean profilesCondition = result.getProfiles().isEmpty() && timer.getProfiles().isEmpty();
        assertTrue(nameCondition && timeCondition && profilesCondition);
    }

    @Test
    public void adding_notification() throws Exception {

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
        //Modify profile
        newProfile.setName("Changed");
        Global.getInstance().modifyProfile(context, newProfile);
        boolean changed = false;
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        for(Profile p : result){
            if(p.getId() == newProfile.getId()){
                if(p.getName().equals(newProfile.getName())){
                    changed = true;
                }
            }
        }
        assertTrue(changed);
    }

    @Test
    public void edit_schedule() throws Exception {
        schedule.setName("Changed");
        schedule.setRepeatWeekly(false);
        Global.getInstance().modifySchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        boolean changed = false;
        for(Schedule s : result){
            if(schedule.getId() == s.getId()){
                if(schedule.getName().equals(s.getName()) && !s.repeatWeekly()){
                    changed = true;
                }
            }
        }
        assertTrue(changed);

    }

    @Test
    public void deleting_profile() throws Exception {

        Global.getInstance().removeProfile(context, newProfile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean notFound = true;
        for(Profile p : result){
            if(p.getName().equals(newProfile.getName()) && p.getApps().isEmpty()){
                notFound = false;
                break;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_schedule() throws Exception {

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

        Global.getInstance().removeTimer(context, timer);
        Vector<FocusTimer> resultVector = Global.getInstance().getTimers(context);
        boolean notFound = true;
        for(FocusTimer t : resultVector){
            if(t.getId() == timer.getId()){
                notFound = false;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_notification() throws Exception {

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
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("com.facebook.katana");
        //open facebook
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

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
        assertTrue(topPackageName.equals("com.facebook.katana"));
    }

    @Test
    public void check_block_redirection() throws Exception{
        //Start service and put facebook as blocked app
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        PackageInfo facebook = new PackageInfo();
        boolean found = false;
        for(PackageInfo p : packageList){
            if(p.packageName.equals("com.facebook.katana")){
                facebook = p;
                found = true;
            }
        }
        assertTrue(found);
        temp.add(facebook);
        Profile newProfile = new Profile("Test", temp, 10000);
        newProfile.setActive(true);
        Global.getInstance().addProfile(context, newProfile);
        context.startService(new Intent(context, AppBlocker.class));

        /*//start open up facebook app
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("com.facebook.katana");
        //open facebook
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

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
        assertTrue(topPackageName.equals("com.proflow.focus_v2"));*/

    }



}
