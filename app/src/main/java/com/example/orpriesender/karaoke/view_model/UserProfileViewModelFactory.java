package com.example.orpriesender.karaoke.view_model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class UserProfileViewModelFactory implements ViewModelProvider.Factory {

    String userId;

    public UserProfileViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(UserProfileViewModel.class)) {
            return (T) new UserProfileViewModel(userId);
        }
        throw new IllegalArgumentException("UserProfileViewModel illegal argument");
    }
}
