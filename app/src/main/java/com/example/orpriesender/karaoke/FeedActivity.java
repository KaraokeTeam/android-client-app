package com.example.orpriesender.karaoke;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends FragmentActivity implements PostListFragment.onUsernameClicked{

    ImageButton profileButton,singButton;
    PostListViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);

        LocalCacheManager.setContext(getApplicationContext());

        profileButton = findViewById(R.id.all_posts_profile_button);
        singButton = findViewById(R.id.all_posts_sing_button);

        for(int i=0; i<10;i++){
            KaraokeRepository.getInstance().addPost(new Post(FirebaseAuth.getInstance().getUid(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),"post number " + i,"zlil meitar"));
        }


        final PostListFragment fragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);

        vm = ViewModelProviders.of(this).get(PostListViewModel.class);
        vm.getAllPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if(posts != null)
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
