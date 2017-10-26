package com.proflow.focus_v2.models;

/**
 * Created by forre on 10/26/2017.
 */

import java.util.Vector;

public class Notification{


    private Vector<Profile> applicationProfiles;
    private android.app.Notification notification;

    public Notification(android.app.Notification notification, Vector<Profile> applicationProfiles) {
        this.notification = notification;
        this.applicationProfiles = applicationProfiles;
    }

    public Vector<Profile> getApplicationProfiles() {
        return applicationProfiles;
    }
    public android.app.Notification getNotification () {return notification;}
}