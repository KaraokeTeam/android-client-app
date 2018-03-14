package com.example.orpriesender.karaoke.model;

import java.util.List;

/**
 * Created by Or Priesender on 26-Feb-18.
 */

public class Group {


    private Note note;
    private float startTime;
    private float endTime;
    private int samplesAmount;
    private float fillRate; //the rate of this group from the whole song
    private float duration;
    private List<Pitch> wrongSamples;
    private List<Pitch> rightSamples;
    private double mistakes=0;
    private double success=0;
    private double groupGrade=0;
    private double mistakesRate = 0.5;
    private double successRate = 1 - mistakesRate;

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

    public void addToWrongSamples (Pitch pitch)
    {
        wrongSamples.add(pitch);
    }

    public void addToRightSamples (Pitch pitch)
    {
        rightSamples.add(pitch);
    }

    public void addSuccess(double s)
    {
        success+=s;
    }

    public void addMistakes(double s)
    {
        mistakes+=s;
    }

    public void calculateGrade()
    {
        double goodGrade = 100*(success/samplesAmount);
        double badGrade = 100 - (100*(mistakes/samplesAmount));
        if(goodGrade>100){goodGrade = 100;}
        if(badGrade<0) {badGrade = 0;}
        goodGrade *= successRate;
        badGrade *= mistakesRate;
        groupGrade = goodGrade + badGrade;
    }

    public double getGroupGrade() {
        return groupGrade;
    }

    @Override
    public String toString() {
        return "NOTE : " + note.toString() + "DURATION : " + getDuration() + "FILL RATE : " + getFillRate() + "\n";
    }
}
