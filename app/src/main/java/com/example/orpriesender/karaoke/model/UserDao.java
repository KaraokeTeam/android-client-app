package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Or Priesender on 08-Mar-18.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUser(String userId);

    @Insert
    void insertAll(List<User> users);

    @Insert
    void addUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Delete
    void deleteUser(User user);
}
