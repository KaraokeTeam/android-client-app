package com.example.orpriesender.karaoke;

/**
 * Created by Or Priesender on 26-Feb-18.
 */

public class Group {

    private Note note;
    private float startTime;
    private float endTime;
    private int samplesAmount;
    private float fillRate;
    private float duration;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public int getSamplesAmount() {
        return samplesAmount;
    }

    public void setSamplesAmount(int samplesAmount) {
        this.samplesAmount = samplesAmount;
    }

    public float getFillRate() {
        return fillRate;
    }

    public void setFillRate(float fillRate) {
        this.fillRate = fillRate;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "NOTE : " + note.toString() + "DURATION : " + getDuration() + "FILL RATE : " + getFillRate() + "\n";
    }
}
