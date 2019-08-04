package com.retro.emergencyqr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.qrReader.QRScanActivity;
import com.retro.emergencyqr.framework.presenter.ViewProfilePresenter;
import com.retro.emergencyqr.framework.view.ProfileView;

import java.util.ArrayList;

public class ViewProfileActivity extends BaseActivity implements ProfileView, View.OnClickListener {

    private ViewProfilePresenter mProfilePresenter;
    private TextView name;
    private TextView age;
    private TextView medication;
    private TextView allergies;
    private TextView hospital;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_profile;
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.profileEmergencyButton).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        name = findViewById(R.id.profileName);
        age = findViewById(R.id.profileAge);
        medication = findViewById(R.id.profileMedication);
        allergies = findViewById(R.id.profileAllergies);
        hospital = findViewById(R.id.profileHospital);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfilePresenter.setText();
    }

    @Override
    protected void bindView() {
        mProfilePresenter = new ViewProfilePresenter(this);
        mProfilePresenter.bindView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileEmergencyButton: {
                startActivity(new Intent(this, QRScanActivity.class));
                break;
            }
        }
    }

    @Override
    public void setText(int index, String text) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(name);
        textViews.add(age);
        textViews.add(medication);
        textViews.add(allergies);
        textViews.add(hospital);

        textViews.get(index).setText(text);
    }
}
