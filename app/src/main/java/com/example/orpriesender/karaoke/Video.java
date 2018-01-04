package com.example.orpriesender.karaoke;

import android.widget.VideoView;

/**
 * Created by Or Priesender on 04-Jan-18.
 */

public class Video {

    private VideoView view;
    private String name;
    private boolean playing;


    public Video(VideoView view, String videoName){
        this.view = view;
        this.name = videoName;
        playing = false;
    }



    public void playVideo(){
        if(playing)
            view.stopPlayback();
        view.setVideoPath("android.resource://com.example.orpriesender.karaoke/raw/" + name);
        view.start();
        playing = true;
    }

    public void stopVideo(){
        if(playing)
            view.stopPlayback();
    }
}
