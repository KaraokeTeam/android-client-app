package com.example.orpriesender.karaoke.model;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by Or Priesender on 02-Mar-18.
 */

//makes saving files locally easy
public class LocalCacheManager {

    private static LocalCacheManager instance = new LocalCacheManager();
    private static Context context;

    private LocalCacheManager() {
    }

    public static LocalCacheManager getInstance() {
        return instance;
    }

    public static void setContext(Context cacheContext) {
        context = cacheContext;
    }

    public static boolean isFileExists(String filename) {
        if (context != null) {
            return new File(context.getCacheDir(), filename).exists();
        }
        return false;
    }


    public File saveOrUpdate(String filename) {
        if (context != null) {
            Log.d("TAG","FILE NAME IS : " + filename);
            File cacheFile = new File(context.getCacheDir(), filename);
            if (cacheFile.exists())
                cacheFile.delete();
            cacheFile.deleteOnExit();
            return cacheFile;
        }
        return null;
    }

    public File getIfExists(String filename) {
        if (context != null) {
            if (isFileExists(filename)) {
                return new File(context.getCacheDir(), filename);
            }
        }
        return null;
    }




/*
How to implement cache ?
put this in the repository function :
 File cachedFile = LocalCacheManager.getIfExists(filename);
        if(cachedFile != null){
            Log.d("LOG","USING CACHED FILE FOR PLAYBACK");
            data.setValue(cachedFile);
            return data;
        }

and put this in the firebase storage/database function :
    final File cacheFile = LocalCacheManager.saveOrUpdate(filename);

    also, if the firebase download failed - delete the file.
 */


}
