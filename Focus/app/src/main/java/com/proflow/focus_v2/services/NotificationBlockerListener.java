package com.proflow.focus_v2.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.Profile;

import java.util.Vector;

import static android.app.Notification.EXTRA_TEXT;

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

        String packageName = sbn.getPackageName();

        PackageManager pm = getPackageManager();

        if(Global.getInstance().appIsBlocked(getApplicationContext(), packageName)) {
            cancelNotification(sbn.getKey());
            Vector<Profile> profiles = getNotificationProfiles();
            FocusNotification fn = null;
            try {
                fn = new FocusNotification(packageName,
                        pm.getPackageInfo(sbn.getPackageName(),0).applicationInfo.loadLabel(pm).toString() ,
                        sbn.getNotification().extras.getString(EXTRA_TEXT));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //will then add notification to vector of what a user missed during blocked profile
            Global.getInstance().addFocusNotification(getApplicationContext(), fn);
        }
    }



    private Vector<Profile> getNotificationProfiles(){
        //TODO Add notification profile functionality -- currently just shows the notification itself
        //will return a vector of active profiles this app is in
        return null;
    }

    public void update(Vector<ApplicationInfo> ActiveAppsVector){

    }
}