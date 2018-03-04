package com.example.orpriesender.karaoke;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by Or Priesender on 10-Jan-18.
 */

public class User {
    private String id;
    private String username;
    private Uri imageUrl;
    private int rating;

    public User(FirebaseUser user){
        this.id = user.getUid();
        this.username = user.getDisplayName();
        this.imageUrl = user.getPhotoUrl();
        this.rating = 0;
    }

    public User(){

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

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
