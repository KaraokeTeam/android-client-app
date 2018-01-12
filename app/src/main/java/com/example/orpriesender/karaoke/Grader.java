package com.example.orpriesender.karaoke;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by aboud on 1/10/2018.
 */

public class Grader {

    private List<Pitch> sourcePitches;
    private List<Onset> sourceOnsets;

    private List<Pitch> performancePitches;
    private List<Onset> performanceOnsets;

    private Context context;

    private Map<String,List<Double>> notes;

    public Grader(Context context, String sourcePitchFile, String sourceOnsetFile){
        //extracts the file from the assets folder and gives it to the pitch and onset readers
        this.performanceOnsets = new LinkedList<>();
        this.performancePitches = new LinkedList<>();
        this.context = context;
        try {

            this.notes = getNotesMapFromJson();
            this.sourcePitches = PitchReader.readPitchesFromFile(loadFileToStorage(context.getAssets().open(sourcePitchFile),"sourcePitch"));
            this.sourceOnsets = OnsetReader.readOnsetsFromFile(loadFileToStorage(context.getAssets().open(sourceOnsetFile),"sourceOnset"));
            this.getNotesMapFromJson();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String,List<Double>> getNotesMapFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String,List<Double>> map =
                mapper.readValue(loadFileToStorage(context.getAssets().open("NotesToHz.json"),"notestohz.json"),HashMap.class);
        return map;
    }

    public Note getNoteFromHz(float pitch){
        String closestNote = "";
        int closestOctave = -1;
        double diff = 9999;
        for(String s : notes.keySet()){
           for(Double d : notes.get(s)){
               if(diff > Math.abs(pitch-d)){
                   diff = pitch - d;
                   closestNote = s;
                   closestOctave = notes.get(s).indexOf(d);
               }
           }
        }
        return new Note(closestNote,closestOctave,diff);
    }

    //save the current given onset and analyze it
    public void consumeOnset(Onset onset){
        this.performanceOnsets.add(onset);
    }

    //save the current given pitch and analyze it
    public void consumePitch(Pitch pitch){
        if(pitch.getPitch() != -1)
            Log.d("NOTE",this.getNoteFromHz(pitch.getPitch()).toString());
        this.performancePitches.add(pitch);
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

    public void printSourcePitches(){
        if(this.sourcePitches != null){
            for(Pitch p : sourcePitches){
                Log.d("SOURCES",p.toString());
            }
        }
    }

    public void printSourceOnsets(){
        if(this.sourceOnsets != null){
            for(Onset o : sourceOnsets){
                Log.d("SOURCES",o.toString());

            }
        }
    }
}
