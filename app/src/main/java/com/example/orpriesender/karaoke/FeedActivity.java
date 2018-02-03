package com.example.orpriesender.karaoke;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends FragmentActivity implements PostListFragment.onUsernameClicked{

    ImageButton profileButton,singButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);
        profileButton = findViewById(R.id.all_posts_profile_button);
        singButton = findViewById(R.id.all_posts_sing_button);

        final PostListFragment fragment = (PostListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        ModelFirebase.getInstance().getAllPosts(new ModelFirebase.FirebaseCallback<List<Post>>() {
            @Override
            public void onComplete(List<Post> posts) {
                fragment.setPosts(posts);
            }

            @Override
            public void onCancel() {
                //spinner.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(),"Loading feed failed",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,200);
                toast.show();
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

    private void startSingActivity(){
        Intent intent = new Intent(getApplicationContext(), TarsosActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity(String userId){
        Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    @Override
    public void onUsernameClicked(String userId) {
        startProfileActivity(userId);
    }
}
