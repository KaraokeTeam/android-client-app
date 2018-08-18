package com.example.orpriesender.karaoke.model;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Or Priesender on 12-Jan-18.
 */

public class Note {
    String note;
    int octave;
    double error;
    public static List<String> noteArr = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    public Note(String note, int octave, double error) {
        this.note = note;
        this.octave = octave;
        this.error = error;
    }

    public Note(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.000");
        return this.note + "\n(" + formatter.format(error) + ")";
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }


    public boolean equals(Note note) {
        if (this.note.equalsIgnoreCase(note.getNote())){

            return true;
        }

        return false;
    }

    public int distance(Note note) {
        return Math.abs(noteArr.indexOf(this.getNote()) - noteArr.indexOf(note.getNote()));
    }

    public int distanceWithNegative(Note note){
        return -1 * (noteArr.indexOf(this.getNote()) - noteArr.indexOf(note.getNote()));
    }

    public boolean isCorrectNote(Note note)
    {
        int d = this.distance(note);
        Log.d("Tag","Distnace between " + this.getNote() + " and " + note.getNote() + " is " + d);
        if (d==0 || d==1 || d==11) {
            return true;
        }
        else{
            return false;
        }
    }

    public int getNoteIndex(){
        return noteArr.indexOf(this.getNote());
    }

    public static String[] getNoteArr(){
        String[] strArr = new String[noteArr.size()];
        strArr = noteArr.toArray(strArr);
        return strArr;
    }
}
