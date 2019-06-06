package com.retro.emergencyqr.framework.presenter;

import com.retro.emergencyqr.framework.view.BaseView;

/**
 * Created by tommy on 06/June/2019.
 */
public interface Presenter<V extends BaseView> {
    void bindView(V baseView);

    void unbindView();

    void onStop();

    void onResume();
}