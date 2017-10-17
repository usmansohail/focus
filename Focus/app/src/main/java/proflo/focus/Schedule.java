package proflo.focus;

import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

public class Schedule {

    private String name;
    private Vector<TimeBlock> timeBlocks;
    private Boolean repeatWeekly;
    private Vector<Profile> profiles;
    private Boolean isActive;


    public Schedule(String name, Vector<TimeBlock> timeBlocks, Boolean repeatWeekly) {
        this.name = name;
        this.timeBlocks = timeBlocks;
        this.repeatWeekly = repeatWeekly;
    }

    public String getName() {
        return name;
    }

    public Vector<TimeBlock> getTimeBlocks() {
        return timeBlocks;
    }

    public Vector<Profile> getProfiles() {
        return profiles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean enableRepeatWeekly(){
        repeatWeekly = true;
        return true;
    }

    public Boolean disableRepeatWeekly(){
        repeatWeekly = false;
        return true;
    }

    public void setRepeatWeekly(Boolean repeatWeekly) {
        this.repeatWeekly = repeatWeekly;
    }

    public Boolean addProfile(Profile profile){
        profiles.add(profile);
        return true;
    }

    public Boolean removeProfile(Profile profile){
        return profiles.remove(profile);
    }

    public Boolean addTimeBlock(TimeBlock timeblock){
        timeBlocks.add(timeblock);
        return true;
    }

    public Boolean removeTimeBlock(TimeBlock timeblock){
        return timeBlocks.remove(timeblock);
    }

    public Boolean isActive(){
        return isActive;
    }

    public Boolean isRepeatWeekly(){
        return repeatWeekly;
    }
}
