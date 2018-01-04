package com.example.orpriesender.karaoke;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Or Priesender on 03-Jan-18.
 * This class will control each of the db references, which will be managed separately.
 */

public class ModelFirebase {
    private static final ModelFirebase ourInstance = new ModelFirebase();

    public static ModelFirebase getInstance() {
        return ourInstance;
    }

    private ModelFirebase() {

    }
}
