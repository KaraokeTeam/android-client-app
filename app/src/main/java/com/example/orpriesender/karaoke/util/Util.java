package com.example.orpriesender.karaoke.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.orpriesender.karaoke.controller.TarsosActivity;

/**
 * Created by Or Priesender on 05-Mar-18.
 */

public class Util {
    public static void presentToast(final Context context, Activity activity, final String text, final int length) {
       activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text, length);
                toast.show();
            }
        });
    }
}
