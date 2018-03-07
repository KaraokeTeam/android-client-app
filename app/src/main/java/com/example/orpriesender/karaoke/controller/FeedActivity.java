package com.example.orpriesender.karaoke.controller;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.LocalCacheManager;
import com.example.orpriesender.karaoke.model.Post;
import com.example.orpriesender.karaoke.model.RoomDatabaseManager;
import com.example.orpriesender.karaoke.model.SongItem;
import com.example.orpriesender.karaoke.model.User;
import com.example.orpriesender.karaoke.view_model.PostListViewModel;
import com.example.orpriesender.karaoke.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends FragmentActivity implements PostListFragment.onUsernameClicked , PostListFragment.onPlayClicked{

    ImageButton profileButton, singButton;
    PostListViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //when application starts, set the context
        LocalCacheManager.setContext(getApplicationContext());
        RoomDatabaseManager.setContext(getApplicationContext());

        KaraokeRepository.getInstance().addUser(new User(FirebaseAuth.getInstance().getCurrentUser()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);
        profileButton = findViewById(R.id.all_posts_profile_button);
        singButton = findViewById(R.id.all_posts_sing_button);
        final PostListFragment fragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);

        vm = ViewModelProviders.of(this).get(PostListViewModel.class);
        vm.getAllPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if (posts != null)
                    fragment.setPosts(posts);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(FirebaseAuth.getInstance().getUid());
            }
        });

        singButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSingActivity();
            }
        });

    }

    private void startSingActivity() {
        Intent intent = new Intent(getApplicationContext(), TarsosActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity(String userId) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    @Override
    public void onUsernameClicked(String userId) {
        startProfileActivity(userId);
    }

    @Override
    public void onPlayClicked(String postId, final PostListFragment.onDownloadFinished callback) {
        //check cache

        KaraokeRepository.getInstance().downloadPerformance(postId).observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
                callback.onDownloadFinished(file);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalCacheManager.getInstance().destroyCache();
    }
}
