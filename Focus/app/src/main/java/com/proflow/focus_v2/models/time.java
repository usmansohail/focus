package com.proflow.focus_v2.models;

import java.io.Serializable;

/**
 * Created by forre on 10/23/2017.
 */

public class time implements Serializable{
    public int hour;
    public int minute;

    public time(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }

    public boolean isBefore(time otherTime) {
        if(this.hour < otherTime.hour){
            return true;
        }

        if(this.hour == otherTime.hour && this.minute < otherTime.minute){
            return true;
        }

        return false;
    }

    public boolean isAfter(time otherTime){
        if(this.hour > otherTime.hour){
            return true;
        }
        if(this.hour == otherTime.hour && this.minute > otherTime.minute){
            return true;
        }
        return false;
    }

    public boolean equals(time otherTime){
        return this.hour == otherTime.hour && this.minute == otherTime.minute;
    }

    public boolean isBetween(time startTime, time endTime){
        return(this.isBefore(endTime) && this.isAfter(startTime));
    }
}

