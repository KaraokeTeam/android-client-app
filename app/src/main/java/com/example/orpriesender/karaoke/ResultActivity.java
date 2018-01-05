package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.IOException;

/**
 * Created by Or Priesender on 11-Dec-17.
 */

public class ResultActivity extends Activity {
    Integer result;
    String outputFile;
    TextView result_number;
    MediaPlayer player;
    ImageButton play;
    Button back;
    private boolean isPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_result);
        //get the information from the previous activity
        Intent intent = getIntent();
        outputFile = intent.getStringExtra("outputFile");
        result = intent.getIntExtra("grade", 0);
        //set the text according to the information received
        result_number = findViewById(R.id.result_number);
        result_number.setText(String.valueOf(result));
        //configure the play button
        play = findViewById(R.id.play);
        play.setEnabled(true);
        play.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
        //configure the audio player
        try {
            player = new MediaPlayer();
            player.setDataSource(outputFile);
            player.prepare();
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
        back = findViewById(R.id.restart_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
