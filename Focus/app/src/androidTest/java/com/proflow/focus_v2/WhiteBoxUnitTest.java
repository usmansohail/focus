package com.proflow.focus_v2;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContext;
import android.util.Log;

import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

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

        context = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void adding_profile() throws Exception {
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile newProfile = new Profile("Test", temp, 1000);
        Global.getInstance().addProfile(context, newProfile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean packageCondition = newProfile.getApps().isEmpty() && result.get(result.size() - 1).getApps().isEmpty();
        boolean nameCondition = newProfile.getName().equals(result.get(result.size() - 1).getName());
        boolean idCondition = (newProfile.getId() == result.get(result.size() - 1).getId());
        assertTrue(packageCondition && nameCondition && idCondition);
    }

    @Test
    public void adding_schedule() throws Exception {
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Vector<Profile> profiles = new Vector<Profile>();
        Schedule schedule = new Schedule("Test", times, profiles, true, 1000);
        Global.getInstance().addSchedule(context, schedule);
        Vector<Schedule> result = Global.getInstance().getSchedules(context);
        Schedule resultSchedule = result.get(result.size() - 1);
        boolean nameCondition = schedule.getName().equals(resultSchedule.getName());
        boolean timeCondition = schedule.getTimeBlocks().isEmpty() && resultSchedule.getTimeBlocks().isEmpty();
        boolean profilesCondition = schedule.getProfiles().isEmpty() && resultSchedule.getProfiles().isEmpty();
        boolean repeatCondition = schedule.repeatWeekly() && resultSchedule.repeatWeekly();
        boolean idCondition = (schedule.getId() == resultSchedule.getId());
        assertTrue(nameCondition && timeCondition && profilesCondition && repeatCondition && idCondition);
    }

    @Test
    public void adding_timer() throws Exception {
        Vector<Profile> profiles = new Vector<Profile>();
        FocusTimer timer = new FocusTimer("Test", Long.valueOf(10), profiles, Long.valueOf(5), 1000);
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
    public void deleting_profile() throws Exception {
        //first create profile
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile newProfile = new Profile("Test", temp, 1000);
        Global.getInstance().addProfile(context, newProfile);

        //Now delete profile
        Global.getInstance().removeProfile(context, newProfile);
        Vector<Profile> result = Global.getInstance().getAllProfiles(context);
        boolean notFound = true;
        for(Profile p : result){
            if(p.getId() == newProfile.getId()){
                notFound = false;
                break;
            }
        }
        assertTrue(notFound);
    }

    @Test
    public void deleting_schedule() throws Exception {
        //add schedule first
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Vector<Profile> profiles = new Vector<Profile>();
        Schedule schedule = new Schedule("Test", times, profiles, true, 1000);
        Global.getInstance().addSchedule(context, schedule);

        //delete schedule and check if not found
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
        //Create timer
        Vector<Profile> profiles = new Vector<Profile>();
        FocusTimer timer = new FocusTimer("Test", Long.valueOf(10), profiles, Long.valueOf(5), 1000);
        Global.getInstance().addTimer(context, timer);
        //Delete timer
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
        //Add notification
        FocusNotification notification = new FocusNotification("package", "name", "description");
        Global.getInstance().addFocusNotification(context, notification);

        //Delete Notification
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
    public void edit_profile() throws Exception {
        //first create profile
        Vector<PackageInfo> temp = new Vector<PackageInfo>();
        Profile newProfile = new Profile("Test", temp, 1000);
        Global.getInstance().addProfile(context, newProfile);

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
        //add schedule first
        Vector<TimeBlock> times = new Vector<TimeBlock>();
        Vector<Profile> profiles = new Vector<Profile>();
        Schedule schedule = new Schedule("Test", times, profiles, true, 1000);
        Global.getInstance().addSchedule(context, schedule);

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
    

}
