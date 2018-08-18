package com.example.orpriesender.karaoke.file_readers;

import android.util.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

/**
 * Created by Or Priesender on 18-Aug-18.
 */

public class GroupWriter {

    public static void writeToFile(File file, List<JsonGroup> groups){
        try {
            Writer writer = new FileWriter(file);
            new Gson().toJson(new JsonGroupObject(groups),writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
