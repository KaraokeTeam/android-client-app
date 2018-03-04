package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Or Priesender on 11-Dec-17.
 */

public class ResultActivity extends Activity {

    TextView result_number;
    MediaPlayer player;
    ImageButton play;
    Button back,publish;
    private boolean isPlay = true;

    //members received from last activity
    Double result;
    String performanceFileName,userId,username,songName;
    File performanceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_result);

        //get the information from the previous activity
        Intent intent = getIntent();
        performanceFile = (File )intent.getSerializableExtra("performanceFile");
        performanceFileName = intent.getStringExtra("performanceFileName");
        result = intent.getDoubleExtra("grade", 0);
        userId = intent.getStringExtra("uid");
        username = intent.getStringExtra("username");
        songName = intent.getStringExtra("song");


        //set the text according to the information received
        result_number = findViewById(R.id.result_number);
        result_number.setText(String.valueOf(result));

        //configure the play button
        play = findViewById(R.id.play);
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
                    play.setImageResource(R.drawable.play);
                    play.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
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
                    play.setImageResource(R.drawable.pause);
                    isPlay = false;
                    player.start();
                } else {
                    play.setImageResource(R.drawable.play);
                    isPlay = true;
                    player.pause();
                }
            }
        });
        //configure the back button
        back = findViewById(R.id.activity_result_restart_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //configure the publish button
        publish = findViewById(R.id.activity_result_publish_button);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //configure the pop up dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ResultActivity.this);
                dialogBuilder.setTitle("Publish Performance");
                dialogBuilder.setMessage("Please insert content");

                //configure the inner text input
                final EditText input = new EditText(ResultActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialogBuilder.setView(input);

                //configure the dialog buttons
                dialogBuilder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Post p = new Post(userId,username,input.getText().toString(),songName);
                        KaraokeRepository.getInstance().addPost(p);
                        KaraokeRepository.getInstance().uploadPerformance(p.getId(),performanceFile);
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
    }
}
