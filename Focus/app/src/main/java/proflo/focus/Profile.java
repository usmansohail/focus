package proflo.focus;

import java.util.Vector;

/**
 * Created by Cameron on 10/15/2017.
 */

public class Profile {

    private String name;
    private Vector<String> profileApps;
    private boolean isActive;


    public Profile(String name, Vector<String> profileApps, boolean isActive) {
        this.name = name;
        this.profileApps = profileApps;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<String> getProfileApps() {
        return profileApps;
    }


    public boolean addAppToProfile(String appID){
        profileApps.add(appID);
        return true;
    }

    public boolean removeAppFromProfile(String appID){
       return profileApps.remove(appID);
    }


    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate(){
        isActive = false;
    }
}
