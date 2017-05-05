package com.emojent.emojent;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;
import android.content.Context;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
//import com.microsoft.projectoxford.emotionsample.helper.ImageHelper;


public class VideoScreenActivity extends AppCompatActivity {


    //android:onClick="VideoScreenActivity"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test", "Video Screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);
    }

    /*
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    */

}