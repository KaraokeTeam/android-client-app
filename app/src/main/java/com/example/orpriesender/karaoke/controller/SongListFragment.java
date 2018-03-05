package com.example.orpriesender.karaoke.controller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.orpriesender.karaoke.R;
import com.example.orpriesender.karaoke.model.FeedAdapter;
import com.example.orpriesender.karaoke.model.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class SongListFragment extends Fragment {
    private List<String> songs;
    private SongListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        songs = new ArrayList<>();
        songs.add("zlil");
        songs.add("zlil with singer");
        adapter = new SongListAdapter(getActivity(), songs);

    }
}
