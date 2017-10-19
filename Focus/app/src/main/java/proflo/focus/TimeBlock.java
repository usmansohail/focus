package proflo.focus;

import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

public class TimeBlock {

    private Long startTime;
    private Long endTime;
    private Vector<Boolean> days;

    private int startMinute;
    private int stopMinute;
    private int stopHour;
    private int startHour;



    public int getStartMinute() {
        return startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStopHour() {
        return stopHour;
    }

    public int getStopMinute() {
        return stopMinute;
    }

    public TimeBlock(Long startTime, Long endTime, Vector<Boolean> days) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
    }

    public TimeBlock(int startHour, int startMinute, int stopHour, int stopMinute, Vector<Boolean> days) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.stopHour = stopHour;
        this.stopMinute = stopMinute;
        this.days = days;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Vector<Boolean> getDays() {
        return days;
    }

    public void setDays(Vector<Boolean> days) {
        this.days = days;
    }
}
