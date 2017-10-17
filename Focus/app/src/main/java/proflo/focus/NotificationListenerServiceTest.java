package proflo.focus;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by alexinaboudreaux on 10/15/17.
 */

public class NotificationListenerServiceTest extends NotificationListenerService{

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        //int notificationCode = matchNotificationCode(sbn);
        cancelNotification(sbn.getKey());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
    }

//    private int matchNotificationCode(StatusBarNotification sbn) {
//        String packageName = sbn.getPackageName();
//
//        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
//                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
//            return(InterceptedNotificationCode.FACEBOOK_CODE);
//        }
//        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
//            return(InterceptedNotificationCode.INSTAGRAM_CODE);
//        }
//        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
//            return(InterceptedNotificationCode.WHATSAPP_CODE);
//        }
//        else{
//            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
//        }
//    }
}

