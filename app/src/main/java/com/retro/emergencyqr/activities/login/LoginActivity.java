package com.retro.emergencyqr.activities.login;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.BaseActivity;
import com.retro.emergencyqr.framework.presenter.LoginPresenter;
import com.retro.emergencyqr.framework.view.LoginView;

public class LoginActivity extends BaseActivity implements LoginView, View.OnClickListener {
    private LoginPresenter mLoginPresenter;
    private EditText edtuserName;
    private EditText edtPassowrd;
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_log;
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        edtuserName = findViewById(R.id.edtUsername);
        edtPassowrd = findViewById(R.id.edtPassword);
    }

    @Override
    protected void bindView() {
        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.bindView(this);
    }

    @Override
    public void onLoginSuccess() {
        // TODO: Navigate to main app.
        Log.e(LOG_TAG, "Login success");

    }

    @Override
    public void onLoginFailed(String errorMessage) {
        Log.e(LOG_TAG, "Login Failed:  "+errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: {
                mLoginPresenter.siginWithUsernamePass(edtuserName.getText().toString(), edtPassowrd.getText().toString());
                break;
            }
        }
    }
}
