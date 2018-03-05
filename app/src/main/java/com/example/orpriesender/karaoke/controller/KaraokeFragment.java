package com.example.orpriesender.karaoke.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.example.orpriesender.karaoke.R;

/**
 * Created by Or Priesender on 04-Jan-18.
 */

public class KaraokeFragment extends Fragment {

    VideoView videoView;
    ViewGroup parent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_karaoke, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView = getView().findViewById(R.id.tarsos_activity_video);
        videoView.setVideoPath("android.resource://com.example.orpriesender.karaoke/raw/videofilenamewithoutextension");
        videoView.start();
    }
}
