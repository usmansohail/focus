package proflo.focus;

import android.os.CountDownTimer;

import java.util.Vector;

/**
 * Created by Cameron on 10/16/2017.
 */

public class Timer{

    private String name;
    private Long initialDuration;
    private Long currentDuration;
    private Boolean isOn;
    private Long startTime;
    private Vector<Profile> timerProfiles;
    private Vector<String> appBucket;
    private CountDownTimer timer;
    private Boolean isPaused = false;

    //Initial duration should be in milliseconds
    public Timer(String name, Long initialDuration, Vector<Profile> timerProfiles) {

        this.name = name;
        this.initialDuration = initialDuration;
        this.currentDuration = initialDuration;
        this.timerProfiles = timerProfiles;

    }

    public String getName() {
        return name;
    }

    public Long getInitialDuration() {
        return initialDuration;
    }

    public Long getCurrentDuration() {
        return currentDuration;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Vector<Profile> getTimerProfiles() {
        return timerProfiles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInitialDuration(Long initialDuration) {
        this.initialDuration = initialDuration;
    }

    public boolean turnOff(){
        isOn = false;
        return true;
    }

    public boolean turnOn(){
        isOn = true;
        return true;
    }

    public boolean addProfile(Profile profile){
        timerProfiles.add(profile);
        return true;
    }

    public boolean removeProfile(Profile profile){
        return timerProfiles.remove(profile);
    }

    public Boolean pause(){
        timer.cancel();
        isPaused = true;
        return true;
    }

    public Boolean start(){
        timer = new CountDownTimer(currentDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentDuration = millisUntilFinished;
            }

            @Override
            public void onFinish() {

            }
        };

        timer.start();
        return true;
    }

    public Boolean reset(){
        try{
            currentDuration = initialDuration;
            timer = new CountDownTimer(initialDuration, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentDuration = millisUntilFinished;
                }

                @Override
                public void onFinish() {

                }
            };
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public Boolean isPaused(){
        return isPaused;
    }






}
