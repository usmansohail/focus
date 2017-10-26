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
    }

    time mStartTime;
    time mEndTime;
    day mDay;

    public TimeBlock(time startTime, time endTime, day day){
        mStartTime = startTime;
        mEndTime = endTime;
        mDay = day;
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

    public void setDays(day Day) {
        this.mDay = Day;
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

    public day getDay() {
        return mDay;
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

            return (other.getDay() == this.getDay())
                    && other.getStartTime().equals(this.getStartTime())
                    && other.getEndTime().equals(this.getEndTime());
        }
    }

    public String getStartString() {
        String ret = getDay().toString().substring(0,1) + getDay().toString().substring(1);
        ret += " " + getStartTime().hour + ":" +getStartTime().minute;

        return ret;
    }

    public String getEndString(){
        String ret = getDay().toString().substring(0,1) + getDay().toString().substring(1);
        ret += " " + getEndTime().hour + ":" +getEndTime().minute;
        return ret;
    }
}
