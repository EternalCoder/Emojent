package com.emojent.emojent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "Main Screen");
        setContentView(R.layout.activity_main_screen);
    }

    public void selectVideo(@SuppressWarnings("UnusedParameters") View view) {
        Intent videoIntent = new Intent(MainScreenActivity.this, VideoScreenActivity.class);
        MainScreenActivity.this.startActivity(videoIntent);
    }

    public void selectAudio(@SuppressWarnings("UnusedParameters") View view) {
        Intent audioIntent = new Intent(MainScreenActivity.this, AudioScreenActivity.class);
        MainScreenActivity.this.startActivity(audioIntent);
    }
}