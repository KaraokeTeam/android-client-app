package com.example.orpriesender.karaoke;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class Post implements Serializable{
    private String id;
    private String userId;
    private String username;
    private String description;
    private String songName;
    private String time;
    private String songUrl;
    private Date date;
    private File performanceFile;
    private int audioPosition;


    public Post(String userId,String username,String description,String songName){
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.songName = songName;
        this.date = new Date();
        this.time =new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(date);
        this.audioPosition = 0;
        this.songUrl = "";
    }

    public Post(){

    }


    public Date getDate() {
        return date;
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

    public Map<String,Object> toMap(){
        Map<String,Object> newMap = new HashMap<>();
        newMap.put("userId",this.userId);
        newMap.put("username",this.username);
        newMap.put("description",this.description);
        newMap.put("songName",this.songName);
        newMap.put("time",this.time);
        newMap.put("date",this.date);
        newMap.put("songUrl",this.songUrl);
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

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public File getPerformanceFile() {
        return performanceFile;
    }

    public void setPerformanceFile(File performanceFile) {
        this.performanceFile = performanceFile;
    }
}
