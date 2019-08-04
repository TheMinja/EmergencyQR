package com.retro.emergencyqr.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.qrReader.QRScanActivity;
import com.retro.emergencyqr.framework.presenter.RecordPresenter;
import com.retro.emergencyqr.framework.view.RecordsView;

public class EnterRecordsActivity extends BaseActivity implements RecordsView, View.OnClickListener {

    private static final String LOG_TAG = EnterRecordsActivity.class.getSimpleName();

    private EditText hospital;
    private EditText medication;
    private EditText allergies;
    private EditText dOB;
    private EditText name;

    private RecordPresenter mRecordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_enter_records;
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.recordsConfirm).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        hospital = findViewById(R.id.recordsHospital);
        medication = findViewById(R.id.recordsMedications);
        allergies = findViewById(R.id.recordsAllergies);
        dOB = findViewById(R.id.recordsDOB);
        name = findViewById(R.id.recordsName);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void bindView() {
        mRecordPresenter = new RecordPresenter(this);
        mRecordPresenter.bindView(this);
    }

    @Override
    public void onRecordSuccess() {
        Log.d(LOG_TAG, getString(R.string.recordsSuccess));
        startActivity(new Intent(this, QRScanActivity.class));
    }

    @Override
    public void onRecordFailed() {
        Toast.makeText(this, "Could Not Save Records", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.recordsConfirm: {
                mRecordPresenter.writeData(name.getText().toString(),
                        dOB.getText().toString(),
                        allergies.getText().toString(),
                        medication.getText().toString(),
                        hospital.getText().toString());
                //TODO check if successfully saved then start new activity
                startActivity(new Intent(this, ViewProfileActivity.class));
                break;
            }
        }
    }
}
