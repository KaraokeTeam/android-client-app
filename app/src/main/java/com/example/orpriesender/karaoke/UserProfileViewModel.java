package com.example.orpriesender.karaoke;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class UserProfileViewModel extends ViewModel {
    private LiveData<User> data;

    public UserProfileViewModel(String userId){
        data = KaraokeRepository.getInstance().getUser(userId);
    }

    public LiveData<User> getUser(){
        return data;
    }
}
