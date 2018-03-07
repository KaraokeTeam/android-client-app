package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

@Dao
public interface LastUpdatedDao {

    @Query("SELECT * FROM lastUpdated WHERE name like 'local'")
    LastUpdated getLocal();

    @Query("SELECT * FROM lastUpdated WHERE name like 'remote'")
    LastUpdated getRemote();

    @Insert
    void insertLastUpdated(LastUpdated lastUpdated);

    @Query("DELETE FROM lastUpdated WHERE name LIKE :id")
    void deleteLastUpdated(String id);


}
