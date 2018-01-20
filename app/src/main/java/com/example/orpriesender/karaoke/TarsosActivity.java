package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;


public class TarsosActivity extends Activity {

    TextView pitchText, noteText;
    Button startListeningButton, stopListeningButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the screen view
        setContentView(R.layout.tarsos_activity);

        //get the needed view elements from the view
        pitchText = findViewById(R.id.pitch_text);
        noteText = findViewById(R.id.note_text);
        startListeningButton = findViewById(R.id.start_pitch);
        stopListeningButton = findViewById(R.id.stop);

        //create a grader with relevant sources
        final Grader grader = new Grader(getApplicationContext(), "jinjit3_pitches.txt", "jinjit3_onsets.txt");

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
                            pitchText.setText("" + res.getPitch());
                            noteText.setText(grader.getNoteFromHz(res.getPitch()).getNote());
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
        final AudioAnalyzer analyzer = new AudioAnalyzer();

        //give the analyzer wanted handlers
        analyzer.setPitchHandler(pitchDetectionHandler);
        analyzer.setOnsetHandler(onsetHandler);
        analyzer.setRecordFile("realtime_record");
        analyzer.init();

        //start listening
        startListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grader.start();
                analyzer.start();
            }
        });

        //stop listening
        stopListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzer.stop();
                double grade = grader.getGrade();
                grader.stop();
                pitchText.setText("0.00");
                noteText.setText("--");
                Log.d("GRADE", "" + grade);
            }
        });
    }

}



