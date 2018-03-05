package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

@Database(entities = {Post.class,LastUpdated.class}, version = 1)
public abstract class MyRoomDatabase extends RoomDatabase {
    public abstract PostDao postDao();
    public abstract LastUpdatedDao lastUpdatedDao();
}
