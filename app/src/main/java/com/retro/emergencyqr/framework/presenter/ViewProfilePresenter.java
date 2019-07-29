package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;

import com.retro.emergencyqr.framework.manager.PrefsDataManager;
import com.retro.emergencyqr.framework.view.ProfileView;

public class ViewProfilePresenter extends BasePresenter<ProfileView> {
    private Activity mActivity;
    private PrefsDataManager prefsDataManager;

    public ViewProfilePresenter(Activity activity){
        mActivity = activity;
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public String readDataPreferences(String key){
        return prefsDataManager.readFromPreferences(key);
    }
}
