package com.example.orpriesender.karaoke;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class FirebaseStorageManager {

    private static FirebaseStorageManager instance = new FirebaseStorageManager();

    private FirebaseStorageManager(){

    }

    public static FirebaseStorageManager getInstance(){
        return instance;
    }

    void uploadAudioForPost(String postId, File file,final FireBaseStorageUploadCallback callback){
        StorageReference instance = FirebaseStorage.getInstance().getReference("audio");
        final UploadTask task = instance.child(postId).putFile(Uri.fromFile(file));
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

    void uploadImageForUser(String userId,File file, final FireBaseStorageUploadCallback callback){
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

    void getPlayback(String name,String extension,final FireBaseStorageDownloadCallback callback){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("playbacks/" + name + "." + extension);
        try {
            final File localFile = File.createTempFile("playbacks",name + "." + extension);
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot,localFile);
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

    void getSourceOnsetFile(String name,final FireBaseStorageDownloadCallback callback){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("sources/" + name + "/onsets.txt");
        try{
            final File localFile = File.createTempFile(name + "Onsets",".txt");
            localFile.deleteOnExit();
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot,localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void getSourcePitchFile(String name,final FireBaseStorageDownloadCallback callback){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("sources/" + name + "/pitches.txt");
        try{
            final File localFile = File.createTempFile(name + "Pitches",".txt");
            localFile.deleteOnExit();
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot,localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void getGroupsForSong(String songName, final FireBaseStorageDownloadCallback callback){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("groups/" + songName + ".json");
        try{
            final File localFile = File.createTempFile(songName + "Groups",".json");
            localFile.deleteOnExit();

            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    callback.onSuccess(taskSnapshot,localFile);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    interface FireBaseStorageDownloadCallback{
        void onSuccess(FileDownloadTask.TaskSnapshot task,File localFile);
        void onFailure(Exception e);
    }

    interface FireBaseStorageUploadCallback {
        void onSuccess(UploadTask.TaskSnapshot task);
        void onFailure(Exception e);
    }

}
