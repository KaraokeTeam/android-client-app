package com.example.orpriesender.karaoke;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class Post {
    private int id;
    private String username;
    private String decsription;
    private String songName;

    public Post(int id,String username,String description,String songName){
        this.username = username;
        this.decsription = description;
        this.songName = songName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
