package com.example.orpriesender.karaoke.model;

import java.io.Serializable;

/**
 * Created by Or Priesender on 18-Aug-18.
 */

public class NoteTimePair implements Serializable{
    String note;
    int noteIndex;
    float time;

    public NoteTimePair(){

    }

    public NoteTimePair(String note, int noteIndex, float time){
        this.note = note;
        this.noteIndex = noteIndex;
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getNoteIndex() {
        return noteIndex;
    }

    public void setNoteIndex(int noteIndex) {
        this.noteIndex = noteIndex;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
