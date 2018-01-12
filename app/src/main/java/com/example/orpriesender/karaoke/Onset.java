package com.example.orpriesender.karaoke;

/**
 * Created by Or Priesender on 12-Jan-18.
 */

public class Onset {
    private float time;

    public Onset(float time){
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TIME : " + this.getTime();
    }
}
