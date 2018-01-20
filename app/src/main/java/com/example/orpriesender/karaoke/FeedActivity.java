package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends Activity {

    List<Post> posts = new ArrayList<>();
    FeedAdapter adapter;
    ListView postList;
    ProgressBar spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        postList = (ListView) findViewById(R.id.feed_list);
        spinner = (ProgressBar) findViewById(R.id.feed_spinner);
        spinner.setVisibility(View.VISIBLE);
        adapter = new FeedAdapter(this, posts);
        postList.setAdapter(adapter);
        ModelFirebase.getInstance().getAllPosts(new ModelFirebase.GetAllPostsCallback() {
            @Override
            public void onComplete(List<Post> posts) {
                adapter.setPosts(posts);
                postList.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                spinner.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(),"Loading failed",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,200);
                toast.show();
            }
        });

    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
