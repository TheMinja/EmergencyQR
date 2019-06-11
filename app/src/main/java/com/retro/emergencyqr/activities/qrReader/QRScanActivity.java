package com.retro.emergencyqr.activities.qrReader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.BaseActivity;
import com.retro.emergencyqr.lib.ui.BarcodeTrackerFactory;
import com.retro.emergencyqr.lib.ui.CameraSourcePreview;
import com.retro.emergencyqr.lib.ui.GraphicOverlay;

import java.io.IOException;

import static com.retro.emergencyqr.utils.Constant.RequestCode.RC_HANDLE_CAMERA_PERM;
import static com.retro.emergencyqr.utils.Constant.RequestCode.RC_HANDLE_GMS;

/**
 * Created by tommy on 10/June/2019.
 */
public class QRScanActivity extends BaseActivity implements GraphicOverlay.OnScannerCallback {

    private static final String TAG = QRScanActivity.class.getSimpleName();

    private final Object mMutex = new Object();

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private GraphicOverlay mGraphicOverlay;

    private View topLayout;

    /**
     * Camera config auto focus. Default is true
     */
    private boolean autoFocus = true;

    /**
     * Camera config request fps.
     */
    private float requestedFps = 15.0f;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_qrscan;
    }

    @Override
    protected void initEvent() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void initView() {
        mPreview = findViewById(R.id.qrscan_preview);
        mGraphicOverlay = findViewById(R.id.qrscan_graphic_overlay);
        mGraphicOverlay.setOnScannerCallback(this);
        topLayout = findViewById(R.id.topLayout);
    }

    @Override
    protected void bindView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    /**
     * Create camera source for preview.
     */
    public void createCameraSource() {
        Context context = getApplicationContext();

        // Create Multi tracker process
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(autoFocus)
                .setRequestedFps(requestedFps);

        mCameraSource = builder
                .build();
    }

    /**
     * Start camera source
     */
    public void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                synchronized (mMutex) {
                    mPreview.start(mCameraSource, mGraphicOverlay);
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source." + e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Request camera permission
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

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

        topLayout.setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, getString(R.string.qr_scan_request_camera_permission_message),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_OK, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            startCameraSource();
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.qr_scan_no_qr_detected)
                .setMessage(R.string.qr_scan_request_permission_denied_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Stops the camera.
     */
    public void stopCamera() {
        synchronized (mMutex) {
            if (mPreview != null) {
                mPreview.stop();
            }
        }
    }

    @Override
    public void onNew(Barcode barcode) {

    }
}
