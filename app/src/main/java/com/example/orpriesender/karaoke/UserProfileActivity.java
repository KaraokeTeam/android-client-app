package com.example.orpriesender.karaoke;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class UserProfileActivity extends FragmentActivity {
    TextView username;
    TextView rating;
    ImageView profilePic;
    ImageButton backButton;
    ProgressBar spinner;
    private PostListViewModel postListVM;
    private UserProfileViewModel userProfileVM;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //get needed views
        username = findViewById(R.id.userprofile_username);
        rating = findViewById(R.id.userprofile_rating);
        profilePic = findViewById(R.id.userprofile_pic);
        spinner = findViewById(R.id.userprofile_spinner);
        backButton = findViewById(R.id.tarsos_activity_back_button);
        //get the list fragment
        final PostListFragment fragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.userprofile_posts_list_fragment);
        final String userId = getIntent().getStringExtra("userId");
        spinner.setVisibility(View.VISIBLE);
        Log.d("TAG","user id is " + userId);

        //fetching the users post list
        postListVM = ViewModelProviders.of(this).get(PostListViewModel.class);
        postListVM.getAllPosts().observe(this, new Observer<List<Post>>() {

            @Override
            public void onChanged(@Nullable List<Post> posts) {
                fragment.setPostsForUser(posts,userId);
            }
        });

        //fetching the user
        UserProfileViewModelFactory factory = new UserProfileViewModelFactory(userId);
        userProfileVM = ViewModelProviders.of(this,factory).get(UserProfileViewModel.class);
        userProfileVM.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                username.setText(user.getUsername());
                rating.setText("" + user.getRating());
                spinner.setVisibility(View.GONE);
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
