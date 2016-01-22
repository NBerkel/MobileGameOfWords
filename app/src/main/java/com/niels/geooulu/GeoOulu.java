package com.niels.geooulu;

import android.app.Application;
import android.util.Log;

public class GeoOulu extends Application {

    private String locRelevantWord;

    public void setLocRelevantWord(String word) {
        this.locRelevantWord = word;
        Log.d("Niels", "relevant word set: " + word);
    }
}