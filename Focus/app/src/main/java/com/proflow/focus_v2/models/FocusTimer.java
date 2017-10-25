package com.proflow.focus_v2.models;

import android.content.pm.PackageInfo;

import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 *
 * I've got to think of how the damn timer is actually supposed to be used... I know we've got an
 * actual 'timer' in the app, but we also need to use some semblance of an actual java.util.timer
 * for actively running schedules... Hmmm...
 *
 * For now this is the model of an actual timer. IE the thing we're displaying. It's start and stop
 * methods effectively just tell the 'running thread' (However we implement that) that
 */


public class FocusTimer {

    private String mName;

    private Long mInitialDuration;
    private Long mCurrentDuration;
    private Long startTime;

    private boolean isActive;
    private Vector<PackageInfo> bucket;

    public FocusTimer(String name, Long initialDuration, Vector<Profile> timerProfiles){
        mName = name;
        mInitialDuration = initialDuration;
        mCurrentDuration = initialDuration;

        isActive = false;
        bucket = new Vector<>();
    }
}