package com.example.orpriesender.karaoke.model;

import android.content.Context;
import android.util.Log;

import java.time.LocalTime;
import java.util.List;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class RoomDatabaseManager {
    private static Context context;
    private static RoomDatabaseManager instance = new RoomDatabaseManager();
    private static MyRoomDatabase db;

    public static RoomDatabaseManager getInstance() {
        return instance;
    }

    public static void setContext(Context c) {
        context = c;
        init();
    }

    private static void init() {
        db = MyRoomDatabase.getInMemoryDatabase(context);

    }

    public void updateLocalTime() {
        if (db != null) {
            db.lastUpdatedDao().deleteLastUpdated("local");
            db.lastUpdatedDao().insertLastUpdated(new LastUpdated("local",System.currentTimeMillis()));

        }
    }

    public void updateRemoteTime() {
        if (db != null) {
            db.lastUpdatedDao().deleteLastUpdated("remote");
            db.lastUpdatedDao().insertLastUpdated(new LastUpdated("remote",System.currentTimeMillis()));
        }
    }

    public double getRemoteTime() {
        if (db != null) {
            LastUpdated remote = db.lastUpdatedDao().getRemote();
            if(remote != null){
                return remote.getLastUpdated();
            }

        }
        return 0;
    }

    public double getLocalTime() {
        if (db != null) {
            LastUpdated local = db.lastUpdatedDao().getLocal();
            if(local != null){
                return local.getLastUpdated();
            }
        }
        return -1;
    }

    //TODO : check if the list works, if not replace with Post...posts
    public void addPosts(List<Post> posts) {
        db.postDao().insertAll(posts);
    }

    public List<Post> getAllPosts() {
        return db.postDao().getAllPosts();
    }

    public void deleteAllPosts() {
        db.postDao().deleteAll();
    }

    //add check for times
    public void updatePosts(List<Post> posts){
        db.postDao().deleteAll();
        db.postDao().insertAll(posts);
    }

    public List<SongItem> getSongsList() {
        return db.songItemDao().getSongsList();
    }

    public void addSong(SongItem song) {
        db.songItemDao().insertSong(song);
    }
}
