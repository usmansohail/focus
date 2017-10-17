package proflo.focus;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.widget.Toast;
import android.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Patrick Truong on 10/16/2017.
 */

public class AppBlocker extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

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

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        List<String> test = getRecentApps(getApplicationContext());
        /*final ArrayList<String> mBlockedPackages = intent.getStringArrayListExtra("mBlockedPackages");

        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                List<String> mPackageNames = getRecentApps(getApplicationContext());
                for(String blocked : mBlockedPackages){
                    for(String packageName : mPackageNames){
                        if(blocked.equals(packageName)){
                            killAppByPackName(getApplicationContext(), blocked);
                            break;
                        }
                    }
                }
            }
        };

        timer.schedule(myTask, 500, 500);*/

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
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public static void killAppByPermission (Context context, String permissionToKill)
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
                    manager.killBackgroundProcesses(process.processName);
                    break;
                }
            }
        }
        catch (Throwable t){t.printStackTrace();}
    }

    public List<String> getRecentApps(Context context) {
        List<String> mPackageNames = new ArrayList<String>();
        String topPackageName = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();

            UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 30, System.currentTimeMillis() + (10 * 1000));
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                mPackageNames.add(event.getPackageName());
                usageEvents.getNextEvent(event);
            }

        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            for(int i = 0; i < taskInfo.size(); i++){
                ComponentName componentInfo = taskInfo.get(i).topActivity;
                mPackageNames.add(componentInfo.getPackageName());
            }
        }

        for(String name : mPackageNames){
            String temp = name;
        }


        return mPackageNames;
    }

}
