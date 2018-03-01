package com.example.orpriesender.karaoke;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.List;

/**
 * Created by Or Priesender on 28-Feb-18.
 */
//singleton main repository
public class KaraokeRepository {

    private static KaraokeRepository instance = new KaraokeRepository();

    public static KaraokeRepository getInstance(){
        return instance;
    }

    public LiveData<User> getUser(String userId){
        final MutableLiveData<User> data = new MutableLiveData<>();

        ModelFireBase.getInstance().getUser(userId, new ModelFireBase.FirebaseCallback<User>() {
            @Override
            public void onComplete(User user) {
                data.setValue(user);
            }

            @Override
            public void onCancel() {
                data.setValue(null);
            }
        });

        return data;
    }


    public LiveData<Post> getPost(String postId){
        final MutableLiveData<Post> data = new MutableLiveData<>();

        ModelFireBase.getInstance().getPost(postId, new ModelFireBase.FirebaseCallback<Post>() {

            @Override
            public void onComplete(Post post) {
                data.setValue(post);
            }

            @Override
            public void onCancel() {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<List<Post>> getAllPosts(){

        final MutableLiveData<List<Post>> data = new MutableLiveData<>();

        ModelFireBase.getInstance().getAllPosts(new ModelFireBase.FirebaseCallback<List<Post>>() {
            @Override
            public void onComplete(List<Post> posts) {
                data.setValue(posts);
            }

            @Override
            public void onCancel() {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<File> getPlayback(String name,String extension){
        final MutableLiveData<File> data = new MutableLiveData<>();

        FirebaseStorageManager.getInstance().getPlayback(name, extension, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                data.setValue(localFile);
            }

            @Override
            public void onFailure(Exception e) {
                data.setValue(null);
            }
        });

        return data;
    }
}
