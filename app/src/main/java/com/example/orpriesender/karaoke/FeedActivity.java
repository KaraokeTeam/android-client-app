package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 05-Jan-18.
 */

public class FeedActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        List<Post> posts = new ArrayList<>();
        for(int i=0;i<20;i++){
           // posts.add(new Post(i,"user" + i,"bla bla bla bla bla bla bla bla bla bla bla bla ", "song " + i));
        }

        FeedAdapter adapter = new FeedAdapter(this,posts);
        ListView list = (ListView) findViewById(R.id.feed_list);
        list.setAdapter(adapter);
    }
}
