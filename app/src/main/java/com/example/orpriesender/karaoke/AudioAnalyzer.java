package com.example.orpriesender.karaoke;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Random;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

/**
 * Created by Or Priesender on 19-Jan-18.
 */

public class AudioAnalyzer {

    AudioDispatcher dispatcher;
    PitchDetectionHandler pitchHandler;
    OnsetHandler onsetHandler;
    File recordFile;
    boolean initialized, stopped;
    Thread audioThread;

    public AudioAnalyzer() {
        initialized = false;
        stopped = true;
    }

    public void init() {
        if (!initialized) {
            initialized = true;
            this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 2048, 1024);
            if (recordFile != null) {
                if(recordFile.exists())
                    recordFile.delete();
                TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                        44100,
                        16,
                        1,
                        2 * 1,
                        44100,
                       false);
                try {
                    RandomAccessFile raf = new RandomAccessFile(recordFile, "rw");
                    AudioProcessor recorder = new WriterProcessor(format, raf);
                    this.dispatcher.addAudioProcessor(recorder);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (pitchHandler != null) {
                this.dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 2048, this.pitchHandler));
            }
            if (onsetHandler != null) {
                ComplexOnsetDetector onSetDetector = new ComplexOnsetDetector(2048);
                onSetDetector.setHandler(onsetHandler);
                this.dispatcher.addAudioProcessor(onSetDetector);
            }

        }
    }

    public void setPitchHandler(PitchDetectionHandler handler) {
        this.pitchHandler = handler;
    }

    public void setOnsetHandler(OnsetHandler handler) {
        this.onsetHandler = handler;
    }

    public void setRecordFile(String filename) {
        recordFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename + ".wav");
        System.out.println("filename : " + recordFile.getName());
        if (recordFile.exists()) {
            recordFile.delete();
        }
    }

    public void start() {
        if (!stopped)
            this.stop();
        if (!initialized)
            this.init();
        this.stopped = false;
        this.audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    public void stop() {
        if (!this.stopped) {
            if (!dispatcher.isStopped()) {
                dispatcher.stop();
                try {
                    audioThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            initialized = false;
            stopped = true;
        }
    }


}
