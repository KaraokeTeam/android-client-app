package com.example.orpriesender.karaoke;

import android.util.Log;

/**
 * Created by Or Priesender on 08-Jan-18.
 */

public class Pitch {

    private float pitch;
    private float start;
    private float end;

    public Pitch(float pitch){
        this.pitch = pitch;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }



    @Override
    public String toString() {
        return "PITCH : " + this.pitch + " START : " + this.getStart() + " END : " + this.getEnd();
    }
}
