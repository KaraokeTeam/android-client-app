package com.example.orpriesender.karaoke.controller;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orpriesender.karaoke.model.FirebaseStorageManager;
import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.Post;
import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.model.User;
import com.example.orpriesender.karaoke.util.Util;
import com.example.orpriesender.karaoke.view_model.ResultViewModel;
import com.example.orpriesender.karaoke.view_model.ResultViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Or Priesender on 11-Dec-17.
 */

public class ResultActivity extends FragmentActivity {

    TextView result_number;
    MediaPlayer player;
    ImageButton play;
    ImageButton backToRecord, publish,backToFeed;
    ProgressBar spinner;
    private boolean isPlay = true;

    //members received from last activity
    double result;
    String performanceFileName, userId, username, songName;
    int performanceLength;
    File performanceFile;

    ResultViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_result);

        //get the information from the previous activity
        Intent intent = getIntent();
        performanceFile = (File) intent.getSerializableExtra("performanceFile");
        performanceFileName = intent.getStringExtra("performanceFileName");
        result = intent.getDoubleExtra("grade",0);
        userId = intent.getStringExtra("uid");
        songName = intent.getStringExtra("song");

        //get the username from the view model and disable the publish button
        publish = findViewById(R.id.activity_result_publish_button);
        publish.setEnabled(false);

        ResultViewModelFactory factory = new ResultViewModelFactory(FirebaseAuth.getInstance().getUid());
        vm = ViewModelProviders.of(this,factory).get(ResultViewModel.class);
        vm.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                publish.setEnabled(true);

                username = user.getUsername();
            }
        });

        //set the text according to the information received
        result_number = findViewById(R.id.result_number);
        NumberFormat format = new DecimalFormat("#0.00");
        result_number.setText(String.valueOf(format.format(result)));

        //configure the play button
        play = findViewById(R.id.activity_result_play);
        play.setEnabled(false);
        play.setBackgroundColor(getResources().getColor(R.color.fui_transparent));

        //configure the audio player
        try {
            player = new MediaPlayer();
            player.setDataSource(getApplicationContext(), Uri.fromFile(new File(getCacheDir(), performanceFileName)));
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play.setEnabled(true);
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play.setImageResource(R.drawable.play_btn_purple_big);
                    isPlay = true;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        //configure the play button click event
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    play.setImageResource(R.drawable.pause_btn_purple_big);
                    isPlay = false;
                    player.start();
                } else {
                    play.setImageResource(R.drawable.play_btn_purple_big);
                    isPlay = true;
                    player.pause();
                }
            }
        });
        //configure the back button
        backToRecord = findViewById(R.id.activity_result_restart_button);
        backToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TarsosActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //configure the publish button
        publish = findViewById(R.id.activity_result_publish_button);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //configure the pop up dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ResultActivity.this, R.style.alertDialogCustom));
                dialogBuilder.setTitle("Publish Performance");
                dialogBuilder.setMessage("Please insert content");

                //configure the inner text input
                final EditText input = new EditText(new ContextThemeWrapper(ResultActivity.this, R.style.alertDialogCustom));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialogBuilder.setView(input);


                //configure the dialog buttons
                dialogBuilder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Post p = new Post(userId, username, input.getText().toString(), songName,result);
                        KaraokeRepository.getInstance().addPost(p);

                        //start showing the progress bar and upload the performance
                        final ProgressBar progressBar = findViewById(R.id.activity_result_progress_bar);
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        KaraokeRepository.getInstance().uploadPerformance(p.getId(), performanceFile, new FirebaseStorageManager.FireBaseStorageUploadCallback() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot task) {
                                progressBar.setVisibility(View.GONE);
                                Util.presentToast(getApplicationContext(),ResultActivity.this,"Performance uploaded successfuly", Toast.LENGTH_SHORT);
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Util.presentToast(getApplicationContext(),ResultActivity.this,"Performance upload failed", Toast.LENGTH_SHORT);
                                finish();
                            }

                            @Override
                            public void onProgress(int progress) {
                                progressBar.setProgress(progress);
                            }
                        });
                    }
                });

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialogBuilder.show();
            }
        });
        backToFeed = findViewById(R.id.activity_result_feed_button);
        backToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ResultActivity.super.onBackPressed();
            }
        });
    }
}
