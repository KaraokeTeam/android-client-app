package com.example.orpriesender.karaoke;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class FirebaseStorageManager {

    private static FirebaseStorageManager instance = new FirebaseStorageManager();

    private FirebaseStorageManager(){

    }

    public FirebaseStorageManager getInstance(){
        return instance;
    }

    void uploadAudioForPost(String postId, File file,final FireBaseStorageCallback callback){
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

    void uploadImageForUser(String userId,File file, final FireBaseStorageCallback callback){
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

    interface FireBaseStorageCallback{
        void onSuccess(UploadTask.TaskSnapshot snapshot);
        void onFailure(Exception e);
    }

}
