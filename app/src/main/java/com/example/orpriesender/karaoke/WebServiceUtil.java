package com.example.orpriesender.karaoke;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Or Priesender on 27-Dec-17.
 */

public class WebServiceUtil {
    private String baseUrl;
    private WebServiceClient client;
    private Context context;
    private static  WebServiceUtil instance;


    public static final WebServiceUtil getInstance(String baseUrl,Context context){
        if(instance == null)
            instance = new WebServiceUtil(baseUrl,context);
        return instance;
    }

    private WebServiceUtil(@Nullable String baseUrl,Context context){
        if(baseUrl == null)
            this.baseUrl = "http://10.0.0.2:5000/";
        else
            this.baseUrl = baseUrl;

        this.context = context;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();

        this.client = retrofit.create(WebServiceClient.class);
    }


    public void getNoteGroups(String filename,final onNoteGroupsResponseListener listener){
        File st = Environment.getDataDirectory();
        File file = new File(st,filename);

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(context.getContentResolver().getType(Uri.fromFile(file))),file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName(),requestFile);

        Call<List<NoteGroup>> call = client.getNoteGroups(body);

        call.enqueue(new Callback<List<NoteGroup>>() {
            @Override
            public void onResponse(Call<List<NoteGroup>> call, Response<List<NoteGroup>> response) {
                listener.onNoteGroupsResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<NoteGroup>> call, Throwable t) {
                Toast toast = Toast.makeText(context,"Failure sending file",Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


    public void getGrade(String filename,final onGradeResponseListener listener){
        File file = new File(filename);
        MediaType type = MediaType.parse(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath())));
        RequestBody requestFile = RequestBody.create(
                type,file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file",file.getName(),requestFile);

        Call<Integer> call = client.getGrade(body);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                listener.onGradeResponse(response.body());
                Toast toast = Toast.makeText(context,"Successfully sent file",Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("error",t.getMessage());
                Toast toast = Toast.makeText(context,"Failure sending file",Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


}
