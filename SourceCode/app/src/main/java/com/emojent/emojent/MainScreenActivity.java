package com.emojent.emojent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Test", "OnCreation");
        setContentView(R.layout.activity_main_screen);
    }

    public void activityRecognize(View v) {
        Log.d("Test", "Activity Recognize function");
        Intent intent = new Intent(this, VideoScreenActivity.class);
        startActivity(intent);
    }
}