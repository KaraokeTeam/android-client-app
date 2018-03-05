package com.example.orpriesender.karaoke.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;

import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Or Priesender on 28-Feb-18.
 */
//singleton main repository
public class KaraokeRepository {

    private static KaraokeRepository instance = new KaraokeRepository();

    public static KaraokeRepository getInstance() {
        return instance;
    }

    public LiveData<User> getUser(String userId) {
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

    public void addUser(User user) {
        ModelFireBase.getInstance().addUser(user);
    }


    public LiveData<Post> getPost(String postId) {
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

    public void addPost(Post post) {
        ModelFireBase.getInstance().addPost(post);
    }

    public void getSourceOnsetFile(String songName, final FirebaseStorageManager.FireBaseStorageDownloadCallback callback) {

        File cachedFile = LocalCacheManager.getInstance().getIfExists(songName + "Onsets.txt");
        if (cachedFile != null) {
            Log.d("LOG", "USING CACHED FILE FOR ONSETS");
            callback.onSuccess(null, cachedFile);
            return;
        }

        FirebaseStorageManager.getInstance().getSourceOnsetFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                callback.onSuccess(task, localFile);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure(e);
            }
        });
    }

    public void getSourcePitchFile(String songName, final FirebaseStorageManager.FireBaseStorageDownloadCallback callback) {

        File cachedFile = LocalCacheManager.getInstance().getIfExists(songName + "Pitches.txt");
        if (cachedFile != null) {
            Log.d("LOG", "USING CACHED FILE FOR PITCHES");
            callback.onSuccess(null, cachedFile);
            return;
        }

        FirebaseStorageManager.getInstance().getSourcePitchFile(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                callback.onSuccess(task, localFile);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getGroupsForSong(String songName, final FirebaseStorageManager.FireBaseStorageDownloadCallback callback) {

        File cachedFile = LocalCacheManager.getInstance().getIfExists(songName + "Groups.txt");
        if (cachedFile != null) {
            Log.d("LOG", "USING CACHED FILE FOR GROUPS");
            callback.onSuccess(null, cachedFile);
            return;
        }

        FirebaseStorageManager.getInstance().getGroupsForSong(songName, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile) {
                callback.onSuccess(task, localFile);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public LiveData<List<Post>> getAllPosts() {
        final MutableLiveData<List<Post>> data = new MutableLiveData<>();
        ModelFireBase.getInstance().getAllPosts(new ModelFireBase.FirebaseCallback<List<Post>>() {
                @Override
                public void onComplete(List<Post> posts) {

                Collections.sort(posts, Collections.reverseOrder(new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        if (o1.getDate() == null || o2.getDate() == null) {
                            return 0;
                        }
                        return o1.getDate().compareTo(o2.getDate());
                    }
                }));
                //RoomDatabaseManager.getInstance().deleteAll();
                //RoomDatabaseManager.getInstance().addPosts(posts);
                data.setValue(posts);
            }

            @Override
            public void onCancel() {
                data.setValue(null);//RoomDatabaseManager.getInstance().getAllPosts()
            }
        });
        return data;
    }

    //using a local cache file if exists, if not getting it from firebase
    public LiveData<File> getPlayback(final String filename) {
        final MutableLiveData<File> data = new MutableLiveData<>();

        File cachedFile = LocalCacheManager.getInstance().getIfExists(filename);
        if (cachedFile != null) {
            Log.d("LOG", "USING CACHED FILE FOR PLAYBACK");
            data.setValue(cachedFile);
            return data;
        }

        FirebaseStorageManager.getInstance().getPlayback(filename, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
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

    public void uploadPerformance(String postId, File file, FirebaseStorageManager.FireBaseStorageUploadCallback callback) {
        FirebaseStorageManager.getInstance().uploadAudioForPost(postId, file, callback);
    }

    //TODO: untested
    public LiveData<File> downloadPerformance(final String postId) {
        final MutableLiveData<File> data = new MutableLiveData<>();

        File cachedFile = LocalCacheManager.getInstance().getIfExists(postId + ".wav");
        if (cachedFile != null) {
            Log.d("LOG", "USING CACHED FILE FOR PERFORMANCE");
            data.setValue(cachedFile);
            return data;
        }

        FirebaseStorageManager.getInstance().downloadAudioForPost(postId, new FirebaseStorageManager.FireBaseStorageDownloadCallback() {
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
