package com.proflow.focus_v2;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContext;
import android.util.Log;

import com.proflow.focus_v2.data.Global;
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
        Profile newProfile = new Profile("Test", temp, 0);
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
        Schedule schedule = new Schedule("Test", times, profiles, true, 0);
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
        FocusTimer timer = new FocusTimer("Test", Long.valueOf(10), profiles, Long.valueOf(5), 0);
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
        
    }

}
