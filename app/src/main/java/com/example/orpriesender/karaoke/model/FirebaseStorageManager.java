package com.example.orpriesender.karaoke.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by Or Priesender on 03-Feb-18.
 */

//singleton class to handle firebase storage
public class FirebaseStorageManager {

    private static FirebaseStorageManager instance = new FirebaseStorageManager();

    private FirebaseStorageManager() {

    }

    public static FirebaseStorageManager getInstance() {
        return instance;
    }


    void downloadAudioForPost(String postId, final FireBaseStorageDownloadCallback callback) {
        try {
            //whatever it is saved in
            StorageReference instance = FirebaseStorage.getInstance().getReference("performances/" + postId + ".wav");
            final File localFile = File.createTempFile(postId, ".wav");
            instance.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot, localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void uploadAudioForPost(String postId, File file, final FireBaseStorageUploadCallback callback) {
        if (file == null) {
            callback.onFailure(new NullPointerException("Attempt to upload a null file"));
            return;
        }

        StorageReference instance = FirebaseStorage.getInstance().getReference().child("performances/").child(postId + ".wav");
        final UploadTask task = instance.putFile(Uri.fromFile(file));
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onProgress(new Long(100 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount())).intValue());
            }
        });
    }

    void uploadImageForUser(String userId, File file, final FireBaseStorageUploadCallback callback) {
        StorageReference instance = FirebaseStorage.getInstance().getReference("images");
        final UploadTask task = instance.child(userId).putFile(Uri.fromFile(file));
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    //TODO : cache here also
    void downloadImageForUser(String userId, final FireBaseStorageDownloadCallback callback) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + userId + ".jpg");
        try {
            final File localFile = File.createTempFile(userId, "jpg");
            localFile.deleteOnExit();
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot, localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                    localFile.delete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //saves the playback to a local file
    void getPlayback(String filename, final FireBaseStorageDownloadCallback callback) {
        final File cacheFile = LocalCacheManager.getInstance().saveOrUpdate(filename);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("playbacks/" + filename);
        ref.getFile(cacheFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot, cacheFile);
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
                cacheFile.delete();
            }
        });

    }

    void getSourceOnsetFile(String name, final FireBaseStorageDownloadCallback callback) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("sources/" + name + "/onsets.txt");
        final File cachedFile = LocalCacheManager.getInstance().saveOrUpdate(name + "Onsets.txt");
        ref.getFile(cachedFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot, cachedFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    void getSourcePitchFile(String name, final FireBaseStorageDownloadCallback callback) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("sources/" + name + "/pitches.txt");
        final File cachedFile = LocalCacheManager.getInstance().saveOrUpdate(name + "Pitches.txt");
        ref.getFile(cachedFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot, cachedFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    void getGroupsForSong(String songName, final FireBaseStorageDownloadCallback callback) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("groups/" + songName + ".json");
        final File cachedFile = LocalCacheManager.getInstance().saveOrUpdate(songName + "Groups.json");
        ref.getFile(cachedFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot, cachedFile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public interface FireBaseStorageDownloadCallback {
        void onSuccess(FileDownloadTask.TaskSnapshot task, File localFile);

        void onFailure(Exception e);
    }

    public interface FireBaseStorageUploadCallback {
        void onSuccess(UploadTask.TaskSnapshot task);

        void onFailure(Exception e);

        void onProgress(int progress);
    }

}
