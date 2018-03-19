package com.example.orpriesender.karaoke.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    private double mistakes;
    private double success;
    private double groupGrade;
    private double mistakesRate;
    private double successRate;

    public Group(){
        mistakes = 0;
        success = 0;
        groupGrade = 0;
        mistakesRate = 0.5;
        successRate = 1-mistakesRate;
        wrongSamples = new ArrayList<>();
        rightSamples = new ArrayList<>();
    }

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

    public List<Pitch> getWrongSamples() {
        return wrongSamples;
    }

    public void setWrongSamples(List<Pitch> wrongSamples) {
        this.wrongSamples = wrongSamples;
    }

    public List<Pitch> getRightSamples() {
        return rightSamples;
    }

    public void setRightSamples(List<Pitch> rightSamples) {
        this.rightSamples = rightSamples;
    }

    public double getMistakes() {
        return mistakes;
    }

    public void setMistakes(double mistakes) {
        this.mistakes = mistakes;
    }

    public double getSuccess() {
        return success;
    }

    public void setSuccess(double success) {
        this.success = success;
    }

    public void setGroupGrade(double groupGrade) {
        this.groupGrade = groupGrade;
    }

    public double getMistakesRate() {
        return mistakesRate;
    }

    public void setMistakesRate(double mistakesRate) {
        this.mistakesRate = mistakesRate;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public double getGroupGrade() {
        return groupGrade;
    }

    @Override
    public String toString() {
        return "NOTE : " + note.toString() + "DURATION : " + getDuration() + "FILL RATE : " + getFillRate() + "\n";
    }
}
