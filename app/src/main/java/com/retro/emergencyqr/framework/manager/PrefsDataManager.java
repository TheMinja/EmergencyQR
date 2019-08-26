package com.retro.emergencyqr.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.retro.emergencyqr.R;

import java.util.ArrayList;

public class PrefsDataManager {

    private static PrefsDataManager INSTANCE;
    private Activity mActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static PrefsDataManager getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new PrefsDataManager(activity);
        }
        return INSTANCE;
    }

    public PrefsDataManager(Activity activity){
        mActivity = activity;
        sharedPreferences = mActivity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void writeToPreferences(String key, String value){
        editor.putString(key, value);
    }

    /**
     *
     * @return value of string or an empty string if there's no assigned value
     */
    public String readFromPreferences(String key){
        return sharedPreferences.getString(key, "");
    }

    /**
     *
     * @return true if successful, false if failed
     */
    public Boolean commitToPreferences(){
        return editor.commit();
    }

    public void savePreferencesAsJson(ArrayList<String> keys){
        Gson gson = new Gson();

        ArrayList<String> values = new ArrayList<>();
        for (String key: keys) {
            values.add(readFromPreferences(key));
        }

        writeToPreferences(mActivity.getString(R.string.recordsJson), gson.toJson(values));
    }

}
