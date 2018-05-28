package com.example.orpriesender.karaoke.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.orpriesender.karaoke.controller.TarsosActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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


    public static File saveImageToFile(Bitmap imageBitmap, String imageFileName,Activity activity){
        File imageFile = null;
        try{
            File dir = Environment.getExternalStoragePublicDirectory(
                                    Environment. DIRECTORY_PICTURES);
            if(!dir.exists()) {
                dir.mkdir();
            }
           imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
            out.close();
            addPicureToGallery(imageFile,activity);
            return imageFile;
        }
        catch
                (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch
                (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private static void addPicureToGallery(File imageFile,Activity activity){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        activity.getApplicationContext().sendBroadcast(mediaScanIntent);
    }


}
