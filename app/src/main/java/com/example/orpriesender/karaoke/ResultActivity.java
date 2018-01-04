package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

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
    private boolean isPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        outputFile = intent.getStringExtra("outputFile");
        result = intent.getIntExtra("grade", 0);
        result_number = findViewById(R.id.result_number);
        result_number.setText(String.valueOf(result));
        play = findViewById(R.id.play);
        play.setEnabled(true);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        play.setImageResource(R.drawable.pause);
                        play.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
                        player = new MediaPlayer();
                        player.setDataSource(outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.prepare();
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            play.setEnabled(true);
                            play.setImageResource(R.drawable.play);
                            play.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
                        }
                    });
                    play.setEnabled(false);
                    Log.d("tag", "CLICKED !");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        back = findViewById(R.id.restart_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
