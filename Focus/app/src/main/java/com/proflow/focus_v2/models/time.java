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

    public String toString(){
        StringBuilder sb = new StringBuilder();

        //Hour:
        if(hour == 0){
            sb.append(12);
        } else if(hour < 13){
            sb.append(hour);
        } else {
            sb.append(hour - 12);
        }

        //Colon... Obviously.
        sb.append(":");

        //Minute:
        if(minute < 10){
            sb.append(0);
            sb.append(minute);
        } else {
            sb.append(minute);
        }

        //Space for prettiness
        sb.append(" ");

        //AM-PM
        if(hour < 12){
            sb.append("AM");
        } else {
            sb.append("PM");
        }

        return sb.toString();
    }

    public String getRFCString()
    {
        StringBuilder sb = new StringBuilder();
        if(hour < 10)
        {
            sb.append("0" + hour);
        }
        else
        {
            sb.append(hour);
        }
        sb.append(":");
        if(minute < 10)
        {
            sb.append("0" + minute);
        }
        else
        {
            sb.append(minute);
        }
        sb.append(":00");

        return sb.toString();
    }
}

