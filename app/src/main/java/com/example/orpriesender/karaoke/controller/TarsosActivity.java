package com.example.orpriesender.karaoke.controller;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.orpriesender.karaoke.audio.AudioAnalyzer;
import com.example.orpriesender.karaoke.audio.Grader;
import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.Onset;
import com.example.orpriesender.karaoke.model.Pitch;
import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.util.Util;
import com.example.orpriesender.karaoke.view_model.TarsosViewModel;
import com.example.orpriesender.karaoke.view_model.TarsosViewModelFactory;
import com.example.orpriesender.karaoke.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;


public class TarsosActivity extends FragmentActivity {


    public interface DataFetchCallback {
        public void onDataReady(boolean success);
    }

    TextView pitchText, noteText, countdownText;
    Button stopListeningButton;
    ImageButton startListeningButton, backButton;
    VideoView videoView;
    Spinner dropdown;
    Grader grader;
    AudioAnalyzer analyzer;
    ProgressBar spinner;
    String videoPath;
    boolean isGraderReady = false, isVideoReady = false;
    String songName;

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
        videoView = findViewById(R.id.tarsos_activity_video);
        this.spinner = findViewById(R.id.tarsos_activity_spinner);
//
//        //init the drop down list
//        String items[] = new String[]{"Eyal Golan - Zlil Meitar","Simple Do Re Mi"};
//
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,items);
//        dropdown.setAdapter(spinnerAdapter);
//

        //create a grader with relevant sources
        this.songName = "zlil";
        grader = new Grader(getApplicationContext(), songName);
        startListeningButton.setEnabled(false);
        stopListeningButton.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);


        fetchSources(new DataFetchCallback() {
            @Override
            public void onDataReady(boolean success) {
                if (success) {
                    startListeningButton.setEnabled(true);
                    spinner.setVisibility(View.GONE);
                    if (videoPath != null) {
                        videoView.setVideoPath(videoPath);
                    }
                } else {
                    Util.presentToast(getApplicationContext(),TarsosActivity.this,"Failed downloading required sources", Toast.LENGTH_LONG);
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
                grader.consumeOnset(new Onset(new Float(time)));
            }
        };

        //create an audio analyzer
        analyzer = new AudioAnalyzer();

        //give the analyzer wanted handlers
        analyzer.setPitchHandler(pitchDetectionHandler);
        analyzer.setOnsetHandler(onsetHandler);
        analyzer.setRecordFile(songName);
        analyzer.init();

        //start listening
        //activates a timer and starts listening to singer
        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateStartListeningButton(0.0f);
                animateVideo(1.0f);
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

    private void animateVideo(final float opacity) {
        TarsosActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoView.animate().alpha(opacity).setDuration(1000);
            }
        });
    }

    private void animateStartListeningButton(final float opacity) {
        TarsosActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startListeningButton.animate().alpha(opacity).setDuration(1000);
            }
        });
    }

    private void fetchSources(DataFetchCallback callback) {
        initGrader(callback);
        initVideo(callback);
    }

    private void checkIfReady(DataFetchCallback callback) {
        synchronized (this) {
            if (isVideoReady && isGraderReady) {
                if (videoView == null || grader == null) {
                    callback.onDataReady(false);
                } else {
                    callback.onDataReady(true);
                }
            }
        }
    }

    private void initGrader(final DataFetchCallback callback) {
        grader.init(new Grader.InitCallback() {
            @Override
            public void onReady(boolean success) {
                isGraderReady = true;
                if (!success) {
                    checkIfReady(callback);
                } else {
                    checkIfReady(callback);
                }
            }
        });
    }

    private void startListening() {
        Log.d("TAG", "START LISTENING");
        videoView.start();
        analyzer.start();
        grader.start();
    }

    private void initVideo(final DataFetchCallback callback) {
        TarsosViewModelFactory factory = new TarsosViewModelFactory("zlil", "mp4");
        tarsosVM = ViewModelProviders.of(this, factory).get(TarsosViewModel.class);
        tarsosVM.getPlayback().observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
                if (file != null) {
                    isVideoReady = true;
                    videoPath = file.getPath();
                    checkIfReady(callback);
                }
            }
        });
        //in case we want to add media controls
        //        MediaController controller = new MediaController(this,false);
        //        controller.setAnchorView(videoView);
        //        videoView.setMediaController(controller);
    }

    private void stopListening() {
        analyzer.stop();
        videoView.stopPlayback();
        spinner.setVisibility(View.VISIBLE);
        grader.getGrade(new Grader.GradeCallback() {
            @Override
            public void onGrade(final double grade) {
                TarsosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                        final Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("grade", grade);
                        intent.putExtra("performanceFileName", analyzer.getRecordFileName());
                        intent.putExtra("song", songName);
                        intent.putExtra("performanceFile", analyzer.getRecordFile());
                        KaraokeRepository.getInstance().getUser(FirebaseAuth.getInstance().getUid()).observe(TarsosActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(@Nullable User user) {
                                intent.putExtra("uid", user.getId());
                                intent.putExtra("username", user.getUsername());
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                });
                Log.d("LOG", "grade : " + grade);
            }
        });
        pitchText.setText("0.00");
        noteText.setText("--");
    }

}



