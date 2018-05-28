package com.example.orpriesender.karaoke.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orpriesender.karaoke.R;

/**
 * Created by Or Priesender on 14-May-18.
 */

public class AppUnavailableFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_unavailable, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        View okButton = getView().findViewById(R.id.app_unavailable_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedActivity activity = (FeedActivity)getActivity();
                activity.checkAndRequestPermissions();
                activity.getSupportFragmentManager().popBackStackImmediate();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
