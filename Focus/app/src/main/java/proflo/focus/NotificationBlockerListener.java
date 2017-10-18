package proflo.focus;

import android.app.*;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Vector;

public class NotificationBlockerListener extends NotificationListenerService{

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(appIsBlocked()) {
            cancelNotification(sbn.getKey());
            Vector<Profile> profiles = getNotificationProfiles();
            Notification notification = new Notification(sbn.getNotification(), profiles);
            //will then add notification to vector of what a user missed during blocked profile
        }
    }

    private boolean appIsBlocked(){
        //will check if app is currently blocked once shared preferences vector exists
        return true;
    }

    private Vector<Profile> getNotificationProfiles(){
        //will return a vector of active profiles this app is in
        return null;
    }
}

