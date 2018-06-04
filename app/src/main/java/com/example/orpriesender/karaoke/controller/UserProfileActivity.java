package com.example.orpriesender.karaoke.controller;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.model.FirebaseStorageManager;
import com.example.orpriesender.karaoke.model.KaraokeRepository;
import com.example.orpriesender.karaoke.model.ModelFireBase;
import com.example.orpriesender.karaoke.model.Post;
import com.example.orpriesender.karaoke.model.User;
import com.example.orpriesender.karaoke.util.Util;
import com.example.orpriesender.karaoke.view_model.PostListViewModel;
import com.example.orpriesender.karaoke.view_model.UserProfileViewModel;
import com.example.orpriesender.karaoke.view_model.UserProfileViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class UserProfileActivity extends FragmentActivity implements PostListFragment.onPlayClicked, PostListFragment.onUsernameClicked {
    float userRating = 0;

    TextView username;
    TextView rating;
    ImageView profilePic;
    ImageButton backButton;
    ProgressBar spinner;
    Bitmap imageBitmap;
    Boolean isCurrentUser = false;
    private PostListViewModel postListVM;
    private UserProfileViewModel userProfileVM;

    static final int REQUEST_IMAGE_CAPTURE=1;
    final static int RESULT_SUCCESS = 0;

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


        //fetching the users post list
        postListVM = ViewModelProviders.of(this).get(PostListViewModel.class);
        postListVM.getAllPosts().observe(this, new Observer<List<Post>>() {

            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if (posts.size() > 0) {
                    fragment.setPostsForUser(posts, userId);
                    int userPostAmount = 0;
                    for(int i = 0; i < posts.size(); i++){
                        if(posts.get(i).getUserId().equals(userId)){
                            userPostAmount++;
                            userRating += posts.get(i).getGrade();
                        }
                    }
                    if(userPostAmount > 0)
                        userRating = userRating / userPostAmount;
                    else userRating = 0;
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    rating.setText("AVG: " + formatter.format(userRating));
                }
            }
        });

        //fetching the user
        UserProfileViewModelFactory factory = new UserProfileViewModelFactory(userId);
        userProfileVM = ViewModelProviders.of(this, factory).get(UserProfileViewModel.class);
        userProfileVM.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                username.setText(user.getUsername());
                applyProfilePicture(user);
                spinner.setVisibility(View.GONE);
                isCurrentUser = (user.getId().equals(FirebaseAuth.getInstance().getUid()));
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.super.onBackPressed();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCurrentUser){
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void applyProfilePicture(User user){
        spinner.setVisibility(View.VISIBLE);
        KaraokeRepository.getInstance().getUserImageFromUri(user.getImageUrl(),user.getId()).observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                if(bitmap != null){
                    profilePic.setImageBitmap(bitmap);

                }else{
                    profilePic.setImageResource(R.drawable.default_profile_pic);
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void
    onActivityResult(int requestCode,int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
            File imageFile = Util.saveImageToFile(imageBitmap,FirebaseAuth.getInstance().getUid() + ".jpeg",this);
            spinner.setVisibility(View.VISIBLE);
            FirebaseStorageManager.getInstance().uploadImageForUser(FirebaseAuth.getInstance().getUid(),imageFile, new FirebaseStorageManager.FireBaseStorageUploadCallback() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot task) {
                    Util.presentToast(getApplicationContext(),getActivity(),"Upload succeeded", Toast.LENGTH_SHORT);
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Exception e) {
                    Util.presentToast(getApplicationContext(),getActivity(),"Upload failed", Toast.LENGTH_SHORT);
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onProgress(int progress) {
                    //ignore
                }
            });
        }
    }

    private FragmentActivity getActivity(){
        return this;
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
    public void onUsernameClicked(String userId) {
        //do nothing
    }
}
