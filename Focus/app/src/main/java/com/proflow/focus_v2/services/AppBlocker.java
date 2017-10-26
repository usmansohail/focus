package com.proflow.focus_v2.services;

import android.app.ActivityManager;
import android.app.AppOpsManager;
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

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
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

/**
 * Created by Patrick Truong on 10/16/2017.
 */

public class AppBlocker extends Service {

    private final String TAG = "APPBLOCKER";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Handler mHandler;

    public static boolean running = false;

    private Runnable mRunnableCode = new Runnable() {
        @Override
        public synchronized void run() {
            running = true;
            Vector<String> blockedPackageNames = getBlockedPackageNames();

            ActivityManager mActivityManager =(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mPackageName = getPackageName();
            Log.d(TAG, "CURRENT APP NAME: " + mPackageName);

//            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
//            Log.d(TAG, "CURRENT APP PROCESS NAME: " + mPackageName);
//            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
//            Log.d(TAG, "CURRENT APP DEPRECATED NAME: " + mPackageName);


            for(String blocked : blockedPackageNames){
                if(mPackageName.compareToIgnoreCase(blocked) == 0){
                    StartApplication(getApplicationContext(), getPackageName());
                    BlockedApplicationAlert().show();
                    break;
                }
            }
            mHandler.postDelayed(this, 300);
        }
    };

    //Yes, I know this is gross - FDunlap
    private Vector<String> getBlockedPackageNames() {
        Vector<String> blockedPackages = new Vector<>();

        Vector<Profile> profiles = Global.getInstance().getAllProfiles(getApplicationContext());
        Vector<Schedule> schedules = Global.getInstance().getSchedules(getApplicationContext());
        Vector<FocusTimer> timers = Global.getInstance().getTimers(getApplicationContext());

        for(Profile p : profiles){
            if(p.isActive()){
                for(PackageInfo pi : p.getApps()){
                    blockedPackages.add(pi.packageName);
                }
            }
        }
        for(Schedule s : schedules){
            if(s.isBlocking()){
                for(Profile p : s.getProfiles()){
                    for(PackageInfo pi : p.getApps()){
                        blockedPackages.add(pi.packageName);
                    }
                }
            }
        }
        for(FocusTimer timer : timers){
            if(!timer.isPaused()){
                blockedPackages.addAll(timer.getApps());
            }
        }
        return blockedPackages;
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

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        //If we have ORIGINAL_INENT
        if(intent != null) {
            if (intent.hasExtra("ORIGINAL_INTENT")) {

            }
        }

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
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
        super.onDestroy();
        running = false;
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, topPackageName, Toast.LENGTH_SHORT).show();
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

}
