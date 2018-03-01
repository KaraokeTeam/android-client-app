package com.example.orpriesender.karaoke;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;



public class TarsosActivity extends FragmentActivity {


    public interface DataFetchCallback{
        public void onDataReady(boolean success);
    }

    TextView pitchText, noteText,countdownText;
    Button  stopListeningButton;
    ImageButton startListeningButton,backButton;
    VideoView video;
    Spinner dropdown;
    Grader grader;
    AudioAnalyzer analyzer;
    ProgressBar spinner;
    String videoPath;
    boolean isGraderReady = false, isVideoReady = false;

    TarsosViewModel tarsosVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the screen view
        setContentView(R.layout.tarsos_activity);

        //get the needed view elements from the view
        //dropdown = findViewById(R.id.spinner);
        backButton = findViewById(R.id.tarsos_activity_back_button);
        countdownText = findViewById(R.id.tarsos_activity_countdown_text);
        pitchText = findViewById(R.id.tarsos_activity_pitch_text);
        noteText = findViewById(R.id.tarsos_activity_note_text);
        startListeningButton = findViewById(R.id.tarsos_activity_start_button);
        stopListeningButton = findViewById(R.id.tarsos_activity_stop);
        video = findViewById(R.id.tarsos_activity_video);
        this.spinner = findViewById(R.id.tarsos_activity_spinner);

//        //init the drop down list
//        String items[] = new String[]{"Eyal Golan - Zlil Meitar","Simple Do Re Mi"};
//
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,items);
//        dropdown.setAdapter(spinnerAdapter);


        //create a grader with relevant sources
        grader = new Grader(getApplicationContext(), "zlil");
        startListeningButton.setEnabled(false);
        stopListeningButton.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);


        fetchSources(new DataFetchCallback() {
            @Override
            public void onDataReady(boolean success) {
                if(success){
                    startListeningButton.setEnabled(true);
                    spinner.setVisibility(View.GONE);
                    if(videoPath != null){
                        video.setVideoPath(videoPath);
                    }
                }else{
                    //ignore
                }
            }
        });


        //set a pitch handler
        final PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(final PitchDetectionResult res, final AudioEvent e) {
                final float pitchInHz = res.getPitch();
                grader.insertPitch(new Pitch(pitchInHz, new Float(e.getTimeStamp()), new Float(e.getEndTimeStamp()), res.getProbability()));
                runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.getPitch() != -1) {
                                    pitchText.setText("Pitch: " + pitchInHz);
                                    noteText.setText("Note: " + grader.getNoteFromHz(pitchInHz).getNote());
                        } else {
                            pitchText.setText("0.00");
                            noteText.setText("--");
                        }
                    }
                });
            }
        };

        //set an onset handler
        final OnsetHandler onsetHandler = new OnsetHandler() {
            @Override
            public void handleOnset(double time, double silence) {
                System.out.println("ONSET : " + time);
                grader.consumeOnset(new Onset(new Float(time)));
            }
        };

        //create an audio analyzer
        analyzer = new AudioAnalyzer();

        //give the analyzer wanted handlers
        analyzer.setPitchHandler(pitchDetectionHandler);
        analyzer.setOnsetHandler(onsetHandler);
        analyzer.setRecordFile("realtime_record");
        analyzer.init();

        //start listening
        //activates a timer and starts listening to singer
        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateStartListeningButton(0.0f);
                animateVideo(1.0f);
//                CountDownTimer timer = new CountDownTimer(3000,1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        String nextText = "" + (Integer.parseInt(countdownText.getText().toString()) - 1);
//                        stopListeningButton.setEnabled(false);
//                        countdownText.setText(nextText);
//                        countdownText.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        countdownText.setVisibility(View.GONE);
//                        countdownText.setText("3");
//                        stopListeningButton.setEnabled(true);
//
//                        boolean isListening = startListening();
//                        if(!isListening){
//                            //TODO: quit with error, or present error and reset
//                        }
//                    }
//                };
//
//                timer.start();
                startListeningButton.setEnabled(false);
                stopListeningButton.setEnabled(true);
               startListening();
            }
        });

        //stop listening
        stopListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateVideo(0.0f);
                animateStartListeningButton(0.0f);
                startListeningButton.setEnabled(true);
                stopListeningButton.setEnabled(false);
                stopListening();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TarsosActivity.super.onBackPressed();
            }
        });
    }

    private void animateVideo(final float opacity){
        TarsosActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                video.animate().alpha(opacity).setDuration(1000);
            }
        });
    }

    private void animateStartListeningButton(final float opacity){
        TarsosActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startListeningButton.animate().alpha(opacity).setDuration(1000);
            }
        });
    }

    private void fetchSources(DataFetchCallback callback){
        initGrader(callback);
        initVideo(callback);
    }

    private void checkIfReady(DataFetchCallback callback){
        synchronized (this){
            if(isVideoReady && isGraderReady){
                 if(video == null || grader == null){
                        callback.onDataReady(false);
                }else{
                     callback.onDataReady(true);
                 }
            }
        }
    }

    private void initGrader(final DataFetchCallback callback){
        grader.init(new Grader.InitCallback() {
            @Override
            public void onReady(boolean success) {
                isGraderReady = true;
                if(!success){
                    checkIfReady(callback);
                }else{
                    Log.d("LOG","GRADER SUCCESS");
                    checkIfReady(callback);
                }
            }
        });
    }

    private void startListening(){
        Log.d("LOG","START LISTENING");
        video.start();
        analyzer.start();
        grader.start();
    }

    private void initVideo(final DataFetchCallback callback){
        TarsosViewModelFactory factory = new TarsosViewModelFactory("zlil","mp4");
        tarsosVM = ViewModelProviders.of(this,factory).get(TarsosViewModel.class);
        tarsosVM.getPlayback().observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
                isVideoReady = true;
                videoPath = file.getPath();
                checkIfReady(callback);
            }
        });
    }

    private void presentToast(final String text, final int length){
        TarsosActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), text, length);
                toast.show();
            }
        });
    }



    private void stopListening(){
        analyzer.stop();
        video.stopPlayback();
        spinner.setVisibility(View.VISIBLE);
        grader.getGrade(new Grader.GradeCallback() {
            @Override
            public void onGrade(double grade) {
                TarsosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                    }
                });
                Log.d("LOG","grade : " + grade);
            }
        });
        pitchText.setText("0.00");
        noteText.setText("--");
    }

}



