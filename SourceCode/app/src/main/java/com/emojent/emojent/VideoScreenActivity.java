package com.emojent.emojent;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.support.design.widget.Snackbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;


//import com.microsoft.projectoxford.emotionsample.helper.ImageHelper;

public class VideoScreenActivity extends AppCompatActivity {

    private EmotionServiceClient client;
    private CameraSource mCameraSource = null;
    private List<RecognizeResult> result = new ArrayList<RecognizeResult>();

    private CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes) {
            //Log.d("Debug","OnPictureTaken method");

            mBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            try {
                try {
                    result = client.recognizeImage(inputStream);
                } catch (EmotionServiceException e) {}
            } catch(IOException d) {}
        }
    };
    private CameraSource.ShutterCallback mShutter = new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {
            //Log.d("Debug","OnShutter method");
        }
    };
    //private byte[] data;
    private Bitmap mBitmap;

    private Preview mPreview;
    private Overlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("Test", "Video Screen0");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);

        mPreview = (Preview) findViewById(R.id.preview);
        mGraphicOverlay = (Overlay) findViewById(R.id.faceOverlay);
        //Log.d("Test", "Video Screen1");
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

       if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }
        //Log.d("Test", "Video Screen2");
    }

    private void createCameraSource() {
        //Log.d("Test", "createCameraSource");
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    private void requestCameraPermission() {
        //Log.w("Test", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        /*
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
                */
    }

    @Override
    protected void onResume() {
        //Log.d("Test", "onResume");
        super.onResume();

        startCameraSource();

        //mCameraSource.takePicture(mShutter, mPicture);

        //Log.d("Test", "onResume End");
    }

    @Override
    protected void onPause() {
        //Log.d("Test", "onPause");
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        //Log.d("Test", "onDestroy");
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        //Log.d("Test", "startCameraSource");
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            //.d("Test", "CameraSourcenotnull");
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
        //Log.d("Test", "EndofstartCameraSource");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //Log.d("Test", "onRequestResult");
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {

        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private Overlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(Overlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            mFaceGraphic.updateList(result);
            //Log.d("Debug","onUpdate");
            mCameraSource.takePicture(mShutter,mPicture);

        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}