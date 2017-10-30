package com.proflow.focus_v2.models;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by forre on 10/23/2017.
 */

public class TimeBlock implements Serializable {

    public enum day {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;

        public static day fromInteger(int x){
            switch(x){
                case 1:
                    return MONDAY;
                case 2:
                    return TUESDAY;
                case 3:
                    return WEDNESDAY;
                case 4:
                    return THURSDAY;
                case 5:
                    return FRIDAY;
                case 6:
                    return SATURDAY;
                case 0:
                    return SUNDAY;
            }
            return null;
        }

        public static int toInteger(day Day){
            switch(Day){
                case MONDAY:
                    return 1;
                case TUESDAY:
                    return 2;
                case WEDNESDAY:
                    return 3;
                case THURSDAY:
                    return 4;
                case FRIDAY:
                    return 5;
                case SATURDAY:
                    return 6;
                case SUNDAY:
                    return 0;
            }
            return -1;
        }

        //Technically shouldn't use strings b/c localization. Issue for later.
        public String getShortString() {
            switch(this){
                case SUNDAY:
                    return "Su";
                case MONDAY:
                    return "M";
                case TUESDAY:
                    return "T";
                case WEDNESDAY:
                    return "W";
                case THURSDAY:
                    return "Th";
                case FRIDAY:
                    return "F";
                case SATURDAY:
                    return "Sa";
                default:
                    return "NA?";
            }
        }
    }

    private time mStartTime;
    private time mEndTime;
    private Vector<day> mDays;

    @Deprecated
    public TimeBlock(time startTime, time endTime, day day){
        mStartTime = startTime;
        mEndTime = endTime;
        mDays = new Vector<>();
        mDays.add(day);
    }

    public TimeBlock(time startTime, time endTime, Vector<day> day){
        mStartTime = startTime;
        mEndTime = endTime;
        mDays = day;
    }


    /*
    SETTERS
     */
    public void setStartTime(time startTime) {
        if(startTime.isBefore(getEndTime())){
            mStartTime = startTime;
        }
    }

    public void setEndTime(time endTime) {
        if(getStartTime().isBefore(endTime)) {
            this.mEndTime = endTime;
        }
    }

    public void setDays(Vector<day> days) {
        mDays = days;
    }

    /*
    GETTERS
     */

    public time getStartTime() {
        return mStartTime;
    }

    public time getEndTime() {
        return mEndTime;
    }

    public boolean hasDay(day d){
        return mDays.contains(d);
    }

    public Vector<day> getDays(){
        return mDays;
    }



    /*
    COMPARATORS/OVERLOADS
     */

    //So if two ranges (x1, x2) and (y1, y2) overlap it means that there exists C, such that
    //x1 <= C <= x2 && y1 <= C <= y2. So it's sufficient to test x1 <= y2 && y1 <= x2 or:
    public boolean overlaps(TimeBlock block) {
        return (this.getStartTime().isBefore(block.getEndTime()) || this.getStartTime().equals(block.getEndTime()))
                && (block.getStartTime().isBefore(this.getEndTime()) || block.getStartTime().equals(this.getEndTime()));
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TimeBlock)){
            return super.equals(obj);
        } else {
            TimeBlock other = (TimeBlock) obj;

            for(day day : other.getDays()){
                if(!mDays.contains(day)){
                    return false;
                }
            }

            return (other.getStartTime().equals(this.getStartTime())
                    && other.getEndTime().equals(this.getEndTime()));
        }
    }

    public String getStartString() {
        StringBuilder sb = new StringBuilder();

        for(day day: getDays()){
            sb.append(day.getShortString());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(" " + getStartTime().toString());
        return sb.toString();
    }

    public String getEndString(){
        StringBuilder sb = new StringBuilder();

        for(day day: getDays()){
            sb.append(day.getShortString());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(" " + getEndTime().toString());
        return sb.toString();
    }

    //Returns false if the days vector doesn't contain the day, or if it fails to remove.
    public boolean removeDay(day day){
        if(mDays.contains(day)){
            return mDays.remove(day);
        }
        return false;
    }

    //Returns false if the vector failed to add, or the day is already in the mDays vector
    public boolean addDay(day day){
        if(!mDays.contains(day)){
            return mDays.add(day);
        }
        return false;
    }
}
