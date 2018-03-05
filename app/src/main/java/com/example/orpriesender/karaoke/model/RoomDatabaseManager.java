package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class RoomDatabaseManager {
    private static Context context;
    private static RoomDatabaseManager instance = new RoomDatabaseManager();
    private static MyRoomDatabase db;

    public static RoomDatabaseManager getInstance(){
        return instance;
    }

    public static void setContext(Context c){
        context = c;
        init();
    }

    private static void init(){
        db = Room.databaseBuilder(context, MyRoomDatabase.class,"KaraokeDB").build();
    }

    public void updateLocal(){
        db.lastUpdatedDao().updateLocal(new LastUpdated("local", System.currentTimeMillis()));
    }

    public void updateRemote(){
        db.lastUpdatedDao().updateRemote(new LastUpdated("remote",System.currentTimeMillis()));
    }

    public double getRemote(){
        return db.lastUpdatedDao().getRemote().getLastUpdated();
    }

    public double getLocal(){
        return db.lastUpdatedDao().getLocal().getLastUpdated();
    }

    //TODO : check if the list works, if not replace with Post...posts
    public void addPosts(List<Post> posts){
        db.postDao().insertAll(posts);
    }

    public List<Post> getAllPosts(){
        return db.postDao().getAllPosts();
    }

    public void deleteAll(){
        db.postDao().deleteAll();
    }
}
