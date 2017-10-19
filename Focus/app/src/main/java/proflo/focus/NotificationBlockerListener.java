package proflo.focus;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Vector;

public class NotificationBlockerListener extends NotificationListenerService{

    Vector<ApplicationInfo> appInfo;

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
        }
    }

    private boolean appIsBlocked(String packageName){
        Vector<ApplicationInfo> appInfo = Global.getInstance().getActiveApps(this);
        for(int i = 0; i < appInfo.size(); i++)
        {
            if(appInfo.get(i).packageName == packageName)
            {
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
