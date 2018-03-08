package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Or Priesender on 07-Mar-18.
 */

@Dao
public interface SongItemDao {

    @Query("SELECT * FROM songs")
    List<SongItem> getSongsList();

    @Insert
    void insertSong(SongItem song);

    @Insert
    void insertAllSongs(List<SongItem> songs);

    @Query("DELETE FROM songs")
    void deleteAllSongs();
}
