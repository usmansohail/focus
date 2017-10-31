package com.proflow.focus_v2.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.fragments.NotificationsFragment;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";
    Context context;

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationUtils(Context ctx) {
        super(ctx);

        this.context = ctx;
        NotificationChannel mainChannel = new NotificationChannel(PRIMARY_CHANNEL,
                getString(android.R.string.ok), NotificationManager.IMPORTANCE_HIGH);
        mainChannel.setLightColor(Color.GREEN);
        mainChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(mainChannel);
    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public Notification.Builder getNotification(String title, String body) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_focus_notification)
                .setAutoCancel(true)
                .setContentIntent(intent);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notificationBuilder The notification object
     */
    public void notify(int id, Notification.Builder notificationBuilder) {
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        getManager().notify(id, notification);
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}