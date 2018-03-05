package com.example.orpriesender.karaoke.web_service;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public interface onGradeResponseListener {
    public void onGradeResponse(Integer grade);

    public void onFailureRespnonse(String message);
}
