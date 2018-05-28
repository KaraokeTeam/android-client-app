package com.example.orpriesender.karaoke.model;

import android.net.Uri;
import android.util.Log;

import com.example.orpriesender.karaoke.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Or Priesender on 03-Jan-18.
 * This class will control each of the db references, which will be managed separately.
 */

public class ModelFireBase {
    private static final ModelFireBase instance = new ModelFireBase();

    public static ModelFireBase getInstance() {
        return instance;
    }

    private ModelFireBase() {

    }

    public void addUser(final User user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference ref = db.getReference("users").child(user.getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("id")){
                    //user exists - do nothing
                } else {
                    ref.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG","error : " + databaseError.getMessage());
            }
        });
    }

    public void updateUserImage(String userId, Uri imageUri){
        Log.d("TAG","UPDATING USER IMAGE");
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("users").child(userId).child("imageUrl");
        ref.setValue(imageUri.toString());
    }

    public void getUser(String id, final FirebaseCallback<User> callBack) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callBack.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callBack.onCancel();
            }
        });
    }

    public void addPost(Post post) {
        String postId = FirebaseDatabase.getInstance().getReference("posts").push().getKey();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        post.setId(postId);
        ref.child(postId).setValue(post.toMap());


    }

    public void getPost(String id, final FirebaseCallback<Post> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                callback.onComplete(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    public void getAllPosts(final FirebaseCallback<List<Post>> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new LinkedList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    posts.add(post);
                }
                callback.onComplete(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });

    }

    public void addSongItem(SongItem song){
        String postId = FirebaseDatabase.getInstance().getReference("songs").push().getKey();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("songs");
        song.setId(postId);
        ref.child(postId).setValue(song.toMap());
    }

    public void getSongsList(final FirebaseCallback<List<SongItem>> callback){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("songs");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<SongItem> songs = new LinkedList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SongItem song = snap.getValue(SongItem.class);
                    songs.add(song);
                }
                callback.onComplete(songs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });

    }

    interface FirebaseCallback<T> {
        void onComplete(T t);
        void onCancel();
    }
}
