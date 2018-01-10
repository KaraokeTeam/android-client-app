package com.example.orpriesender.karaoke;

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


    public Post(String id,String username,String description,String songName){
        this.username = username;
        this.description = description;
        this.songName = songName;
        this.id = id;
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
}
