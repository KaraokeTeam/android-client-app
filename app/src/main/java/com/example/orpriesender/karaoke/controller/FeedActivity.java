package com.example.orpriesender.karaoke.controller;


import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import be.tarsos.dsp.util.FFMPEGDownloader;
import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.LocalCacheManager;
import com.example.orpriesender.karaoke.model.Post;
import com.example.orpriesender.karaoke.model.RoomDatabaseManager;
import com.example.orpriesender.karaoke.model.SongItem;
import com.example.orpriesender.karaoke.model.User;
import com.example.orpriesender.karaoke.view_model.PostListViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends FragmentActivity implements PostListFragment.onUsernameClicked, PostListFragment.onPlayClicked, PostListFragment.onProfilePicNeeded {

    ImageButton profileButton, singButton;
    PostListViewModel vm;
    Bundle savedInstanceState;
    private final int ALL_PERMISSIONS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);
        if (checkAndRequestPermissions()) {
            createActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public boolean checkAndRequestPermissions() {
        int permissionExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (recordPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ALL_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void createActivity() {
        //when application starts, set the context
        LocalCacheManager.setContext(getApplicationContext());
        RoomDatabaseManager.setContext(getApplicationContext());

        //after a user logged in - add it to users list or update it
        KaraokeRepository.getInstance().addUser(new User(FirebaseAuth.getInstance().getCurrentUser()));

        //when adding a song run the application once with this
        //KaraokeRepository.getInstance().addSong(new SongItem("Sam Smith - Im Not The Only One","sam"));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Integer> perms = new HashMap();
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        if (requestCode == ALL_PERMISSIONS && grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            createActivity();
                        } else {
                            showAppUnavailableScreen();
                        }
                    } else showAppUnavailableScreen();
                } else showAppUnavailableScreen();
            } else showAppUnavailableScreen();
        }
    }

    private void showAppUnavailableScreen() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        AppUnavailableFragment fragment = new AppUnavailableFragment();
        transaction.add(R.id.activity_feed_frame_layout,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public LiveData<File> onProfilePicNeeded(final String userId, final PostListFragment.onDownloadFinished callback) {
        final LiveData<File> liveData = KaraokeRepository.getInstance().getUserImage(userId);
        liveData.observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
//                liveData.removeObserver(this);
                callback.onDownloadFinished(file);
            }
        });
        return liveData;
    }
}
