package com.example.orpriesender.karaoke;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by aboud on 1/10/2018.
 */

public class Grader {

    private List<Pitch> sourcePitches;
    private List<Onset> sourceOnsets;
    private List<Pitch> performancePitches;
    private List<Onset> performanceOnsets;
    //the application context
    private Context context;
    //notes table
    private Map<String, List<Double>> notes;
    //offset to start from in each consume
    private int currentOffset;
    //number of current mistakes
    private int mistakes;
    //queue to put all pitches in - then consume them from a different thread
    private ArrayBlockingQueue<Pitch> queue;
    //flag to keep the thread running
    private boolean keepGoing;
    //the thread
    private Thread thread;

    public Grader(Context context, String sourcePitchFile, String sourceOnsetFile) {
        this.context = context;
        this.init();
        try {
            this.notes = getNotesMapFromJson();
            //extracts the file from the assets folder and gives it to the pitch and onset readers
            this.sourcePitches = PitchReader.readPitchesFromFile(loadFileToStorage(context.getAssets().open(sourcePitchFile), "sourcePitch"));
            this.sourceOnsets = OnsetReader.readOnsetsFromFile(loadFileToStorage(context.getAssets().open(sourceOnsetFile), "sourceOnset"));
            this.getNotesMapFromJson();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        this.currentOffset = 0;
        this.performanceOnsets = new LinkedList<>();
        this.performancePitches = new LinkedList<>();
        this.mistakes = 0;
        this.keepGoing  = true;
    }

    private Map<String, List<Double>> getNotesMapFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, List<Double>> map =
                mapper.readValue(loadFileToStorage(context.getAssets().open("NotesToHz.json"), "notestohz.json"), HashMap.class);
        return map;
    }

    public Note getNoteFromHz(float pitch) {
        List<String> noteArr = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
        String closestNote = "";
        int noteIndex = -1;
        int closestOctave = -1;
        double diff = 9999;
        for (String s : notes.keySet()) {
            List<Double> octaves = notes.get(s);
            for (int i = 0; i < octaves.size(); i++) {
                if (diff > Math.abs(pitch - octaves.get(i))) {
                    diff = Math.abs(pitch - octaves.get(i));
                    closestNote = s;
                    closestOctave = i;
                    noteIndex = noteArr.indexOf(s);
                }
            }
        }

        //if the pitch is right on the note return
        if (diff == 0)
            return new Note(closestNote, closestOctave, diff);
        else {
            double below = notes.get(noteArr.get(noteIndex)).get(closestOctave);
            double above;
            //if the difference is positive, get the note above
            if (diff > 0){
                above = notes.get(noteArr.get((noteIndex + 1) % 12)).get(closestOctave % 8);
            }
            //if the difference is negative, get the note below
            else {
                above = notes.get(noteArr.get((noteIndex - 1) % 12)).get(closestOctave % 8);
            }
            double error = (diff / (below - above));
            return new Note(closestNote, closestOctave, error);
        }

    }

    public void start(){
        this.init();
        queue = new ArrayBlockingQueue<>(20);
        this.keepGoing = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(keepGoing || !queue.isEmpty()){
                    try {
                        Pitch p = queue.poll(1,TimeUnit.SECONDS);
                        if(p != null){
                            consumePitch(p);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                }

        });
        thread.start();
    }

    public void stop(){
        try {
            keepGoing = false;
            if(thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void insertPitch(Pitch p){
        if(p != null){
            try {
                queue.put(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //save the current given onset and analyze it
    public void consumeOnset(Onset onset) {
        this.performanceOnsets.add(onset);
    }

    //save the current given pitch and analyze it
    public void consumePitch(Pitch pitch) {
        boolean correct = false;
        if(pitch.getPitch() == -1)
            return;
        this.performancePitches.add(pitch);
        for (int i = currentOffset; i < sourcePitches.size(); i++) {
            Pitch sourcePitch = sourcePitches.get(i);
            Note given = getNoteFromHz(pitch.getPitch());
            Note source = getNoteFromHz(sourcePitch.getPitch());
            if (given.equals(source) && (Math.abs(pitch.getStart() - sourcePitch.getStart()) < 0.1)) {
                currentOffset = i;
                correct = true;
                break;
            }

        }
        if (!correct) {
            mistakes++;
        }
    }

    private File loadFileToStorage(InputStream input, String name) throws IOException {
        FileOutputStream fos = null;
        File file = new File(context.getFilesDir(), "/" + name);
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while ((nbread = input.read(data)) > -1) {
                fos.write(data, 0, nbread);
            }
            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return null;
    }

    public void printSourcePitches() {
        if (this.sourcePitches != null) {
            for (Pitch p : sourcePitches) {
                Log.d("SOURCES", p.toString());
            }
        }
    }

    public void printSourceOnsets() {
        if (this.sourceOnsets != null) {
            for (Onset o : sourceOnsets) {
                Log.d("SOURCES", o.toString());

            }
        }
    }

    public double getGrade() {
        this.stop();
        double mistakePercent = (((double) mistakes) /((double) performancePitches.size()));
        return 100 - (100 * mistakePercent);
    }
}
