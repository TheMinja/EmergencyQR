package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.PrefsDataManager;
import com.retro.emergencyqr.framework.view.ProfileView;

import java.util.ArrayList;

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

    public void setText(){

        String name = readDataPreferences(mActivity.getString(R.string.recordsNameKey));
        String dOB = readDataPreferences(mActivity.getString(R.string.recordsDOBKey));
        String medication = readDataPreferences(mActivity.getString(R.string.recordsMedicationKey));
        String allergies = readDataPreferences(mActivity.getString(R.string.recordsAllergiesKey));
        String hospital = readDataPreferences(mActivity.getString(R.string.recordsHospitalKey));

        if(!name.isEmpty()) {
            getView().setText(0, name);
        }
        if(dOB.isEmpty()) {
            getView().setText(1, dOB);
        }
        if(medication.isEmpty()){
            getView().setText(2, medication);
        }
        if(allergies.isEmpty()){
            getView().setText(3, allergies);
        }
        if(hospital.isEmpty()){
            getView().setText(4, hospital);
        }
    }
}
