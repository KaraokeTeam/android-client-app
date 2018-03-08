package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Or Priesender on 10-Jan-18.
 */

@Entity(tableName = "users")
@TypeConverters(User.class)
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String username;
    private Uri imageUrl;
    private int rating;

    public User(FirebaseUser user) {
        this.id = user.getUid();
        this.username = user.getDisplayName();
        this.imageUrl = user.getPhotoUrl();
        this.rating = 0;
    }

    public User() {

    }

    @TypeConverter
    public static String UriToString(Uri uri){
        if(uri == null)
            return null;
        return uri.toString();
    }

    @TypeConverter
    public static Uri StringToUri(String string){
        if(string == null)
            return null;
        return Uri.parse(string);
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
