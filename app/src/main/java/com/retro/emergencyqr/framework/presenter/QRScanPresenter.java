package com.retro.emergencyqr.framework.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.PrefsDataManager;
import com.retro.emergencyqr.framework.view.QRScanView;
import com.retro.emergencyqr.lib.ui.BarcodeTrackerFactory;
import com.retro.emergencyqr.lib.ui.CameraSourcePreview;
import com.retro.emergencyqr.lib.ui.GraphicOverlay;

import java.io.IOException;

import static com.retro.emergencyqr.utils.Constant.RequestCode.RC_HANDLE_CAMERA_PERM;
import static com.retro.emergencyqr.utils.Constant.RequestCode.RC_HANDLE_GMS;

public class QRScanPresenter extends BasePresenter<QRScanView> {
    private static final String TAG = "QRScanPresenter";
    private Activity mActivity;
    private PrefsDataManager prefsDataManager;

    /**
     * Camera config auto focus. Default is true
     */
    private boolean autoFocus = true;

    /**
     * Camera config request fps.
     */
    private float requestedFps = 15.0f;

    private final Object mMutex = new Object();

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private GraphicOverlay mGraphicOverlay;

    public QRScanPresenter(Activity activity){
        this.mActivity = activity;
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public void readBarcode(Barcode barcode){
        //String needs to be a key, value string
        String value = barcode.rawValue;

        if(value != null || !value.isEmpty()){
            String data = prefsDataManager.readFromPreferences(value);

        } else {
            getView().onScanFail("Wrong key");
        }
    }

    /**
     * Create camera source for preview.
     */
    public void createCameraSource(GraphicOverlay graphicOverlay, CameraSourcePreview preview) {
        mGraphicOverlay = graphicOverlay;
        mPreview = preview;
        Context context = mActivity.getApplicationContext();

        // Create Multi tracker process
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(graphicOverlay);
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
            boolean hasLowStorage = mActivity.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(mActivity, "Low Storage", Toast.LENGTH_LONG).show();
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
                mActivity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(mActivity, code, RC_HANDLE_GMS);
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
    public void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(mActivity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = mActivity;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        mActivity.findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, mActivity.getString(R.string.qr_scan_request_camera_permission_message),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_OK, listener)
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            mActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource(mGraphicOverlay, mPreview);
            startCameraSource();
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.qr_scan_no_qr_detected)
                .setMessage(R.string.qr_scan_request_permission_denied_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


}
