package proflo.focus;

import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

public class Notification extends android.app.Notification {

    private Vector<Profile> applicationProfiles;

    public Notification(Vector<Profile> applicationProfiles) {
        this.applicationProfiles = applicationProfiles;
    }

    public Vector<Profile> getApplicationProfiles() {
        return applicationProfiles;
    }
}
