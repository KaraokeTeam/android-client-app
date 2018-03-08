package com.example.orpriesender.karaoke.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.controller.PostListFragment;
import com.example.orpriesender.karaoke.util.PostListMediaPlayer;

import java.io.File;
import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Post> posts;
    PostListFragment.onUsernameClicked onUsernameClickedListener;
    PostListFragment.onPlayClicked onPlayClickedListener;
    PostListFragment.onDownloadFinished onDownloadFinishedListener;

    private boolean isPlaying;

    public FeedAdapter(Activity activity, List<Post> posts) {
        this.posts = posts;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isPlaying = false;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
//        String postId = posts.get(position).getId();
//        long id = Long.parseLong(postId);
        return 0;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //if this list item is empty - inflate it
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feed_item_layout, null);
        }

        //ImageView image = convertView.findViewById(R.id.user_image);
        TextView username = convertView.findViewById(R.id.username);
        TextView description = convertView.findViewById(R.id.description);
        TextView time =  convertView.findViewById(R.id.time);
        final SeekBar seekBar = convertView.findViewById(R.id.seek_bar);
        final ImageButton playPause =  convertView.findViewById(R.id.play_pause_button);
        final ProgressBar spinner =  convertView.findViewById(R.id.feed_item_spinner);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    posts.get(position).setAudioPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //pause the audio if it plays
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //start the audio if it is paused
            }
        });

        if (onUsernameClickedListener != null) {
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUsernameClickedListener.onUsernameClicked(posts.get(position).getUserId());
                }
            });
        }

        if (onPlayClickedListener != null) {
            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posts.get(position).getPerformanceFile() == null) { //performance not yet downloaded
                        spinner.setVisibility(View.VISIBLE);
                        playPause.setVisibility(View.GONE);
                        onPlayClickedListener.onPlayClicked(posts.get(position).getId(), new PostListFragment.onDownloadFinished() {
                            @Override
                            public void onDownloadFinished(File file) {
                                posts.get(position).setPerformanceFile(file);
                                playPause.setImageResource(R.drawable.pause);
                                playPause.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                                PostListMediaPlayer.getInstance().setData(file);
                                seekBar.setMax(PostListMediaPlayer.getInstance().getSongDuration());
                                seekBar.setProgress(posts.get(position).getAudioPosition());
                                isPlaying = true;
                                PostListMediaPlayer.getInstance().setOnProgressListener(new PostListMediaPlayer.onProgressListener() {
                                    @Override
                                    public void onProgress(int progress) {
                                        posts.get(position).setAudioPosition(progress);
                                        seekBar.setProgress(progress);
                                    }
                                });
                                PostListMediaPlayer.getInstance().setOnCompletionListener(new PostListMediaPlayer.onCompletionListener() {
                                    @Override
                                    public void onPlayingComplete() {
                                        playPause.setImageResource(R.drawable.play);
                                        seekBar.setProgress(0);
                                        posts.get(position).setAudioPosition(0);
                                        PostListMediaPlayer.getInstance().reset();
                                        isPlaying = false;
                                        seekBar.setEnabled(true);
                                    }
                                });
                                PostListMediaPlayer.getInstance().start(posts.get(position).getAudioPosition());
                                seekBar.setEnabled(false);
                            }//end on download finished
                        });
                    } else {//performance already downloaded
                        if (isPlaying) { //on press pause
                            seekBar.setEnabled(true);
                            playPause.setImageResource(R.drawable.play);

                            PostListMediaPlayer.getInstance().pause();

                            int currentPosition = PostListMediaPlayer.getInstance().getPosition();
                            posts.get(position).setAudioPosition(currentPosition);
                            seekBar.setProgress(currentPosition);

                            isPlaying = false;

                        } else { //on press play and performance was downloaded
                            PostListMediaPlayer.getInstance().setOnProgressListener(new PostListMediaPlayer.onProgressListener() {
                                @Override
                                public void onProgress(int progress) {
                                    posts.get(position).setAudioPosition(progress);
                                    seekBar.setProgress(progress);
                                }
                            });
                            PostListMediaPlayer.getInstance().setOnCompletionListener(new PostListMediaPlayer.onCompletionListener() {
                                @Override
                                public void onPlayingComplete() {
                                    playPause.setImageResource(R.drawable.play);
                                    seekBar.setProgress(0);
                                    posts.get(position).setAudioPosition(0);
                                    PostListMediaPlayer.getInstance().reset();
                                    isPlaying = false;
                                    seekBar.setEnabled(true);
                                }
                            });
                            playPause.setImageResource(R.drawable.pause);
                            isPlaying = true;
                            seekBar.setEnabled(false);
                            seekBar.setProgress(posts.get(position).getAudioPosition());
                            PostListMediaPlayer.getInstance().setData(posts.get(position).getPerformanceFile());
                            PostListMediaPlayer.getInstance().start(posts.get(position).getAudioPosition());
                        }
                    }
                }
            });
        }


        convertView.setTag(position);
        username.setText(posts.get(position).getUsername());
        description.setText(posts.get(position).getDescription());
        time.setText(posts.get(position).getTime());
        seekBar.setProgress(posts.get(position).getAudioPosition());

        return convertView;
    }

    public void setOnUsernameClickListener(PostListFragment.onUsernameClicked listener) {
        this.onUsernameClickedListener = listener;
    }

    public void setOnDownloadFinishedListener(PostListFragment.onDownloadFinished listener) {
        this.onDownloadFinishedListener = listener;
    }

    public void setOnPlayClickedListener(PostListFragment.onPlayClicked listener) {
        this.onPlayClickedListener = listener;
    }

}
