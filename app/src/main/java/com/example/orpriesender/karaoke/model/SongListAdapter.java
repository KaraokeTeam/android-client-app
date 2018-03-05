package com.example.orpriesender.karaoke.model;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.orpriesender.karaoke.R;

import java.util.List;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class SongListAdapter extends BaseAdapter {

    List<String> songs;
    private LayoutInflater inflater;

    public SongListAdapter(Activity activity,List<String> songs){
        this.songs = songs;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.song_list_item_layout, null);
        }

        TextView songName = convertView.findViewById(R.id.song_list_item_song_name);
        songName.setText(songs.get(position));

        songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG",((TextView) v).getText().toString());
            }
        });
        return convertView;
    }
}
