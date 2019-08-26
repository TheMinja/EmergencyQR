package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;
import android.util.Log;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.PrefsDataManager;
import com.retro.emergencyqr.framework.view.RecordsView;

import java.util.ArrayList;

public class RecordPresenter extends BasePresenter<RecordsView> {
    private final String TAG = "RecordPresenter";
    private Activity mActivity;
    private PrefsDataManager prefsDataManager;
    private ArrayList<String> keys = new ArrayList<>();

    public RecordPresenter(Activity activity){
        this.mActivity = activity;
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public void writeData(String name, String dOB, String allergies, String currentMedication, String hospital){
        getView().updateProgressDialog(true);

        keys.add(mActivity.getString(R.string.recordsNameKey));
        keys.add(mActivity.getString(R.string.recordsDOBKey));
        keys.add(mActivity.getString(R.string.recordsAllergiesKey));
        keys.add(mActivity.getString(R.string.recordsMedicationKey));
        keys.add(mActivity.getString(R.string.recordsHospitalKey));

        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsNameKey), name);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsDOBKey), dOB);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsAllergiesKey), allergies);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsMedicationKey), currentMedication);
        prefsDataManager.writeToPreferences(mActivity.getString(R.string.recordsHospitalKey), hospital);
        prefsDataManager.savePreferencesAsJson(keys);

        if(prefsDataManager.commitToPreferences()){
            getView().updateProgressDialog(false);
            getView().onRecordSuccess();
            Log.e(TAG, prefsDataManager.readFromPreferences(mActivity.getString(R.string.recordsJson)));
        }else {
            getView().updateProgressDialog(false);
            getView().onRecordFailed();
        }
    }

    public String readData(String key){
        return prefsDataManager.readFromPreferences(key);
    }

}
