package com.example.orpriesender.karaoke.view_model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.xml.transform.Result;

/**
 * Created by Or Priesender on 19-Mar-18.
 */

public class ResultViewModelFactory implements ViewModelProvider.Factory {

        private String userId;

        public ResultViewModelFactory(String userId){
            this.userId = userId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ResultViewModel.class)) {
                return (T) new ResultViewModel(userId);
            }
            throw new IllegalArgumentException("ResultViewModel illegal argument");
        }

}
