package com.proflow.focus_v2.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.Notification;

import java.util.Vector;

public class NotificationBlockerListener extends NotificationListenerService{

    Vector<ApplicationInfo> appInfo;
    Context mContext;

    public NotificationBlockerListener(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(appInfo == null){
            appInfo = new Vector<>();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(appIsBlocked(sbn.getPackageName())) {
            cancelNotification(sbn.getKey());
            Vector<Profile> profiles = getNotificationProfiles();
            Notification notification = new Notification(sbn.getNotification(), profiles);
            //will then add notification to vector of what a user missed during blocked profile
            Global.getInstance().addNotification(getApplicationContext(), notification);
        }
    }

    private boolean appIsBlocked(String packageName) {
        Vector<PackageInfo> activeApps = new Vector<>();

        Log.d("NBL", "looking for: " + packageName);

        Vector<Profile> profiles = Global.getInstance().getAllProfiles(getApplicationContext());
        Vector<Schedule> schedules = Global.getInstance().getSchedules(getApplicationContext());

        for(Profile p : profiles){
            if(p.isActive()){
                activeApps.addAll(p.getApps());
            }
        }

        for(Schedule s: schedules){
            if(s.isBlocking()){
                for(Profile p : s.getProfiles()){
                    activeApps.addAll(p.getApps());
                }
            }
        }

        //todo timers

        for(PackageInfo pi : activeApps){
            Log.d("NBL", "Found: " + pi.packageName);
            if(pi.packageName.compareToIgnoreCase(packageName) == 0){
                return true;
            }
        }

        return false;
    }

    private Vector<Profile> getNotificationProfiles(){
        //will return a vector of active profiles this app is in
        return null;
    }

    public void update(Vector<ApplicationInfo> ActiveAppsVector){

    }
}