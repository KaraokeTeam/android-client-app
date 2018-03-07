package com.example.orpriesender.karaoke.view_model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class TarsosViewModelFactory implements ViewModelProvider.Factory {
    String name, extension;

    public TarsosViewModelFactory(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TarsosViewModel.class)) {
            return (T) new TarsosViewModel();//name + extension
        }
        throw new IllegalArgumentException("TarsosViewModel illegal argument");
    }
}
