package com.retro.emergencyqr.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.presenter.SignUpPresenter;
import com.retro.emergencyqr.framework.view.SignUpView;

public class SignUpActivity extends BaseActivity implements SignUpView, View.OnClickListener {

    private SignUpPresenter mSignUpPresenter;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtConfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initEvent() {
        findViewById(R.id.signUpButton).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        edtUsername = findViewById(R.id.signUpUsername);
        edtPassword = findViewById(R.id.signUpPassword);
        edtConfPassword = findViewById(R.id.signUpConfPassword);
    }

    @Override
    protected void bindView() {
        mSignUpPresenter = new SignUpPresenter(this);
        mSignUpPresenter.bindView(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpButton: {
                mSignUpPresenter.createUserWithEmailAndPassword(edtUsername.getText().toString(),
                        edtPassword.getText().toString(),
                        edtConfPassword.getText().toString());
                break;
            }
        }
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(SignUpActivity.this, getString(R.string.signup_regiSuccess), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignUpActivity.this, ViewProfileActivity.class));

    }

    @Override
    public void onSignUpFailed(String message) {
        Toast.makeText(SignUpActivity.this, getString(R.string.signup_regiFailed) + message, Toast.LENGTH_SHORT).show();
    }
}
