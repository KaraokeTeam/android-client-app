package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

@Entity(tableName = "lastUpdated")
public class LastUpdated {
    @PrimaryKey
    @NonNull
    private String name;
    private double lastUpdated;

    public LastUpdated(String name,double lastUpdated){
        this.name = name;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
