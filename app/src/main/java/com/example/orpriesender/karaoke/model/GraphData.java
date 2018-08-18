package com.example.orpriesender.karaoke.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Or Priesender on 18-Aug-18.
 */

public class GraphData implements Serializable{
    List<NoteTimePair> performance;
    List<NoteTimePair> source;

    public GraphData(List<NoteTimePair> performance, List<NoteTimePair> source){
        this.performance = performance;
        this.source = source;
    }

    public GraphData(){

    }

    public List<NoteTimePair> getPerformance() {
        return performance;
    }

    public void setPerformance(List<NoteTimePair> performance) {
        this.performance = performance;
    }

    public List<NoteTimePair> getSource() {
        return source;
    }

    public void setSource(List<NoteTimePair> source) {
        this.source = source;
    }
}
