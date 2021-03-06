package com.proflow.focus_v2.services;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.helpers.NotificationUtils;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.app.Notification.EXTRA_TEXT;
import static android.content.ContentValues.TAG;

/**
 * Created by Patrick Truong on 10/16/2017.
 */

public class AppBlocker extends Service {

    private final static String TAG = "AppBlocker";


    private Looper mServiceLooper;

    public static boolean running = false;
    public static boolean blocked = false;
    private ServiceHandler mServiceHandler;
    private Handler mHandler;
    private ArrayList<String> mBlockedPackages;
    private Runnable mRunnableCode = new Runnable() {
        @Override
        public synchronized void run() {

            //checkScheduleNotificationFlags();
            //Added global method for checking if app is blocked.
            final String currentApp = getCurrentApp(getApplicationContext());
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
                                    if(packages.getValue(String.class).compareToIgnoreCase(currentApp) == 0){
                                        StartApplication(getApplicationContext(), getPackageName());
                                        blocked = true;
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
                                if(pName.compareToIgnoreCase(currentApp) == 0){
                                    StartApplication(getApplicationContext(), getPackageName());
                                    blocked = true;
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
                                    if(packages.getValue(String.class).compareToIgnoreCase(currentApp) == 0){
                                        StartApplication(getApplicationContext(), getPackageName());
                                        blocked = true;
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
                               // activeApps.addAll(p.getApps());
                            }
                            for(PackageInfo pi : activeApps){
                                Log.d("NBL", "Found: " + pi.packageName);
                                if(pi.packageName.compareToIgnoreCase(currentApp) == 0){
                                    StartApplication(getApplicationContext(), getPackageName());
                                    blocked = true;
                                }
                            }
                        }
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            mHandler.postDelayed(this, 300);
        }
    };

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }

    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mHandler = new Handler(mServiceLooper);

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    public ArrayList<String> ReturnBlockedApps(){
        return mBlockedPackages;
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        //If we have ORIGINAL_INENT
        if(intent != null) {
            if (intent.hasExtra("ORIGINAL_INTENT")) {

            }
        }

        running = true;

        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        //mBlockedPackages = intent.getStringArrayListExtra("mBlockedPackages");
        mHandler.post(mRunnableCode);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    public void killAppByPermission (Context context, String permissionToKill)
    {
        try
        {
            PackageManager p = context.getPackageManager();
            final List<PackageInfo> appinstall = p.getInstalledPackages(PackageManager.GET_PERMISSIONS);
            for(PackageInfo pInfo:appinstall)
            {
                String[] reqPermission = pInfo.requestedPermissions;
                if(reqPermission != null)
                {
                    for(int i=0;i<reqPermission.length;i++)
                    {
                        if (((String)reqPermission[i]).toLowerCase().contains(permissionToKill.toLowerCase()))
                        {
                            killAppByPackName(context, pInfo.packageName.toString());
                            break;
                        }
                    }
                }
            }
        }
        catch (Throwable t){t.printStackTrace();}
    }

    public static void killAppByPackName (Context context, String packageToKill)
    {
        try
        {
            ActivityManager actvityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfos)
            {
                //Log.e("running",runningAppProcessInfo.processName);
                if(runningAppProcessInfo.processName.toLowerCase().contains(packageToKill.toLowerCase()))
                {
                    android.os.Process.sendSignal(runningAppProcessInfo.pid, android.os.Process.SIGNAL_KILL);
                    actvityManager.killBackgroundProcesses(packageToKill);
                    //Log.e("killed", "!!! killed "+ packageToKill);
                }
            }
        }
        catch (Throwable t){}
    }


    public static void killAppByName (Context context, String appNameToKill)
    {
        try
        {
            ActivityManager  manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : listOfProcesses)
            {
                if (process.processName.toLowerCase().contains(appNameToKill.toLowerCase()))
                {
                    //Log.e("killed", process.processName);
                    // manager.killBackgroundProcesses(process.processName);
                    break;
                }
            }
        }
        catch (Throwable t){t.printStackTrace();}
    }

    public static boolean needPermissionForBlocking(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return  (mode != MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    public String getCurrentApp(Context context) {
        List<String> mPackageNames = new ArrayList<String>();
        String topPackageName = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
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
                //Toast.makeText(this, topPackageName, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "second one", Toast.LENGTH_SHORT).show();
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            /*for(int i = 0; i < taskInfo.size(); i++){
                ComponentName componentInfo = taskInfo.get(i).topActivity;
                mPackageNames.add(componentInfo.getPackageName());
            }*/
        }

        return topPackageName;
    }



    public boolean StartApplication(Context context, String packageName){
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    private void checkScheduleNotificationFlags(){
        Vector<Boolean> newBlockingProfiles = new Vector<>();
        Vector<Boolean> oldBlockingProfiles = Global.getInstance().getNotificationFlags(getApplicationContext());
        Vector<Schedule> schedules = Global.getInstance().getSchedules();
        for(int i=0; i<schedules.size(); i++){
            newBlockingProfiles.add(schedules.get(i).isBlocking());
        }


        if(oldBlockingProfiles.size() > 0 && newBlockingProfiles.size() > 0) {
            for (int i = 0; i < oldBlockingProfiles.size(); i++) {
                if (newBlockingProfiles.size() > i ){
                    if (oldBlockingProfiles.get(i) == false && newBlockingProfiles.get(i) == true) {
                        sendScheduleStartNotif(i);
                    } else if (oldBlockingProfiles.get(i) == true && newBlockingProfiles.get(i) == false) {
                        sendScheduleEndNotif(i);
                    }
                }
            }
        }
        Global.getInstance().setScheduleFlags(getApplicationContext(), newBlockingProfiles);
    }

    private void sendScheduleStartNotif(int i){
            NotificationUtils mNotificationUtils = new NotificationUtils(getApplicationContext());
    Notification.Builder nb = mNotificationUtils.
            getNotification("Schedule Started Blocking", "The schedule "+Global.getInstance().getSchedules().get(i).getName()+ " is currently blocking apps.");
                        mNotificationUtils.notify(101, nb);
    }

    private void sendScheduleEndNotif(int i){
    NotificationUtils mNotificationUtils = new NotificationUtils(getApplicationContext());
    Notification.Builder nb = mNotificationUtils.
            getNotification("Schedule Ended Blocking", "The schedule "+Global.getInstance().getSchedules().get(i).getName()+ " is no longer blocking apps.");
                        mNotificationUtils.notify(101, nb);
    }

    private android.app.AlertDialog BlockedApplicationAlert(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Blocked Application");
        alertDialogBuilder.setMessage("Focus! You are trying to access a distracting application that has been blocked! ");
        alertDialogBuilder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return(alertDialogBuilder.create());
    }
}