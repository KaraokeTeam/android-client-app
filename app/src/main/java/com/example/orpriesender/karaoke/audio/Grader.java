package com.example.orpriesender.karaoke.audio;

import android.content.Context;
import android.util.Log;

import com.example.orpriesender.karaoke.model.FirebaseStorageManager;
import com.example.orpriesender.karaoke.model.Group;
import com.example.orpriesender.karaoke.file_readers.GroupReader;
import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.Note;
import com.example.orpriesender.karaoke.model.Onset;
import com.example.orpriesender.karaoke.file_readers.OnsetReader;
import com.example.orpriesender.karaoke.model.Pitch;
import com.example.orpriesender.karaoke.file_readers.PitchReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Created by aboud on 1/10/2018.
 */

//grading a performance according to source files
public class Grader {

    public interface InitCallback {
        void onReady(boolean success);
    }

    public interface GradeCallback {
        void onGrade(double grade);
    }

    private List<Pitch> sourcePitches = null;
    private List<Onset> sourceOnsets = null;
    private List<Pitch> performancePitches;
    private List<Onset> performanceOnsets;
    private int iterator;
    private double grade;

    //the application context
    private Context context;
    //notes table
    private Map<String, List<Double>> notes;
    //offset to start from in each consume
    private int currentOffset;
    //number of current mistakes
    private double mistakes;
    //queue to put all pitches in - then consume them from a different thread
    private ArrayBlockingQueue<Pitch> queue;
    //flag to keep the thread running
    private boolean keepGoing;
    //the thread
    private Thread thread;
    private boolean errorMode = false;
    private String songName;
    private List<Group> groups;

    private boolean onsetsCompleted = false, pitchesCompleted = false, groupsCompleted = false;

    public Grader(Context context, String songName) {
        this.context = context;
        this.songName = songName;
        try {
            this.notes = getNotesMapFromJson();

            //extracts the file from the assets folder and gives it to the pitch and onset readers
            //this.sourcePitches = PitchReader.readPitchesFromFile(loadFileToStorage(context.getAssets().open(sourcePitchFile), "sourcePitch"));
            //this.sourceOnsets = OnsetReader.readOnsetsFromFile(loadFileToStorage(context.getAssets().open(sourceOnsetFile), "sourceOnset"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkIfReady(InitCallback callback) {
        synchronized (this) {
            if (onsetsCompleted && pitchesCompleted && groupsCompleted) {
                if (sourceOnsets == null || sourcePitches == null || groups == null) {
                    callback.onReady(false);
                } else {
                    callback.onReady(true);
                }
            }
        }

    }

    public void init(final InitCallback callback) {
        this.currentOffset = 0;
        this.performanceOnsets = new LinkedList<>();
        this.performancePitches = new LinkedList<>();
        this.mistakes = 0;
        this.keepGoing = true;
        this.grade=0;
        this.iterator = 0;

        if (!onsetsCompleted) {
            KaraokeRepository.getInstance().getSourceOnsetFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                    sourceOnsets = OnsetReader.readOnsetsFromFile(localFile);
                    onsetsCompleted = true;
                    checkIfReady(callback);
                }

                @Override
                public void onFailure(Exception e) {
                    errorMode = true;
                    onsetsCompleted = true;
                    checkIfReady(callback);

                }
            });
        }

        if (!pitchesCompleted) {
            KaraokeRepository.getInstance().getSourcePitchFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                    sourcePitches = PitchReader.readPitchesFromFile(localFile);
                    pitchesCompleted = true;
                    checkIfReady(callback);
                }

                @Override
                public void onFailure(Exception e) {
                    errorMode = true;
                    pitchesCompleted = true;
                    checkIfReady(callback);
                }
            });
        }


        if (!groupsCompleted) {
            KaraokeRepository.getInstance().getGroupsForSong(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                    groups = GroupReader.readGroupsFromFile(localFile);
                    groupsCompleted = true;
                    checkIfReady(callback);
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    groupsCompleted = true;
                    checkIfReady(callback);
                }
            });
        }
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
            if (diff > 0) {
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

    public void start() {
        queue = new ArrayBlockingQueue<>(20);
        this.keepGoing = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (keepGoing || !queue.isEmpty()) {
                    try {
                        Pitch p = queue.poll(1, TimeUnit.SECONDS);
                        if (p != null) {
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

    public void stop() {
        try {
            keepGoing = false;
            if (thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void insertPitch(Pitch p) {
        if (p != null) {
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
        boolean halfCorrect = false;
        if (pitch.getPitch() == -1)
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
            } else if ((given.distance(source) == 1 || given.distance(source) == 11) && (Math.abs(pitch.getStart() - sourcePitch.getStart()) < 0.2)) {
                halfCorrect = true;
            }

        }
        if (!correct) {
            if (halfCorrect) {
                mistakes += 0.5;
            } else {
                mistakes++;
            }
        }
    }

    public void newAlgorithm(Pitch pitch) {
        if (pitch.getPitch() == -1)
            return;
        Note given = getNoteFromHz(pitch.getPitch());
        Group currentGroup = groups.get(iterator);
        if (pitch.getEnd() > currentGroup.getEndTime() )
        {
            //moving to the next group
            if(pitch.getStart() > groups.get(iterator+1).getStartTime()) {
                groups.get(iterator).calculateGrade();
                grade += groups.get(iterator).getGroupGrade();
                iterator++;
                currentGroup = groups.get(iterator);
            }
        }
        //the iterator and currentGroup pointing on the right group (time)
        //and now we start to compare the sample
        if(given.equals(currentGroup))
        {
            //if you song correctly
            groups.get(iterator).addToRightSamples(pitch);
            groups.get(iterator).addSuccess(1);
        }else
        {
            //if you song incorrectly

            if(given.distance(currentGroup.getNote()) == 1 || given.distance(currentGroup.getNote()) == 11)
            {
                //check if the sample is a "Neighbor" note
                groups.get(iterator).addToWrongSamples(pitch);
                groups.get(iterator).addMistakes(0.5);
            }else
            {
                //it's a bad mistake
                groups.get(iterator).addToRightSamples(pitch);
                groups.get(iterator).addSuccess(1);
            }
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

    public void getGrade(final GradeCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stop();
                if (performancePitches.size() == 0) {
                    callback.onGrade(0);
                }else{
                    double performanceRate = 100 / (groups.get(iterator).getEndTime() / groups.get(groups.size()-1).getEndTime());
                    grade *= performanceRate;
                    callback.onGrade(Math.round(grade));
                }
            }
        }).start();
    }
}