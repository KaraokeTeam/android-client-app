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


    public void addPosts(List<Post> posts) {
        db.postDao().insertAll(posts);
    }

    public List<Post> getAllPosts() {
        return db.postDao().getAllPosts();
    }

    public void deleteAllPosts() {
        db.postDao().deleteAll();
    }

    public void updatePosts(List<Post> posts){
        db.postDao().deleteAll();
        db.postDao().insertAll(posts);
    }

    public List<SongItem> getSongsList() {
        return db.songItemDao().getSongsList();
    }

    public void updateSongsList(List<SongItem> songs){
        db.songItemDao().deleteAllSongs();
        db.songItemDao().insertAllSongs(songs);
    }
    public List<User> getAllUsers(){
        return db.userDao().getAllUsers();
    }

    public void updateOrAddUser(User user){
        User localUser = db.userDao().getUser(user.getId());
        if(localUser != null){
            db.userDao().deleteUser(user);
        }else db.userDao().addUser(user);
    }

    public void addUser(User user){
        db.userDao().addUser(user);
    }

    public void updateUsers(List<User> users){
        db.userDao().deleteAllUsers();
        db.userDao().insertAll(users);
    }

    public User getUser(String id){
        return db.userDao().getUser(id);
    }
}
