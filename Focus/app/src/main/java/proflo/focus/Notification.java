package proflo.focus;

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
