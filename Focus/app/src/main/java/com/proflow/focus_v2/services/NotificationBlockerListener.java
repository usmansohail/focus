package com.proflow.focus_v2.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;

import java.util.Vector;

import static android.app.Notification.EXTRA_TEXT;
import static android.content.ContentValues.TAG;

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
    public void onNotificationPosted(final StatusBarNotification sbn){

        final String packageName = sbn.getPackageName();

        final PackageManager pm = getPackageManager();

        Log.d("NBL", "looking for: " + packageName);

        final Vector<FocusTimer> timers = new Vector<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Timers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    for(DataSnapshot profiles : id.child("profiles").getChildren()){
                        for(DataSnapshot packages : profiles.child("mPackageNames").getChildren()){
                            boolean paused = id.child("paused").getValue(boolean.class);
                            if(!paused){
                                if(packages.getValue(String.class).compareToIgnoreCase(packageName) == 0){
                                    cancelNotification(sbn.getKey());
                                    FocusNotification fn = null;
                                    try {
                                        fn = new FocusNotification(packageName,
                                                pm.getPackageInfo(sbn.getPackageName(),0).applicationInfo.loadLabel(pm).toString() ,
                                                sbn.getNotification().extras.getString(EXTRA_TEXT));
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child(Global.getInstance().getUsername()).child("Notification").child(String.valueOf(fn.getId())).setValue(fn);
                                        Global.getInstance().addFocusNotification(getApplicationContext(), fn);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    /*FocusTimer timer = new FocusTimer(id);
                    timers.add(timer);*/
                }
                //Do this first cause it should be relatively quick.
                /*for(FocusTimer t : timers){
                    if(!t.isPaused()){
                        for(String pName : t.getApps()){
                            if(pName.compareToIgnoreCase(packageName) == 0){
                                cancelNotification(sbn.getKey());
                                FocusNotification fn = null;
                                try {
                                    fn = new FocusNotification(packageName,
                                            pm.getPackageInfo(sbn.getPackageName(),0).applicationInfo.loadLabel(pm).toString() ,
                                            sbn.getNotification().extras.getString(EXTRA_TEXT));
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                //will then add notification to vector of what a user missed during blocked profile
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child(Global.getInstance().getUsername()).child("Notification").child(String.valueOf(fn.getId())).setValue(fn);
                                Global.getInstance().addFocusNotification(getApplicationContext(), fn);
                            }
                        }
                    }
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final Vector<Schedule> schedules = new Vector<>();
        final Vector<PackageInfo> activeApps = new Vector<>();
        ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Schedules");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    for(DataSnapshot profiles : id.child("profiles").getChildren()){
                        for(DataSnapshot packages : profiles.child("mPackageNames").getChildren()){
                            boolean blocking = id.child("blocking").getValue(boolean.class);
                            if(blocking){
                                if(packages.getValue(String.class).compareToIgnoreCase(packageName) == 0){
                                    cancelNotification(sbn.getKey());
                                    FocusNotification fn = null;
                                    try {
                                        fn = new FocusNotification(packageName,
                                                pm.getPackageInfo(sbn.getPackageName(),0).applicationInfo.loadLabel(pm).toString() ,
                                                sbn.getNotification().extras.getString(EXTRA_TEXT));
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child(Global.getInstance().getUsername()).child("Notification").child(String.valueOf(fn.getId())).setValue(fn);
                                        //Global.getInstance().addFocusNotification(getApplicationContext(), fn);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    /*Schedule schedule = new Schedule(id);
                    schedules.add(schedule);*/
                }
                //Do this first cause it should be relatively quick.
                /*for (Schedule s : schedules) {
                    if (s.isBlocking()) {
                        Log.d(TAG, "Blocking. Num Profiles:" + s.getProfiles().size());
                        for (Profile p : s.getProfiles()) {
                            Log.d(TAG, "Adding apps from profile: " + p.getName());
                            //activeApps.addAll(p.getApps());
                        }
                        for(PackageInfo pi : activeApps){
                            Log.d("NBL", "Found: " + pi.packageName);
                            if(pi.packageName.compareToIgnoreCase(packageName) == 0){
                                cancelNotification(sbn.getKey());
                                FocusNotification fn = null;
                                try {
                                    fn = new FocusNotification(packageName,
                                            pm.getPackageInfo(sbn.getPackageName(), 0).applicationInfo.loadLabel(pm).toString(),
                                            sbn.getNotification().extras.getString(EXTRA_TEXT));
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                //will then add notification to vector of what a user missed during blocked profile
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child(Global.getInstance().getUsername()).child("Notification").child(String.valueOf(fn.getId())).setValue(fn);
                                Global.getInstance().addFocusNotification(getApplicationContext(), fn);
                            }
                        }
                    }
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}