package com.retro.emergencyqr.framework.view;

/**
 * Created by tommy on 06/June/2019.
 */
public interface BaseView {
    void updateProgressDialog(boolean isShowProgressDialog);
    void showErrorMessageDialog(String errorTitle, String errorMessage, Boolean isBackLogin);
}
