package com.example.orpriesender.karaoke;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class Post {
    private String id;
    private String userId;
    private String username;
    private String description;
    private String songName;
    private String time;
    private String songUrl;
    private int audioPosition;


    public Post(String userId,String username,String description,String songName,String songUrl){
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.songName = songName;
        this.time = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date());
        this.songUrl = songUrl;
        this.audioPosition = 0;
    }

    public Post(){

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

    public Map<String,String> toMap(){
        Map<String,String> newMap = new HashMap<>();
        newMap.put("userId",this.userId);
        newMap.put("username",this.username);
        newMap.put("description",this.description);
        newMap.put("songName",this.songName);
        newMap.put("time",this.time);
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
}
