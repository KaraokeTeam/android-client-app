package com.example.orpriesender.karaoke.file_readers;

import com.example.orpriesender.karaoke.model.Onset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Or Priesender on 12-Jan-18.
 */

public class OnsetReader {

    public static List<Onset> readOnsetsFromFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<Onset> result = new LinkedList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(onsetFromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private static Onset onsetFromLine(String line) {
        return new Onset(Float.parseFloat(line));
    }
}
