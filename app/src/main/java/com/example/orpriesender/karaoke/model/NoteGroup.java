package com.example.orpriesender.karaoke.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public class NoteGroup {
    List<Integer> pitches = new ArrayList<>();
    Character note;

    public NoteGroup() {

    }

    public NoteGroup(Character note, Integer... pitches) {
        this.note = note;
        for (Integer pitch : pitches) {
            this.pitches.add(pitch);
        }
    }


    public List<Integer> getPitches() {
        return pitches;
    }

    public void setPitches(List<Integer> pitches) {
        this.pitches = pitches;
    }

    public Character getNote() {
        return note;
    }

    public void setNote(Character note) {
        this.note = note;
    }
}
