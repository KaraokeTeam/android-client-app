package com.example.orpriesender.karaoke;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class PostListViewModel extends ViewModel {
    private LiveData<List<Post>> posts;

    public PostListViewModel(){
        posts = KaraokeRepository.getInstance().getAllPosts();
    }

    public LiveData<List<Post>> getAllPosts(){
        return posts;
    }
}
