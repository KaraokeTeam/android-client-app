package com.example.orpriesender.karaoke.audio;

import android.content.Context;
import android.util.Log;

import com.example.orpriesender.karaoke.file_readers.GroupReader;
import com.example.orpriesender.karaoke.file_readers.JsonGroup;
import com.example.orpriesender.karaoke.file_readers.ReaderCallback;
import com.example.orpriesender.karaoke.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.FileDownloadTask;

import java.io.*;
import java.util.ArrayList;
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
public class Grader implements Serializable{

    public interface InitCallback {
        void onReady(boolean success);
    }

    public interface NoteDistanceUpdates{
        void onUpdate(int distance);
    }

    private List<Pitch> sourcePitches = null;
    private List<Onset> sourceOnsets = null;
    private List<Pitch> performancePitches;
    private List<Onset> performanceOnsets;
    private int iterator;
    private double grade;
    private double maxGrade;
    private float performanceDuration;
    private final double roomForError = 1;

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
    private List<Pitch> rightPerformance;
    private NoteDistanceUpdates updater;

    private boolean onsetsCompleted = false, pitchesCompleted = false, groupsCompleted = false;

    public Grader(Context context, String songName, NoteDistanceUpdates updater) {
        this.context = context;
        this.songName = songName;
        this.rightPerformance = new ArrayList();
        this.updater = updater;
        try {
            this.notes = getNotesMapFromJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Grader(){

    }



    public void init(final InitCallback callback) {
        this.currentOffset = 0;
        this.performanceOnsets = new LinkedList<>();
        this.performancePitches = new LinkedList<>();
        this.mistakes = 0;
        this.keepGoing = true;
        this.grade = 0;
        this.maxGrade = 0;
        this.iterator = 1;
        performanceDuration = 0;

//        if (!onsetsCompleted) {
//            KaraokeRepository.getInstance().getSourceOnsetFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
//                    sourceOnsets = OnsetReader.readOnsetsFromFile(localFile);
//                    onsetsCompleted = true;
//                    checkIfReady(callback);
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    errorMode = true;
//                    onsetsCompleted = true;
//                    checkIfReady(callback);
//                }
//            });
//        }
//
//        if (!pitchesCompleted) {
//            KaraokeRepository.getInstance().getSourcePitchFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
//                    double now = System.currentTimeMillis();
//                    sourcePitches = PitchReader.readPitchesFromFile(localFile);
//                    Log.d("TAG","reading pitches took " + (System.currentTimeMillis() - now));
//                    pitchesCompleted = true;
//                    checkIfReady(callback);
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    errorMode = true;
//                    pitchesCompleted = true;
//                    checkIfReady(callback);
//                }
//            });
//        }


        if (!groupsCompleted) {
            KaraokeRepository.getInstance().getGroupsForSong(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                    GroupReader.readGroupsFromFile(localFile, new ReaderCallback<Group>() {
                        @Override
                        public void onReadingFinished(List<Group> results) {
                            groups = results;
                            groupsCompleted = true;
                            callback.onReady(true);
                        }
                    });

                    //checkIfReady(callback);
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    groupsCompleted = true;
                    callback.onReady(false);
                    //checkIfReady(callback);
                }
            });
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
                            easyAlgo(p);
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

    public void easyAlgo(Pitch pitch) {
        if (pitch.getPitch() == -1 ||
                pitch.getStart() < groups.get(0).getStartTime() ||
                pitch.getStart() > groups.get(groups.size() - 1).getEndTime()||
                pitch.getConfidence() < 0.8)
            return;

        Note given = getNoteFromHz(pitch.getPitch());

        this.getCurrentGroup(pitch);

        if (iterator == -1) {
            return;
        }
        int tempIterator = iterator;
        Group currentGroup = groups.get(tempIterator);

        performancePitches.add(pitch);

        //update the UI on note distance
        updater.onUpdate(currentGroup.getNote().distanceWithNegative(given));

        while (tempIterator < groups.size() && pitch.getStart() + roomForError >= (groups.get(tempIterator).getStartTime())) {
            if (currentGroup.getNote().isCorrectNote(given)) {
                rightPerformance.add(pitch);
                break;
            }
            tempIterator++;
            if (tempIterator < groups.size()) {
                currentGroup = groups.get(tempIterator);
            }
        }
    }

    private boolean noteWithinRange(Pitch pitch, Group group, double range) {
        if (pitch.getStart() >= group.getStartTime() - range && pitch.getStart() <= group.getEndTime() + range) {
            return true;
        }
        return false;
    }

    public void getCurrentGroup(Pitch pitch) {
        if (iterator != -1) {
            if (groups.get(iterator).getEndTime() < (pitch.getStart() - roomForError)) {
                if ((iterator + 1) < groups.size()) {
                    iterator++;
                    if (groups.get(iterator).getEndTime() < (pitch.getStart() - roomForError)) {
                        getCurrentGroup(pitch);
                    }
                } else {
                    iterator = -1;
                }
            }
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
//unused
//    public void checkIfReady(InitCallback callback) {
//        synchronized (this) {
//            if (onsetsCompleted && pitchesCompleted && groupsCompleted) {
//                if (sourceOnsets == null || sourcePitches == null || groups == null) {
//                    callback.onReady(false);
//                } else {
//                    callback.onReady(true);
//                }
//            }
//        }
//
//    }

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

            if (given.equals(source) && (Math.abs(pitch.getStart() - sourcePitch.getStart()) < 0.2)) {
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
        performancePitches.add(pitch);
        Note given = getNoteFromHz(pitch.getPitch());
        Group currentGroup = groups.get(iterator);//add 0.1 seconds
        Log.d("TAG", "current pitch time : " + pitch.getEnd() + " group end time : " + currentGroup.getEndTime());
        if (pitch.getEnd() > currentGroup.getEndTime()) {
            //moving to the next group
            if (pitch.getStart() > groups.get(iterator + 1).getStartTime()) {
                groups.get(iterator).calculateGrade();
                performanceDuration += groups.get(iterator).getDuration();
                Log.d("TAG", "win : " + groups.get(iterator).getSuccess() + " lose : " + groups.get(iterator).getMistakes() + " fillrate : " + groups.get(iterator).getFillRate());
                Log.d("TAG", "grade before + is : " + grade + "and group grade is : " + groups.get(iterator).getGroupGrade());
                //grade += groups.get(iterator).getGroupGrade();
                iterator++;
                currentGroup = groups.get(iterator);
            }
        }
        //the iterator and currentGroup pointing on the right group (time)
        //and now we start to compare the sample
        Log.d("TAG", "given note is : " + given.toString() + " current note is : " + currentGroup.getNote().toString());
        if (given.equals(currentGroup.getNote())) {
            //if you song correctly
            groups.get(iterator).addToRightSamples(pitch);

        } else {
            //if you song incorrectly
            groups.get(iterator).addToWrongSamples(pitch);
        }
    }
    public void handlePitch(Pitch pitch) {
        Note given = getNoteFromHz(pitch.getPitch());
        Group currentGroup = groups.get(iterator);
        Group nextGroup = groups.get(iterator + 1);
        Group previousGroup = groups.get(iterator - 1);

        if (pitch.getPitch() < 0) {
            return;
        }

        performancePitches.add(pitch);

        if (noteWithinRange(pitch, currentGroup, roomForError)) {
            if (given.isCorrectNote(currentGroup.getNote())) {
                currentGroup.addToRightSamples(pitch);
            } else if (noteWithinRange(pitch, previousGroup, roomForError)
                    && given.isCorrectNote(previousGroup.getNote())) {
                previousGroup.addToRightSamples(pitch);

            } else if (noteWithinRange(pitch, nextGroup, roomForError) && given.isCorrectNote(nextGroup.getNote())) {
                nextGroup.addToRightSamples(pitch);
            } else {
                currentGroup.addToWrongSamples(pitch);
            }
        } else if (noteWithinRange(pitch, previousGroup, roomForError)) {

            if (given.isCorrectNote(previousGroup.getNote())) {
                previousGroup.addToRightSamples(pitch);

            } else {
                previousGroup.addToWrongSamples(pitch);

            }
        } else if (noteWithinRange(pitch, nextGroup, roomForError)) {

            if (given.isCorrectNote(nextGroup.getNote())) {
                nextGroup.addToRightSamples(pitch);

            } else {
                nextGroup.addToWrongSamples(pitch);
            }
        } else {
            System.out.println("note is in grey zone , pitch time :"  + pitch.getStart() + " group start : " + currentGroup.getStartTime() + " group end " + currentGroup.getEndTime());
        }
        if (!noteWithinRange(pitch, currentGroup, 0) && noteWithinRange(pitch, nextGroup, roomForError)) {
            if (iterator < groups.size() - 2)
                iterator++;
        }
        currentGroup.calculateGrade();

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

    public GraphData getGraphData(){
        if(performancePitches.size() == 0 || groups.size() == 0){
            return null;
        }

        List<NoteTimePair> performance = new ArrayList<>();
        List<NoteTimePair> source = new ArrayList<>();

        for(int i=0; i < groups.size(); i++){
            Group currentGroup = groups.get(i);
            Note currentNote = currentGroup.getNote();
            source.add(new NoteTimePair(currentNote.getNote(),currentNote.getNoteIndex(),currentGroup.getMiddleTime()));
        }

        List<Group> performanceGroups = this.getGroups(performancePitches);
        for(int i=0; i < performanceGroups.size(); i++){
            Group currentGroup = performanceGroups.get(i);
            Note currentNote = currentGroup.getNote();
            performance.add(new NoteTimePair(currentNote.getNote(),currentNote.getNoteIndex(),currentGroup.getMiddleTime()));
        }

        return new GraphData(performance,source);

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
        if (performancePitches.size() == 0) {
            return 0;
        }
        System.out.println("iterator : " + iterator);
        double performanceLength = getPerformanceLength();
        groups.get(0).calculateGrade();
        groups.get(iterator + 1).calculateGrade();
        for (int i = 0; i < iterator; i++) {
            double fillRate = groups.get(i).getDuration() / performanceLength;
            double groupGrade = groups.get(i).getGroupGrade();
            grade += fillRate * groupGrade;
        }
        System.out.println("grade : " + grade);
        return grade;
    }

    public double getGrade2() {
        if (performancePitches.size() == 0) {
            return 0;
        }
        double performanceLength = performancePitches.size();
        double rightSamples = rightPerformance.size();
        System.out.println("performanceLength: " + performanceLength + " rightSamples " + rightSamples);
        grade = (rightSamples / performanceLength) * 100;
        return grade;
    }

    public double getPerformanceLength(){
        for(int i=0; i < iterator; i++){
            performanceDuration += groups.get(i).getDuration();
        }
        return performanceDuration;
    }

    public List<Group> getGroups(List<Pitch> pitches){
        String currentNote = null;
        Group currentGroup = new Group();
        if(pitches.size() == 0) return null;
        currentGroup.setNote(new Note(""));

        float totalTime = pitches.get(pitches.size() - 1).getEnd() - pitches.get(0).getStart();

        List<Group> results = new ArrayList<>();

        for(int i=0; i < pitches.size(); i++){
            Pitch currentPitch = pitches.get(i);
            currentNote = getNoteFromHz(currentPitch.getPitch()).getNote();

            if(currentGroup.getNote().getNote().equalsIgnoreCase(currentNote)){
                currentGroup.addToAllSamples(currentPitch);
            } else {
                currentGroup.updateStartTime();
                currentGroup.updateEndTime();
                currentGroup.updateDuration();
                currentGroup.updateFillRate(totalTime);
                results.add(currentGroup);

                currentGroup = new Group();
                currentGroup.setNote(new Note(currentNote));
                currentGroup.addToAllSamples(currentPitch);
            }
        }
        return results;
    }

    public List<JsonGroup> getJsonGroups(List<Group> groups){
        List<JsonGroup> results = new ArrayList<>();
        for(int i=0 ; i < groups.size(); i++){
            results.add(new JsonGroup(groups.get(i)));
        }
        System.out.println("PRINTING GROUPS JSON");
        System.out.println("SIZE IS " + results.size());
        System.out.println(results.toString());
        return results;
    }

    public List<Pitch> getSourcePitches() {
        return sourcePitches;
    }

    public void setSourcePitches(List<Pitch> sourcePitches) {
        this.sourcePitches = sourcePitches;
    }

    public List<Onset> getSourceOnsets() {
        return sourceOnsets;
    }

    public void setSourceOnsets(List<Onset> sourceOnsets) {
        this.sourceOnsets = sourceOnsets;
    }

    public List<Pitch> getPerformancePitches() {
        return performancePitches;
    }

    public void setPerformancePitches(List<Pitch> performancePitches) {
        this.performancePitches = performancePitches;
    }

    public List<Onset> getPerformanceOnsets() {
        return performanceOnsets;
    }

    public void setPerformanceOnsets(List<Onset> performanceOnsets) {
        this.performanceOnsets = performanceOnsets;
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public double getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(double maxGrade) {
        this.maxGrade = maxGrade;
    }

    public float getPerformanceDuration() {
        return performanceDuration;
    }

    public void setPerformanceDuration(float performanceDuration) {
        this.performanceDuration = performanceDuration;
    }

    public double getRoomForError() {
        return roomForError;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, List<Double>> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, List<Double>> notes) {
        this.notes = notes;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    public double getMistakes() {
        return mistakes;
    }

    public void setMistakes(double mistakes) {
        this.mistakes = mistakes;
    }

    public ArrayBlockingQueue<Pitch> getQueue() {
        return queue;
    }

    public void setQueue(ArrayBlockingQueue<Pitch> queue) {
        this.queue = queue;
    }

    public boolean isKeepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isErrorMode() {
        return errorMode;
    }

    public void setErrorMode(boolean errorMode) {
        this.errorMode = errorMode;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Pitch> getRightPerformance() {
        return rightPerformance;
    }

    public void setRightPerformance(List<Pitch> rightPerformance) {
        this.rightPerformance = rightPerformance;
    }

    public NoteDistanceUpdates getUpdater() {
        return updater;
    }

    public void setUpdater(NoteDistanceUpdates updater) {
        this.updater = updater;
    }

    public boolean isOnsetsCompleted() {
        return onsetsCompleted;
    }

    public void setOnsetsCompleted(boolean onsetsCompleted) {
        this.onsetsCompleted = onsetsCompleted;
    }

    public boolean isPitchesCompleted() {
        return pitchesCompleted;
    }

    public void setPitchesCompleted(boolean pitchesCompleted) {
        this.pitchesCompleted = pitchesCompleted;
    }

    public boolean isGroupsCompleted() {
        return groupsCompleted;
    }

    public void setGroupsCompleted(boolean groupsCompleted) {
        this.groupsCompleted = groupsCompleted;
    }
}