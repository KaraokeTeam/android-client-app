package com.example.orpriesender.karaoke.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.SongItem;

import java.io.File;
import java.util.List;

/**
 * Created by Or Priesender on 28-Feb-18.
 */

public class TarsosViewModel extends ViewModel {
    private LiveData<File> playback;
    private LiveData<List<SongItem>> songs;

    public TarsosViewModel() {
        songs = KaraokeRepository.getInstance().getSongsList();
    }

    public LiveData<File> getPlayback(String filename) {
        this.playback = KaraokeRepository.getInstance().getPlayback(filename);
        return playback;
    }

    public LiveData<List<SongItem>> getSongsList(){
        return songs;
    }
}
