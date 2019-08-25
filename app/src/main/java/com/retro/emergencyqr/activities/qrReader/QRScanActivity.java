package com.retro.emergencyqr.activities.qrReader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.BaseActivity;
import com.retro.emergencyqr.activities.MapsActivity;
import com.retro.emergencyqr.activities.ViewProfileActivity;
import com.retro.emergencyqr.framework.presenter.QRScanPresenter;
import com.retro.emergencyqr.framework.view.QRScanView;
import com.retro.emergencyqr.lib.ui.CameraSourcePreview;
import com.retro.emergencyqr.lib.ui.GraphicOverlay;

/**
 * Created by tommy on 10/June/2019.
 */
public class QRScanActivity extends BaseActivity implements GraphicOverlay.OnScannerCallback, QRScanView {

    private static final String TAG = QRScanActivity.class.getSimpleName();
    private QRScanPresenter qrScanPresenter;

    private final Object mMutex = new Object();

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private GraphicOverlay mGraphicOverlay;

    private View topLayout;

    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_qrscan;
    }

    @Override
    protected void initEvent() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            qrScanPresenter.createCameraSource(mGraphicOverlay, mPreview);
        } else {
            qrScanPresenter.requestCameraPermission();
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
        qrScanPresenter = new QRScanPresenter(this);
        qrScanPresenter.bindView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrScanPresenter.startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        startActivity(new Intent(this, ViewProfileActivity.class));
                    }

                    // Right to left swipe action
                    else {
                        startActivity(new Intent(this, MapsActivity.class));
                    }
                    break;
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        qrScanPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        qrScanPresenter.readBarcode(barcode);
    }

    @Override
    public void onScanSuccess() {
        startActivity(new Intent(this, ViewProfileActivity.class));
    }

    @Override
    public void onScanFail(String errorMessage) {
        Toast.makeText(this, "Scan Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
