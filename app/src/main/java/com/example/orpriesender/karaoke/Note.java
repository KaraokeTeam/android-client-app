package com.example.orpriesender.karaoke;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Or Priesender on 12-Jan-18.
 */

public class Note {
    String note;
    int octave;
    double error;


    public Note(String note,int octave,double error){
        this.note = note;
        this.octave = octave;
        this.error = error;
    }

    public Note(String note){
        this.note = note;
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.000");
        return this.note  + "\n(" + formatter.format(error) + ")";
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

    @Override
    public boolean equals(Object obj) {
        Note note = (Note) obj;
        if(this.note.equalsIgnoreCase(note.getNote()))
            return true;
        return false;
    }

    public int distance(Note note)
    {
        List<String> noteArr = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
        return Math.abs(noteArr.indexOf(this.getNote())-noteArr.indexOf(note.getNote()));
    }

}
