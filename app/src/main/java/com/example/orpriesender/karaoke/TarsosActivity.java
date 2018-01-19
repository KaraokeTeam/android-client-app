package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by Or Priesender on 08-Jan-18.
 */

public class TarsosActivity extends Activity {

    TextView pitchText, noteText;
    List<Pitch> pitches = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tarsos_activity);
        pitchText = findViewById(R.id.pitch_text);
        noteText = findViewById(R.id.note_text);
        final Grader grader = new Grader(getApplicationContext(),"jinjit3_pitches.txt","jinjit3_onsets.txt");

        final PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(final PitchDetectionResult res, final AudioEvent e) {

                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(res.getPitch() != -1){
                            grader.insertPitch(new Pitch(pitchInHz,new Float(e.getTimeStamp()),new Float(e.getEndTimeStamp()),res.getProbability()));
                            pitchText.setText("" + res.getPitch());
                            noteText.setText(grader.getNoteFromHz(res.getPitch()).toString());
                        } else {
                            pitchText.setText("0.00");
                            noteText.setText("--");
                        }


                    }
                });
            }
        };

        final OnsetHandler onsetHandler = new OnsetHandler() {
            @Override
            public void handleOnset(double time, double silence) {
                //silence not used because aubio doesnt have it
               // grader.consumeOnset(new Onset(new Float(time)));
            }
        };

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 2048, 1024);
        Button b = findViewById(R.id.start_pitch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                grader.start();
                AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 2048, pitchDetectionHandler);

                ComplexOnsetDetector onSetDetector = new ComplexOnsetDetector(2048);
                onSetDetector.setHandler(onsetHandler);

                dispatcher.addAudioProcessor(onSetDetector);
                dispatcher.addAudioProcessor(pitchProcessor);


                Thread audioThread = new Thread(dispatcher, "Audio Thread");
                audioThread.start();
            }
        });

        Button stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dispatcher.isStopped())
                    dispatcher.stop();
                grader.getGrade();
            }
        });


    }



}


