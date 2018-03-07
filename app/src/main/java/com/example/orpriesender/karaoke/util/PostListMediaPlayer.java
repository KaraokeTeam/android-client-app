package com.example.orpriesender.karaoke.util;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class PostListMediaPlayer {
    private static PostListMediaPlayer instance = new PostListMediaPlayer();
    private MediaPlayer player;
    private onProgressListener onProgressListener;
    private onCompletionListener onCompletionListener;
    private File file;

    private PostListMediaPlayer() {
        this.player = new MediaPlayer();
    }

    public static PostListMediaPlayer getInstance() {
        return instance;
    }

    public void reset() {
        if (player != null) {
            player.reset();
        }
    }

    public void setOnCompletionListener(PostListMediaPlayer.onCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    public void setData(File file) {
        try {
            this.file = file;
            player.reset();
            player.setDataSource(file.getPath());
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnProgressListener(onProgressListener listener) {
        this.onProgressListener = listener;
    }

    public void start(int startProgress) {
        player.seekTo(startProgress);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onCompletionListener != null) {
                    stop();
                    onCompletionListener.onPlayingComplete();
                }
            }
        });
        handleProgress(startProgress);
    }

    public int getPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        } else return 0;
    }

    public void handleProgress(int startProgress) {
        final Handler handler = new Handler();
        onProgressListener.onProgress(startProgress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onProgressListener != null) {
                            if (player.isPlaying()) {
                                onProgressListener.onProgress(player.getCurrentPosition());
                                handler.postDelayed(this, 100);
                            }
                        }
                    }
                }, 100);
            }
        }).start();
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public int getSongDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    public interface onProgressListener {
        public void onProgress(int progress);
    }

    public interface onCompletionListener {
        public void onPlayingComplete();
    }


}
