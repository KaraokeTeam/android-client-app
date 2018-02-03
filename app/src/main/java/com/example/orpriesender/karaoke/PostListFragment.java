package com.example.orpriesender.karaoke;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 03-Feb-18.
 */

public class PostListFragment extends Fragment {

    List<Post> posts;
    FeedAdapter adapter;
    ListView postList;
    ProgressBar spinner;
    TextView noItemsText;
    onUsernameClicked listener;

    public PostListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        posts = new ArrayList<>();
        adapter = new FeedAdapter(getActivity(),posts);
        postList = (ListView) getView().findViewById(R.id.feed_list);
        spinner = (ProgressBar) getView().findViewById(R.id.feed_spinner);
        noItemsText = (TextView) getView().findViewById(R.id.no_items_text);

        try{
            listener = (onUsernameClicked) getActivity();

        }catch(ClassCastException c){
            listener = null;
        }
        //if it is null it will not be used
        adapter.setOnUsernameClickListener(listener);
        //activate spinner
        spinner.setVisibility(View.VISIBLE);
    }

    public void setPosts(List<Post> posts){
        this.posts = posts;
        adapter.setPosts(posts);
        postList.setAdapter(adapter);
        spinner.setVisibility(View.GONE);
    }

    public void setPostsForUser(List<Post> posts, String userId){
        List<Post> results = new ArrayList<>();
        for(Post p : posts){
            if(p.getUserId().equals(userId))
                results.add(p);
        }
        adapter.setPosts(results);
        postList.setAdapter(adapter);
        spinner.setVisibility(View.GONE);
        if(results.size() == 0)
            noItemsText.setVisibility(View.VISIBLE);
    }



    interface onUsernameClicked{
        public void onUsernameClicked(String userId);
    }

}
