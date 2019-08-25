package com.retro.emergencyqr.framework.view;

public interface QRScanView extends BaseView {

    void onScanSuccess();
    void onScanFail(String errorMessage);
}
