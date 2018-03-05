package com.example.orpriesender.karaoke.web_service;

import com.example.orpriesender.karaoke.model.NoteGroup;

import java.util.List;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public interface onNoteGroupsResponseListener {
    public void onNoteGroupsResponse(List<NoteGroup> notes);
}
