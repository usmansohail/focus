package proflo.focus;

import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

class Global {
    private static final Global instance = new Global();

    static Global getInstance() { return instance; }

    private Vector<Profile> allProfiles;
    private Vector<Profile> activeProfiles;

    private Vector<String> activeApps;
    private Vector<Schedule> schedules;

    //TODO What the fuck is this precisely
    private Calendar schedule;

    private Vector<Timer> timers;
    private Vector<Notification> notifications;

    public boolean loaded = false;

    private Global() {
    }

    public Vector<Profile> getAllProfiles() {
        return allProfiles;
    }

    public Vector<Profile> getActiveProfiles() {
        return activeProfiles;
    }

    public Vector<Schedule> getSchedules() {
        return schedules;
    }

    public Calendar getSchedule() {
        return schedule;
    }

    public Vector<Timer> getTimers() {
        return timers;
    }

    public Vector<Profile> getActiveProfilesForApp(String appID){
        Vector<Profile> activeprofiles = new Vector<Profile>();

        for (int i = 0; i < activeProfiles.size(); i++){
            for (int j = 0; j < activeProfiles.get(i).getProfileApps().size(); j++){
                if (activeProfiles.get(i).getProfileApps().get(j) == appID){
                    activeprofiles.add(activeProfiles.get(i));
                }
            }
        }

        return activeprofiles;
    }

    //new profiles isActive set to false
    public Boolean createProfile(String name, Vector<String> apps){
        Profile tprofile = new Profile(name, apps, false);
        return true;
    }

    public Boolean modifyProfile(String name, Vector<String> apps){

        for (int i= 0; i < activeProfiles.size(); i++){
            if (activeProfiles.get(i).getName() == name ){
                activeProfiles.get(i).setProfileApps(apps);
            }
        }

        return true;
    }


    public Vector<String> getActiveApps() {
        return activeApps;
    }

    public void setActiveApps(Vector<String> activeApps) {
        this.activeApps = activeApps;
    }

    public Boolean removeProfile(Profile profile){
        return allProfiles.remove(profile);
    }

    public Boolean addActiveProfile(Profile profile){
        for (int i = 0; i < allProfiles.size(); i++){
            if (allProfiles.get(i) == profile){
                activeProfiles.add(allProfiles.get(i));
            }
        }

        return true;
    }

    public Boolean removeActiveProfile(Profile profile){
        return activeProfiles.remove(profile);
    }

    public Boolean createSchedule(String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly){
        Schedule schedule = new Schedule(name, timeBlocks, repeatWeekly);
        schedules.add(schedule);

        return true;
    }

    public Boolean modifySchedule(String name, TimeBlock timeBlocks, Boolean repeatWeekly){

        for (int i = 0; i < schedules.size(); i++){
            if (schedules.get(i).getName() == name){
                schedules.get(i).addTimeBlock(timeBlocks);
                schedules.get(i).setRepeatWeekly(repeatWeekly);

            }
        }

        return true;
    }

    public Boolean removeSchedule(Schedule schedule){
        return schedules.remove(schedule);
    }

    public Boolean createTimer(String name, Long initialDuration, Vector<Profile> timerProfiles){
        Timer ttimer = new Timer(name, initialDuration, timerProfiles);
        timers.add(ttimer);
        return true;
    }

    public Boolean modifyTimer(String name, Long initialDuration, Profile newProfile){
        for (int i = 0; i < timers.size(); i++){
            if (timers.get(i).getName() == name){
                timers.get(i).setInitialDuration(initialDuration);
                timers.get(i).addProfile(newProfile);
            }
        }

        return true;
    }

    public Boolean removeTimer(Timer timer){
        return timers.remove(timer);
    }

    public Boolean createNotification(Vector<Profile> profiles, android.app.Notification notification){
        Notification not = new Notification(notification, profiles);
        notifications.add(not);

        return true;
    }

    public Boolean removeNotification(Notification notification){
        return notifications.remove(notification);
    }


    public Vector<Notification> getNotifications() {
        return notifications;
    }


    public void setAllProfiles(Vector<Profile> allProfiles) {
        this.allProfiles = allProfiles;
    }

    public void setActiveProfiles(Vector<Profile> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public void setSchedules(Vector<Schedule> allSchedules) {
        this.schedules = allSchedules;
    }

    public void setTimers(Vector<Timer> timers) {
        this.timers = timers;
    }

    public void setNotifications(Vector<Notification> notifications) {
        this.notifications = notifications;
    }
}
