package com.example.orpriesender.karaoke;

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

public class ModelFirebase {
    private static final ModelFirebase instance = new ModelFirebase();

    public static ModelFirebase getInstance() {
        return instance;
    }

    private ModelFirebase() {

    }

    public void addUser(User user){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("users").child(user.getId());
        ref.setValue(user);
    }

    public void getUser(String id, final FirebaseCallback<User> callBack){
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

    public void addPost(Post post){
        String postId = FirebaseDatabase.getInstance().getReference("posts").push().getKey();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.child(postId).setValue(post.toMap());

    }

    public void getPost(String id,final FirebaseCallback<Post> callback){
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

    void getAllPosts(final FirebaseCallback<List<Post>> callback){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new LinkedList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
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

    interface FirebaseCallback<T>{
        void onComplete(T t);
        void onCancel();
    }
}
