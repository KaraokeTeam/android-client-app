package com.example.orpriesender.karaoke;

import java.util.List;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public interface WebServiceResponseListener {
    public void onGradeResponse(Integer grade);
    public void onGroupsResponse(List<NoteGroup> notes);
}
