package com.retro.emergencyqr.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
        editor.commit();
    }

    /**
     *
     * @return value of string or an empty string if there's no assigned value
     */
    public String readFromPreferences(String key){
        return sharedPreferences.getString(key, "");
    }

}
