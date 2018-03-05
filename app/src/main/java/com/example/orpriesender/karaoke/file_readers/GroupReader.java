package com.example.orpriesender.karaoke.file_readers;

import com.example.orpriesender.karaoke.model.Group;
import com.example.orpriesender.karaoke.model.Note;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Or Priesender on 26-Feb-18.
 */

public class GroupReader {

    public static List<Group> readGroupsFromFile(File file) {
        List<Group> result = new ArrayList<>();
        JsonParser parser = new JsonParser();
        try {
            Object object = parser.parse(new FileReader(file));
            JsonObject jsonObj = (JsonObject) object;

            JsonArray groups = (JsonArray) jsonObj.get("groups");
            Iterator<JsonElement> i = groups.iterator();
            while (i.hasNext()) {
                Group group = new Group();
                JsonObject arrayElement = (JsonObject) i.next();

                group.setSamplesAmount(Integer.parseInt(arrayElement.get("amount").getAsString()));
                group.setDuration(Float.parseFloat(arrayElement.get("duration").getAsString()));
                group.setEndTime(Float.parseFloat(arrayElement.get("end").getAsString()));
                group.setStartTime(Float.parseFloat(arrayElement.get("start").getAsString()));
                group.setFillRate(Float.parseFloat(arrayElement.get("note_value").getAsString()));
                group.setNote(new Note(arrayElement.get("note").getAsString()));
                result.add(group);
            }

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
