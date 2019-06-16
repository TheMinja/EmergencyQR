package com.retro.emergencyqr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.activities.qrReader.QRScanActivity;
import com.retro.emergencyqr.framework.presenter.LoginPresenter;
import com.retro.emergencyqr.framework.view.LoginView;

public class LoginActivity extends BaseActivity implements LoginView, View.OnClickListener {
    private LoginPresenter mLoginPresenter;
    private EditText edtuserName;
    private EditText edtPassowrd;
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();

        if(mLoginPresenter.getCurrentUser() != null){
            onLoginSuccess();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_log;
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.google_sign_in).setOnClickListener(this);
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
        startActivity(new Intent(this, QRScanActivity.class));
        Log.e(LOG_TAG, "Login success");
        finish();
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        Snackbar.make(findViewById(R.id.loginLayout), R.string.failed_login, Snackbar.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Login Failed:  " + errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: {
                mLoginPresenter.siginWithUsernamePass(edtuserName.getText().toString(), edtPassowrd.getText().toString());
                break;
            }
            case R.id.google_sign_in: {
                mLoginPresenter.signInWithGoogle();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mLoginPresenter.handleGoogleSignInResult(1234, resultCode, data, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task task) {
                if(task == null){
                    onLoginFailed("Login Failed");
                } else if(task.isSuccessful()){
                    mLoginPresenter.getCurrentUser();
                    onLoginSuccess();
                }else {
                    onLoginFailed("Google Login In Failed");
                }
            }
        });
    }


}
