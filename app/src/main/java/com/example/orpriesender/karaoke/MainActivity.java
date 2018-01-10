package com.example.orpriesender.karaoke;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends Activity {

    private Button record_button;
    private Button stop_button;
    private Button feed_button;
    private MediaRecorder recorder;
    private String outputFile;
    private TextView message;
    private Spinner songList;
    private VideoView videoView;
    private boolean recording = false;
    private boolean videoPlaying = false;
    private static final int RC_SIGN_IN = 123;
    private static final int INTERNET_PERMISSION = 111;
    private static final int STORAGE_PERMISSION = 222;
    private static final int RECORD_PERMISSION = 333;




    @Override
    protected void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record_button = (Button) findViewById(R.id.record);
        stop_button = (Button) findViewById(R.id.stop);
        message = (TextView) findViewById(R.id.message);
        feed_button = (Button) findViewById(R.id.feed_button);
        stop_button.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_button.3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(outputFile);

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                videoView = findViewById(R.id.video);
                videoView.setVideoPath("android.resource://com.example.orpriesender.karaoke/raw/zlil");
                videoView.start();
                //video.playVideo();
                videoPlaying = true;
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        recorder.stop();
                        recorder.release();
                    }
                });

                recorder.start();
                recording = true;
                record_button.setEnabled(false);
                stop_button.setEnabled(true);
                message.setText("Started Recording");

            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                //video.stopVideo();
                recorder.stop();
                recording = false;
                videoPlaying = false;
                recorder.release();
                recorder = null;
                stop_button.setEnabled(false);
                record_button.setEnabled(true);

                final ProgressBar pb = findViewById(R.id.main_progress_bar);
                pb.setVisibility(View.VISIBLE);
                WebServiceUtil util = WebServiceUtil.getInstance("http://10.0.0.6:5000/", getBaseContext());
                util.getGrade(outputFile, new onGradeResponseListener() {
                    @Override
                    public void onGradeResponse(Integer grade) {
                        Log.d("TAG", "got response :" + grade);
                        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("outputFile", outputFile);
                        intent.putExtra("grade", grade);
                        pb.setVisibility(View.GONE);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailureRespnonse(String code) {
                        onCreate(null);
                    }
                });

            }
        });

        feed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                startActivity(intent);
            }
        });

    }

    boolean askPermission(){
        int internet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int externalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if(internet != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);

        if(externalStorage != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION);

        if(internet != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_PERMISSION);




        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGood = true;
        switch(requestCode){
            case INTERNET_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    allGood = true;

                } else {

                    allGood = false;
                }
                break;
            case STORAGE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    allGood = true;

                } else {

                    allGood = false;
                }
                break;
            case RECORD_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    allGood = true;

                } else {

                    allGood = false;
                }
                break;
        }

        if(!allGood){
            System.exit(1);
        }
    }



}
