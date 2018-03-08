package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Or Priesender on 07-Mar-18.
 */

@Entity(tableName = "songs")
public class SongItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    String id;
    @ColumnInfo(name = "fullName")
    String fullName;
    @ColumnInfo(name = "systemName")
    String systemName;

    public SongItem(String fullName, String systemName){
        this.fullName = fullName;
        this.systemName = systemName;
    }

    @Ignore
    public SongItem(){

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,String> toMap(){
        Map<String,String> map = new HashMap<>();
        map.put("id",this.id);
        map.put("fullName",this.fullName);
        map.put("systemName",this.systemName);
        return map;
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
