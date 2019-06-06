package com.retro.emergencyqr.framework.view;

import android.content.Intent;

/**
 * Created by tommy on 06/June/2019.
 */
public interface LoginView extends BaseView {
    void onLoginSuccess();

    void onLoginFailed(String errorMessage);
}
