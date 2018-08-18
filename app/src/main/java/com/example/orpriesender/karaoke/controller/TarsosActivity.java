package com.example.orpriesender.karaoke.controller;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.orpriesender.karaoke.file_readers.GroupWriter;
import com.example.orpriesender.karaoke.model.*;
import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.util.Util;
import com.example.orpriesender.karaoke.view_model.TarsosViewModel;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;


public class TarsosActivity extends FragmentActivity {

    public interface DataFetchCallback {
        public void onDataReady(boolean success);
    }

    TextView pitchText, noteText, countdownText,messageText;
    ImageButton stopListeningButton;
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
        dropdown = findViewById(R.id.tarsos_activity_dropdown);
        backButton = findViewById(R.id.tarsos_activity_back_button);
        countdownText = findViewById(R.id.tarsos_activity_countdown_text);
        pitchText = findViewById(R.id.tarsos_activity_pitch_text);
        noteText = findViewById(R.id.tarsos_activity_note_text);
        startListeningButton = findViewById(R.id.tarsos_activity_start_button);
        stopListeningButton = findViewById(R.id.tarsos_activity_stop);
        videoView = findViewById(R.id.tarsos_activity_video);
        messageText = findViewById(R.id.tarsos_activity_message_text);
        this.spinner = findViewById(R.id.tarsos_activity_spinner);

        //buttons are disabled by until all sources arrive
        startListeningButton.setEnabled(false);
        stopListeningButton.setEnabled(false);

        //init the dropdown list
        tarsosVM = ViewModelProviders.of(this).get(TarsosViewModel.class);
        tarsosVM.getSongsList().observe(this, new Observer<List<SongItem>>() {
            @Override
            public void onChanged(@Nullable List<SongItem> songItems) {
                final ArrayAdapter<SongItem> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.simple_spinner_item,songItems);
                dropdown.setAdapter(spinnerAdapter);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SongItem song = spinnerAdapter.getItem(position);
                        if(song.getSystemName() == null){
                            Log.d("TAG","system name is null");
                            return;
                        }
                        songName = song.getSystemName();
                        Log.d("TAG","system name is " + songName);
                        //create a grader with relevant sources
                        grader = new Grader(getApplicationContext(), song.getSystemName(), new Grader.NoteDistanceUpdates() {
                            @Override
                            public void onUpdate(final int distance) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        noteText.setTextColor(getColorFromDistance(distance));
                                    }
                                });

                            }
                        });
                        spinner.setVisibility(View.VISIBLE);
                        fetchSources(song.getSystemName(),new DataFetchCallback() {
                            @Override
                            public void onDataReady(boolean success) {
                                if (success) {
                                    startListeningButton.setEnabled(true);
                                    spinner.setVisibility(View.GONE);
                                    dropdown.setEnabled(false);
                                    if (videoPath != null) {
                                        videoView.setVideoPath(videoPath);
                                    }

                                } else {
                                    Util.presentToast(getApplicationContext(),TarsosActivity.this,"Failed downloading required sources", Toast.LENGTH_LONG);
                                }
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        //set a pitch handler
        final PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(final PitchDetectionResult res, final AudioEvent e) {
                final float pitchInHz = res.getPitch();
                insertPitch(grader,new Pitch(pitchInHz, new Float(e.getTimeStamp()), new Float(e.getEndTimeStamp()), res.getProbability()));
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
        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateStartListeningButton(0.0f);
                animateVideo(1.0f);
                startListeningButton.setEnabled(false);
                stopListeningButton.setEnabled(true);
                dropdown.setVisibility(View.GONE);
                startListening();
            }
        });

        //stop listening
        stopListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateVideo(0.0f);
                animateStartListeningButton(0.0f);
                pitchText.setVisibility(View.GONE);
                noteText.setVisibility(View.GONE);
                messageText.setText("Calculating grade ... ");
                messageText.setVisibility(View.VISIBLE);
                startListeningButton.setEnabled(true);
                stopListeningButton.setEnabled(false);
                stopListening();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
                finish();
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

    private void fetchSources(String filename,DataFetchCallback callback) {
        initGrader(callback);
        initVideo(filename,callback);
    }

    private void checkIfReady(final DataFetchCallback callback) {
        synchronized (this) {
            if (isVideoReady && isGraderReady) {
                if (videoView == null || grader == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataReady(false);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataReady(true);
                        }
                    });
                }
            }
        }
    }

    private int getColorFromDistance(int distance){
        distance = Math.abs(distance);
        if(distance  > 4) return ContextCompat.getColor(getApplicationContext(),R.color.red);
        else if(distance <= 4 && distance >= 2) return ContextCompat.getColor(getApplicationContext(),R.color.yellow);
        else return ContextCompat.getColor(getApplicationContext(),R.color.green);
    }

    private void initGrader(final DataFetchCallback callback) {
                grader.init(new Grader.InitCallback() {
                    @Override
                    public void onReady(boolean success) {
                        isGraderReady = true;
                        checkIfReady(callback);
                    }
                });
    }

    private void startListening() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoView.start();
            }
        }).start();
        analyzer.start();
        grader.start();
    }

    private void initVideo(String filename,final DataFetchCallback callback) {
//        TarsosViewModelFactory factory = new TarsosViewModelFactory("zlil", "mp4");
        //VM definition was here

        tarsosVM.getPlayback(filename).observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
                if (file != null) {
                    isVideoReady = true;
                    videoPath = file.getPath();
                    //check if the other source files arrived
                    checkIfReady(callback);
                }
            }
        });
        //in case we want to add media controls
        //        MediaController controller = new MediaController(this,false);
        //        controller.setAnchorView(videoView);
        //        videoView.setMediaController(controller);
    }

    private void insertPitch(final Grader grader, final Pitch pitch){
        grader.insertPitch(pitch);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pitch.getPitch() != -1) {
                    pitchText.setText("Pitch: " + pitch.getPitch());
                    noteText.setText("Note: " + grader.getNoteFromHz(pitch.getPitch()).getNote());
                } else {
                    pitchText.setText("0.00");
                    noteText.setText("--");
                }
            }
        });
    }

    private void stopListening() {
        double grade = 0;
        analyzer.stop();
        videoView.stopPlayback();
        if(grader != null){
            spinner.setVisibility(View.VISIBLE);
            grade = grader.getGrade2();
            spinner.setVisibility(View.GONE);
            final Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("grade", grade);
            intent.putExtra("performanceFileName", analyzer.getRecordFileName());
            intent.putExtra("song", songName);
            intent.putExtra("performanceFile", analyzer.getRecordFile());
            intent.putExtra("uid",FirebaseAuth.getInstance().getUid().toString());
            intent.putExtra("graphData",grader.getGraphData());
            //only uncomment this if you want to generate a new group json file
//            GroupWriter.writeToFile(new File(getFilesDir(),"groups.json"),grader.getJsonGroups(grader.getGroups(grader.getPerformancePitches())));
            startActivity(intent);
            finish();
        }

//        grader.getGrade(new Grader.GradeCallback() {
//            @Override
//            public void onGrade(final double grade) {
//                TarsosActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                        intent.putExtra("grade", grade);
//                        intent.putExtra("performanceFileName", analyzer.getRecordFileName());
//                        intent.putExtra("song", songName);
//                        intent.putExtra("performanceFile", analyzer.getRecordFile());
//                        Log.d("TAG","starting result activity");
//                        //change - pass through the uid and let the result activity get the user
//                        intent.putExtra("uid",FirebaseAuth.getInstance().getUid().toString());
//                        startActivity(intent);
//                        spinner.setVisibility(View.GONE);
//                        finish();
//                    }
//                });
//            }
//        });
        pitchText.setText("0.00");
        noteText.setText("--");
    }

//    public void loadFFMpegAndExecute(final String cmd) throws FFmpegNotSupportedException {
//        final FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
//        ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
//
//            @Override
//            public void onStart() {
//                Log.d("TAG","onStart loading executable");
//            }
//
//            @Override
//            public void onFinish() {
//                Log.d("TAG","onFinish loading executable");
//            }
//
//            @Override
//            public void onFailure() {
//                Log.d("TAG","onFailure loading executable");
//            }
//
//            @Override
//            public void onSuccess() {
//                Log.d("TAG","onSuccess loading executable");
//                String[] command = {cmd};
//                try {
//                    ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
//                        @Override
//                        public void onStart() {
//                            Log.d("TAG","onStart execute command");
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            Log.d("TAG","onFinish execute command");
//                        }
//
//                        @Override
//                        public void onSuccess(String message) {
//                            Log.d("TAG","onSuccess execute command " + message);
//                        }
//
//                        @Override
//                        public void onProgress(String message) {
//                            Log.d("TAG","onProgress execute command " + message);
//                        }
//
//                        @Override
//                        public void onFailure(String message) {
//                            Log.d("TAG","onFailure execute command " + message);
//                        }
//                    });
//                } catch (FFmpegCommandAlreadyRunningException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}



