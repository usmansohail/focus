package proflo.focus;

import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

public class TimeBlock {

    private Long startTime;
    private Long endTime;
    private Vector<String> days;

    public TimeBlock(Long startTime, Long endTime, Vector<String> days) {
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Vector<String> getDays() {
        return days;
    }

    public void setDays(Vector<String> days) {
        this.days = days;
    }
}
