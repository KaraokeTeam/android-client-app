package com.example.orpriesender.karaoke;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public interface WebServiceClient {
    @Multipart
    @POST("/group")
    Call<List<NoteGroup>> getNoteGroups(@Part MultipartBody.Part file);

    @Multipart
    @POST("/grade")
    Call<Integer> getGrade(@Part MultipartBody.Part file);
}
