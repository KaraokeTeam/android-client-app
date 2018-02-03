package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class UserProfileActivity extends Activity {
    TextView username;
    TextView rating;
    ImageView profilePic;
    ImageButton backButton;
    ProgressBar spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //get needed views
        username = findViewById(R.id.userprofile_username);
        rating = findViewById(R.id.userprofile_rating);
        profilePic = findViewById(R.id.userprofile_pic);
        spinner = findViewById(R.id.userprofile_spinner);
        backButton = findViewById(R.id.back_button);
        //get the list fragment
        final PostListFragment fragment = (PostListFragment) getFragmentManager().findFragmentById(R.id.userprofile_posts_list_fragment);
        final String userId = getIntent().getStringExtra("userId");
        spinner.setVisibility(View.VISIBLE);
        ModelFirebase.getInstance().getUser(userId, new ModelFirebase.FirebaseCallback<User>() {
            @Override
            public void onComplete(User user) {
                  username.setText(user.getUsername());
                  rating.setText("" + user.getRating());
                  spinner.setVisibility(View.GONE);
                  //take image from storage and insert to profile pic
            }

            @Override
            public void onCancel() {
                //present failure and exit the app
            }
        });

        ModelFirebase.getInstance().getAllPosts(new ModelFirebase.FirebaseCallback<List<Post>>() {
            @Override
            public void onComplete(List<Post> posts) {
//                adapter.setPosts(posts);
//                postList.setAdapter(adapter);
//                spinner.setVisibility(View.GONE);
                fragment.setPostsForUser(posts,userId);
            }

            @Override
            public void onCancel() {
                //spinner.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(),"Loading feed failed",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,200);
                toast.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.super.onBackPressed();
            }
        });
    }
}
