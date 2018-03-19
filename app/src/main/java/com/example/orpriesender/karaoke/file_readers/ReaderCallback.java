package com.example.orpriesender.karaoke.file_readers;

import java.util.List;

/**
 * Created by Or Priesender on 17-Mar-18.
 */

public interface ReaderCallback<T> {
    void onReadingFinished(List<T> results);
}
