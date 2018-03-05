package com.example.orpriesender.karaoke.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts")
    List<Post> getAllPosts();

    @Query("SELECT * FROM posts WHERE userId LIKE :userId")
    List<Post> getPostsForUser(String userId);

    @Insert
    void insertAll(List<Post> posts);

    @Query("DELETE FROM posts")
    void deleteAll();

    @Delete
    void delete(Post post);
}
