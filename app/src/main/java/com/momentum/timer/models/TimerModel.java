package com.momentum.timer.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class TimerModel implements Serializable {
    private int id,audioSetting;
    private String selectedAudio;
    private String timerTitle,dominoTitle,restTitle;
    private long segmentHours,segmentMinutes,segmentSeconds;
    private long currentHour,currentMinute,currentSecond;
    private int currentDominoSegment=0,currentRestSegment=0,spacingPercentage,numberOfSegment,numberOfRepeat;
    private ArrayList<Long> allSegments=new ArrayList<>();
    private ArrayList<Long> restSegments=new ArrayList<>();
    private String activeTime="domino";
    private boolean timeRunning;
    private boolean paused;
    private int currentRepeat=0;
    private ArrayList<Long> timeChain=new ArrayList<>();

    public Boolean isDomino() {
        return activeTime.equals(Constants.DOMINO);
    }

    public ArrayList<Long> getTimeChain() {
        return timeChain;
    }

    public void calculateAllSegments() {
        allSegments.clear();
        restSegments.clear();
        timeChain.clear();
        allSegments.add(getMainTotalSecond());
        for (int i=1;i<numberOfSegment;i++) {
            allSegments.add((Long) Math.round(allSegments.get(i-1)*Constants.INCREASING_TIME));
        }
        for (int i=0;i<numberOfSegment;i++) {
            restSegments.add((Long)Math.round(allSegments.get(i)*(spacingPercentage/100.0)));
        }
        for (int i=0;i<numberOfSegment;i++) {
            timeChain.add(allSegments.get(i));
            timeChain.add(restSegments.get(i));
        }
    }

    public int getAudioSetting() {
        return audioSetting;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getCurrentRepeat() {
        return currentRepeat;
    }

    public void setCurrentRepeat(int currentRepeat) {
        this.currentRepeat = currentRepeat;
    }

    public String getSelectedAudio() {
        return selectedAudio;
    }

    public void setSelectedAudio(String selectedAudio) {
        this.selectedAudio = selectedAudio;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void increaseRepeat() {
        currentRepeat++;
    }

    public void increaseDomino() {
        currentDominoSegment++;
    }
    public void  increaseRest() {
        currentRestSegment++;
    }
    public void getCurrentSegmentTime() {
        if (currentSecond%2==0) {
            activeTime="domino";
            if (allSegments.get(currentDominoSegment)>=3600) {
                currentHour=(int)Math.round((float)allSegments.get(currentDominoSegment)/Constants.HOUR_CONSTANT);
            }
            Long remainingTime=allSegments.get(currentDominoSegment)-(currentHour*Constants.HOUR_CONSTANT);
            if (remainingTime>59) {
                currentMinute=Math.round((float) remainingTime/Constants.MINUTE_CONSTANT);
            }
            remainingTime=remainingTime-(currentMinute*Constants.MINUTE_CONSTANT);
            currentSecond=currentMinute;
        }
        else {
            activeTime="rest";
            if (restSegments.get(currentRestSegment)>=3600) {
                currentHour=(int)Math.round((float)allSegments.get(currentRestSegment)/Constants.HOUR_CONSTANT);
            }
            Long remainingTime=allSegments.get(currentRestSegment)-(currentHour*Constants.HOUR_CONSTANT);
            if (remainingTime>59) {
                currentMinute = Math.round((float) remainingTime / Constants.MINUTE_CONSTANT);
            }
            remainingTime = remainingTime - (currentMinute * Constants.MINUTE_CONSTANT);
            currentSecond = currentMinute;
        }
    }

    public ArrayList<Long> getAllSegments() {
        return allSegments;
    }

    public long getWholeDuration() {
        long time = 0;
        for (Long i : allSegments) {
            time = time + i;
        }
        for (Long i : restSegments) {
            time = time + i;
        }
        Log.d("wholeDuration", time + "");
        return time;
    }

    public ArrayList<Long> getRestSegments() {
        return restSegments;
    }

    public boolean isTimeRunning() {
        return timeRunning;
    }

    public void setTimeRunning(boolean timeRunning) {
        this.timeRunning = timeRunning;
    }

    public int getCurrentDominoSegment() {
        return currentDominoSegment;
    }

    public void setCurrentDominoSegment(int currentDominoSegment) {
        this.currentDominoSegment = currentDominoSegment;
    }

    public int getCurrentRestSegment() {
        return currentRestSegment;
    }

    public void setCurrentRestSegment(int currentRestSegment) {
        this.currentRestSegment = currentRestSegment;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public long getMainTotalSecond() {
        return segmentSeconds+(segmentMinutes*60)+(segmentHours*60*60);
    }

    public long getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(long currentHour) {
        this.currentHour = currentHour;
    }

    public long getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(long currentMinute) {
        this.currentMinute = currentMinute;
    }

    public long getCurrentSecond() {
        return currentSecond;
    }

    public void setCurrentSecond(long currentSecond) {
        this.currentSecond = currentSecond;
    }

    public TimerModel(int id, String timerTitle, String dominoTitle, String restTitle, long segmentHours, long segmentMinutes, long segmentSeconds, int spacingPercentage, int numberOfSegment, int numberOfRepeat, int audioSetting, String selectedAudio) {
        this.id = id;
        this.timerTitle = timerTitle;
        this.dominoTitle = dominoTitle;
        this.restTitle = restTitle;
        this.segmentHours = segmentHours;
        this.segmentMinutes = segmentMinutes;
        this.segmentSeconds = segmentSeconds;
        this.spacingPercentage = spacingPercentage;
        this.numberOfSegment = numberOfSegment;
        this.numberOfRepeat = numberOfRepeat;
        this.audioSetting = audioSetting;
        this.selectedAudio = selectedAudio;

    }

    public TimerModel(String timerTitle, String dominoTitle, String restTitle, int segmentHours, int segmentMinutes, int segmentSeconds, int spacingPercentage, int numberOfSegment, int numberOfRepeat) {
        this.timerTitle = timerTitle;
        this.dominoTitle = dominoTitle;
        this.restTitle = restTitle;
        this.segmentHours = segmentHours;
        this.segmentMinutes = segmentMinutes;
        this.segmentSeconds = segmentSeconds;
        this.spacingPercentage = spacingPercentage;
        this.numberOfSegment = numberOfSegment;
        this.numberOfRepeat = numberOfRepeat;
    }

    public int getId() {
        return id;
    }

    public String getTimerTitle() {
        return timerTitle;
    }

    public String getDominoTitle() {
        return dominoTitle;
    }

    public String getRestTitle() {
        return restTitle;
    }

    public long getSegmentHours() {
        return segmentHours;
    }

    public long getSegmentMinutes() {
        return segmentMinutes;
    }

    public long getSegmentSeconds() {
        return segmentSeconds;
    }

    public int getSpacingPercentage() {
        return spacingPercentage;
    }

    public int getNumberOfSegment() {
        return numberOfSegment;
    }

    public int getNumberOfRepeat() {
        return numberOfRepeat;
    }
    @NonNull
    public String toString() {
        return "id: " + id + " timerTitle:" + timerTitle + " dominoTitle:" + dominoTitle + " restTitle:" + restTitle + " segmentHours:" + segmentHours + " segmentMinutes:" + segmentMinutes + " segmentSeconds:" + segmentSeconds + " spacingPercentage:" + spacingPercentage + " numberOfSegment:" + numberOfSegment + " numberOfRepeat:" + numberOfRepeat + " audioSetting: " + audioSetting;
    }
}
