package com.example.orpriesender.karaoke.web_service;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.orpriesender.karaoke.model.NoteGroup;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Utility for accessing the python web service.
 */

public class WebServiceUtil {
    //base web service url
    private String baseUrl;
    //the Retrofit client
    private WebServiceClient client;
    //the application context
    private Context context;
    //singleton instance
    private static WebServiceUtil instance;

    //get the singleton instance
    public static final WebServiceUtil getInstance(String baseUrl, Context context) {
        if (instance == null)
            instance = new WebServiceUtil(baseUrl, context);
        return instance;
    }

    //constructor
    private WebServiceUtil(@Nullable String baseUrl, Context context) {
        if (baseUrl == null)
            this.baseUrl = "http://10.0.0.6:5000/";
        else
            this.baseUrl = baseUrl;

        this.context = context;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).readTimeout(500, TimeUnit.SECONDS);

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder.client(httpClient.build()).build();

        this.client = retrofit.create(WebServiceClient.class);
    }

    //get note groups from the web service
    public void getNoteGroups(String filename, final onNoteGroupsResponseListener listener) {
        File st = Environment.getDataDirectory();
        File file = new File(st, filename);

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(context.getContentResolver().getType(Uri.fromFile(file))), file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<List<NoteGroup>> call = client.getNoteGroups(body);

        call.enqueue(new Callback<List<NoteGroup>>() {
            @Override
            public void onResponse(Call<List<NoteGroup>> call, Response<List<NoteGroup>> response) {
                Toast toast = Toast.makeText(context, "Successfully sent file", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
                listener.onNoteGroupsResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<NoteGroup>> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Failure sending file", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        });

    }


    public void getGrade(String filename, final onGradeResponseListener listener) {
        File file = new File(filename);
        MediaType type = MediaType.parse(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath())));
        RequestBody requestFile = RequestBody.create(
                type, file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<Integer> call = client.getGrade(body);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                listener.onGradeResponse(response.body());
                Toast toast = Toast.makeText(context, "Successfully sent file", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                listener.onFailureRespnonse(t.getMessage());
                Log.e("error", t.getMessage());
                Toast toast = Toast.makeText(context, "Failure sending file", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 200);
                toast.show();
            }
        });

    }


}
