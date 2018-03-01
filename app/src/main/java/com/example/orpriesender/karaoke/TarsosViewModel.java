package com.example.orpriesender.karaoke;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.io.File;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class TarsosViewModel extends ViewModel {
    private LiveData<File> data;

    public TarsosViewModel(String name,String extension){
        data = KaraokeRepository.getInstance().getPlayback(name,extension);
    }

    public LiveData<File> getPlayback(){
        return data;
    }
}
