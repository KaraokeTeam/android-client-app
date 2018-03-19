package com.example.orpriesender.karaoke.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.User;

/**
 * Created by Or Priesender on 19-Mar-18.
 */

public class ResultViewModel extends ViewModel {

    private LiveData<User> user;

    public ResultViewModel(String userId){
        user = KaraokeRepository.getInstance().getUser(userId);
    }

    public LiveData<User> getUser(){
        return user;
    }
}
