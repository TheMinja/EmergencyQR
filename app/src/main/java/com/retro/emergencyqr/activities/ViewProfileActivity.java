package com.retro.emergencyqr.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.presenter.ViewProfilePresenter;
import com.retro.emergencyqr.framework.view.ProfileView;

public class ViewProfileActivity extends BaseActivity implements ProfileView, View.OnClickListener {

    private ViewProfilePresenter mProfilePresenter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_profile;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        mProfilePresenter = new ViewProfilePresenter(this);
        mProfilePresenter.bindView(this);
    }

    @Override
    public void onClick(View v) {

    }
}
