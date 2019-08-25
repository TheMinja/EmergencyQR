package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;

import com.retro.emergencyqr.framework.view.MapsView;

public class MapsPresenter extends BasePresenter<MapsView> {
    private Activity mActivity;

    public MapsPresenter(Activity activity){
        this.mActivity = activity;
    }

}
