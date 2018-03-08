package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

@Database(entities = {Post.class,SongItem.class,User.class}, version = 1)
public abstract class MyRoomDatabase extends RoomDatabase {
    public abstract PostDao postDao();
    public abstract SongItemDao songItemDao();
    public abstract UserDao userDao();

    private static MyRoomDatabase instance;

    public static MyRoomDatabase getInMemoryDatabase(Context context){
        if(instance == null){
            instance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),MyRoomDatabase.class).allowMainThreadQueries().build();
        }
        return instance;
    }

    public static void destroyInstance(){
        instance = null;
    }
}
