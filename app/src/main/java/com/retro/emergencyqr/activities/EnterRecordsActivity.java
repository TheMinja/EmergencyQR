package com.retro.emergencyqr.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.view.RecordsView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;

public class EnterRecordsActivity extends BaseActivity implements RecordsView, View.OnClickListener {

    private EditText name;
    private EditText allergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_records);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_enter_records;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {

    }

    @Override
    public void onRecordSuccess() {

    }

    @Override
    public void onRecordFailed() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.recordsConfirm: {

            }
        }
    }
}
