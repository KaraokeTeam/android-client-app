package com.example.orpriesender.karaoke.model;

/**
 * Created by Or Priesender on 08-Jan-18.
 */

public class Pitch {

    private float pitch;
    private float start;
    private float end;
    private float confidence;

    public Pitch(float pitch, float start, float end, float confidence) {

        this.pitch = pitch;
        this.start = start;
        this.end = end;
        this.confidence = confidence;

    }

    public Pitch(float pitch, float start, float confidence) {

        this.pitch = pitch;
        this.start = start;
        this.end = -1;
        this.confidence = confidence;

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


    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "PITCH : " + this.getPitch() + " START : " + this.getStart() + " CONF : " + this.getConfidence();
    }
}
