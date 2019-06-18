package com.retro.emergencyqr.framework.manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDataManager {
    private DatabaseReference mDatabase;

    public FirebaseDataManager(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void setProfileData(){

    }
}
