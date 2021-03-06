package com.example.orpriesender.karaoke.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Or Priesender on 26-Feb-18.
 */

public class Group implements Serializable{


    private Note note;
    private float startTime;
    private float endTime;
    private int samplesAmount;
    private double fillRate; //the rate of this group from the whole song
    private double duration;
    private List<Pitch> wrongSamples;
    private List<Pitch> rightSamples;
    private List<Pitch> allSamples;
    private double totalSamples;
    private double mistakes;
    private double success;
    private double groupGrade;
    private double mistakesRate;
    private double successRate;

    public Group(){
        totalSamples = 0;
        mistakes = 0;
        success = 0;
        groupGrade = 0;
        mistakesRate = 0.1;
        successRate = 1-mistakesRate;
        wrongSamples = new ArrayList<>();
        rightSamples = new ArrayList<>();
        allSamples = new ArrayList<>();
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

    public float getMiddleTime(){
        float start = this.getStartTime();
        float end = this.getEndTime();
        float middle = start + ((end - start) / 2);
        return middle;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public void updateStartTime(){
        if(allSamples.size() > 0){
            this.startTime = allSamples.get(0).getStart();
        } else this.startTime = 0;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public void updateEndTime() {
        if(allSamples.size() > 0)
            this.endTime = allSamples.get(allSamples.size() - 1).getStart();
        else this.endTime = 0;
    }

    public int getSamplesAmount() {
        return samplesAmount;
    }

    public void setSamplesAmount(int samplesAmount) {
        this.samplesAmount = samplesAmount;
    }

    public double getFillRate() {
        return fillRate;
    }

    public void setFillRate(float fillRate) {
        this.fillRate = fillRate;
    }

    public void updateFillRate(float totalTime){
        if(this.duration != 0){
            this.fillRate = duration / totalTime;
        } else this.fillRate = 0;
    }
    public double getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void updateDuration(){
        if(this.startTime != 0 && this.endTime != 0){
            this.duration = this.endTime - this.startTime;
        } else this.duration = 0;
    }

    public void addToWrongSamples (Pitch pitch)
    {
        allSamples.add(pitch);
        wrongSamples.add(pitch);
        totalSamples++;
    }

    public void addToRightSamples (Pitch pitch)
    {
        allSamples.add(pitch);
        rightSamples.add(pitch);
        success++;
        totalSamples++;
    }

    public void addSuccess(double s)
    {
        success+=s;
    }

    public void addMistakes(double s)
    {
        mistakes+=s;
    }

    public void calculateGrade(){
        if(totalSamples != 0){ //TODO : add check to minimum totalSamples according to source
            groupGrade = 100 * (success / totalSamples);
            Log.d("TAG","success/total = " + success + "/" + totalSamples);
            Log.d("TAG","Group Grade : " + groupGrade);
        } else groupGrade = 0;

    }

    public List<Pitch> getAllSamples() {
        return allSamples;
    }

    public void setAllSamples(List<Pitch> allSamples) {
        this.allSamples = allSamples;
    }

    public void addToAllSamples(Pitch pitch){
        allSamples.add(pitch);
        totalSamples++;
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

    public void setFillRate(double fillRate) {
        this.fillRate = fillRate;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getTotalSamples() {
        return totalSamples;
    }

    public void setTotalSamples(double totalSamples) {
        this.totalSamples = totalSamples;
    }
}
