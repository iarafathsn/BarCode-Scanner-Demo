package com.iarafathsn.barcodescannerdemo;

import static com.iarafathsn.barcodescannerdemo.util.CommonUtil.ROW_BARCODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.iarafathsn.barcodescannerdemo.google.mlkit.lib.*;
import com.iarafathsn.barcodescannerdemo.google.mlkit.lib.barcode.BarcodeScannerProcessor;
import com.iarafathsn.barcodescannerdemo.util.BarcodeFoundListener;

import java.io.IOException;

public class LandingActivity extends AppCompatActivity implements BarcodeFoundListener {
    private static final String TAG = "MainTaskActivity";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        startWithCameraPermissionCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resuming Camera Activity...");

        startWithCameraPermissionCheck();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Stopping Camera Activity...");

        preview.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Pausing Camera Activity...");

        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Releasing Camera Source...");

        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private void startWithCameraPermissionCheck() {
        if (ContextCompat.checkSelfPermission(
                LandingActivity.this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startCameraModule();
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCameraModule();
                } else {
                    Toast.makeText(LandingActivity.this,
                            "Camera access is needed to scan.",
                            Toast.LENGTH_LONG).show();
                }
            });

    private void startCameraModule() {
        Log.i(TAG, "Starting Camera Module...");
        createCameraSource();
        startCameraSource();
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
        else {
            Toast.makeText(LandingActivity.this,
                    "Camera access is needed to scan.",
                    Toast.LENGTH_LONG).show();

            Log.i(TAG, "Camera wasn't started..");
        }
    }

    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            Log.i(TAG, "cameraSource was null");
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            Log.i(TAG, "Using Barcode Detector Processor");
            BarcodeScannerProcessor barcodeScannerProcessor = new BarcodeScannerProcessor(this, this);

            cameraSource.setMachineLearningFrameProcessor(barcodeScannerProcessor);
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not create BarcodeScannerProcessor. Error: " + e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onValidBarcodeFound(Barcode barcode) {
        Lifecycle.State currentState = getLifecycle().getCurrentState();

        Log.i(TAG, "Currently is on " + currentState);

        if (currentState != Lifecycle.State.RESUMED) {
            Log.i(TAG, "Currently is not on RESUMED...");
            return;
        }

        Intent myIntent = new Intent(this, BarCodeResultActivity.class);
        myIntent.putExtra(ROW_BARCODE, barcode.getRawValue().toString());
        startActivity(myIntent);
    }
}