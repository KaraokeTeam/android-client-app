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


    public static void destroyCache() {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
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



