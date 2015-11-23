package com.niels.mobilegameofwords;

import android.app.Application;
import android.util.Log;

public class GameOfWords extends Application {

    private String locRelevantWord;

    public String getLocRelevantWord() {
        return locRelevantWord;
    }

    public void setLocRelevantWord(String word) {
        this.locRelevantWord = word;
        Log.d("Niels", "relevant word set: " + word);
    }
}