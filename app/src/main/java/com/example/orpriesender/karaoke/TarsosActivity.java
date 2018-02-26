package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;


public class TarsosActivity extends Activity {

    TextView pitchText, noteText,countdownText;
    Button  stopListeningButton;
    ImageButton startListeningButton,backButton;
    VideoView video;
    Spinner dropdown;
    Grader grader;
    AudioAnalyzer analyzer;
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

//        //init the drop down list
//        String items[] = new String[]{"Eyal Golan - Zlil Meitar","Simple Do Re Mi"};
//
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,items);
//        dropdown.setAdapter(spinnerAdapter);


        //create a grader with relevant sources
        grader = new Grader(getApplicationContext(), "jinjit3");

        //set a pitch handler
        final PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(final PitchDetectionResult res, final AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.getPitch() != -1) {
                            grader.insertPitch(new Pitch(pitchInHz, new Float(e.getTimeStamp()), new Float(e.getEndTimeStamp()), res.getProbability()));
                            pitchText.setText("Pitch: " + res.getPitch());
                            noteText.setText("Note: " + grader.getNoteFromHz(res.getPitch()).getNote());
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

                startListeningButton.animate().alpha(0.0f).setDuration(500);
                video.animate().alpha(1.0f).setDuration(1000);
                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String nextText = "" + (Integer.parseInt(countdownText.getText().toString()) - 1);
                        stopListeningButton.setEnabled(false);
                        countdownText.setText(nextText);
                        countdownText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        countdownText.setVisibility(View.GONE);
                        countdownText.setText("3");
                        stopListeningButton.setEnabled(true);

                        boolean isListening = startListening();
                        if(!isListening){
                            //TODO: quit with error, or present error and reset
                        }
                    }
                };

                timer.start();
            }
        });

        //stop listening
        stopListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            double grade = stopListening();
                startListeningButton.animate().alpha(1.0f).setDuration(500);
                video.animate().alpha(0.0f).setDuration(1000);
            System.out.println("GRADE IS " + grade);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TarsosActivity.super.onBackPressed();
            }
        });
    }

    private boolean startListening(){
        boolean graderAvailable = grader.init();
        if(graderAvailable){
            grader.start();
            analyzer.start();
            return true;
        }else{
            analyzer.start();
            return false;
        }
    }

    private double stopListening(){
        analyzer.stop();
        double grade = grader.getGrade();
        pitchText.setText("0.00");
        noteText.setText("--");
        return grade;
    }

}



