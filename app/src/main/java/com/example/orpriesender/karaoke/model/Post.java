package com.example.orpriesender.karaoke.model;

import android.arch.lifecycle.Observer;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

@Entity(tableName = "posts")
public class Post implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo(name = "userId")
    private String userId;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "songName")
    private String songName;
    @ColumnInfo(name = "time")
    private String time;

    public double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @ColumnInfo(name = "lastUpdated")
    private double lastUpdated;
    @Ignore
    private Map<String,Object> timestamp;
    @Ignore
    private Date date;
    @Ignore
    private File performanceFile;
    @Ignore
    private int audioPosition;

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    @Ignore
    private Bitmap profilePic;


    public Post(String userId, String username, String description, String songName) {
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.songName = songName;
        this.timestamp = new HashMap<>();
        timestamp.put("timestamp",ServerValue.TIMESTAMP);
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        this.date = new Date();
        this.time = format.format(date);
        this.audioPosition = 0;
    }
    @Ignore
    public Post() {

    }


    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        try {
            this.date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }  finally{
            return this.date;
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAudioPosition() {
        return audioPosition;
    }

    public void setAudioPosition(int audioPosition) {
        this.audioPosition = audioPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("id",this.id);
        newMap.put("userId", this.userId);
        newMap.put("username", this.username);
        newMap.put("description", this.description);
        newMap.put("songName", this.songName);
        newMap.put("time", this.time);
        return newMap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public File getPerformanceFile() {
        return performanceFile;
    }

    public void setPerformanceFile(File performanceFile) {
        this.performanceFile = performanceFile;
    }
}
