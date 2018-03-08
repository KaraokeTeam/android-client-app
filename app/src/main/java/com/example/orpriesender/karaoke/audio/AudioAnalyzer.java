package com.example.orpriesender.karaoke.audio;

import android.net.Uri;
import com.example.orpriesender.karaoke.model.LocalCacheManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
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

//analyzing audio files
public class AudioAnalyzer {

    private AudioDispatcher dispatcher;
    private PitchDetectionHandler pitchHandler;
    private OnsetHandler onsetHandler;
    private File recordFile;
    private boolean initialized, stopped;
    private Thread audioThread;

    public AudioAnalyzer() {
        initialized = false;
        stopped = true;
    }

    public void init() {
        if (!initialized) {
            initialized = true;
            this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 2048, 0);
            if (recordFile != null) {
                if (recordFile.exists())
                    recordFile.delete();
                TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED,
                        44100,
                        2 * 8,
                        1,
                        2 * 1,
                        44100,
                        ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()));
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
        recordFile = LocalCacheManager.getInstance().saveOrUpdate(filename + ".wav");
                /*
                new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename + ".wav");
        if (recordFile.exists()) {
            recordFile.delete();
        }
                 */
    }

    public File getRecordFile() {
        return recordFile;
    }

    public String getRecordFileName() {
        return Uri.fromFile(recordFile).getLastPathSegment();
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
