package com.example.orpriesender.karaoke;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.000");
        return this.note + this.octave + "\n(" + formatter.format(error) + ")";
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
}
