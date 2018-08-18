package com.example.orpriesender.karaoke.file_readers;

import com.example.orpriesender.karaoke.model.Group;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Or Priesender on 18-Aug-18.
 */

public class JsonGroup {

    String note;
    String start;
    String end;
    String amount;
    String note_value;
    String duration;

    public JsonGroup(Group group){
        DecimalFormat decimalFormat = new DecimalFormat("#.#######");
        this.note = group.getNote().getNote();
        this.start = decimalFormat.format(group.getStartTime());
        this.end = decimalFormat.format(group.getEndTime());
        this.amount = decimalFormat.format(group.getTotalSamples());
        this.note_value = decimalFormat.format(group.getFillRate());
        this.duration = decimalFormat.format(group.getDuration());
    }

}
