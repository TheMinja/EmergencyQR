package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.PrefsDataManager;
import com.retro.emergencyqr.framework.view.RecordsView;

public class RecordPresenter extends BasePresenter<RecordsView> {
    private Activity mActivity;
    private PrefsDataManager prefsDataManager;

    public RecordPresenter(Activity activity){
        this.mActivity = activity;
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public void writeData(String name, String dOB, String allergies, String currentMedication, String hospital){
        getView().updateProgressDialog(true);

        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsNameKey), name);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsDOBKey), dOB);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsAllergiesKey), allergies);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsMedicationKey), currentMedication);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsHospitalKey), hospital);

        if(prefsDataManager.commitToPreferences()){
            getView().updateProgressDialog(false);
            getView().onRecordSuccess();
        }else {
            getView().updateProgressDialog(false);
            getView().onRecordFailed();
        }
    }

    public String readData(String key){
        return prefsDataManager.readFromPreferences(key);
    }

}
