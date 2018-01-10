package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by Or Priesender on 08-Jan-18.
 */

public class TarsosActivity extends Activity{

    TextView pitchText,noteText;
    List<Pitch> pitches = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tarsos_activity);
        pitchText = findViewById(R.id.pitch_text);
        noteText = findViewById(R.id.note_text);

        final PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){

                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };


        Button b = findViewById(R.id.start_pitch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
                AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
                dispatcher.addAudioProcessor(pitchProcessor);
                Thread audioThread = new Thread(dispatcher, "Audio Thread");
                audioThread.start();
            }
        });


    }

    public void processPitch(float pitchInHz) {



//        pitchText.setText("" + pitchInHz);
//
//        if(pitchInHz >= 110 && pitchInHz < 123.47) {
//            //A
//            noteText.setText("A");
//        }
//        else if(pitchInHz >= 123.47 && pitchInHz < 130.81) {
//            //B
//            noteText.setText("B");
//        }
//        else if(pitchInHz >= 130.81 && pitchInHz < 146.83) {
//            //C
//            noteText.setText("C");
//        }
//        else if(pitchInHz >= 146.83 && pitchInHz < 164.81) {
//            //D
//            noteText.setText("D");
//        }
//        else if(pitchInHz >= 164.81 && pitchInHz <= 174.61) {
//            //E
//            noteText.setText("E");
//        }
//        else if(pitchInHz >= 174.61 && pitchInHz < 185) {
//            //F
//            noteText.setText("F");
//        }
//        else if(pitchInHz >= 185 && pitchInHz < 196) {
//            //G
//            noteText.setText("G");
//        }
    }
}


