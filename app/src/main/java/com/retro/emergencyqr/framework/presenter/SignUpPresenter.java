package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.FirebaseUIManager;
import com.retro.emergencyqr.framework.view.SignUpView;

public class SignUpPresenter extends BasePresenter<SignUpView> {
    private Activity mActivity;
    private FirebaseUIManager firebaseUIManager;

    public SignUpPresenter(Activity activity) {
        mActivity = activity;
        firebaseUIManager = FirebaseUIManager.getInstance(mActivity);
    }

    public void createUserWithEmailAndPassword(String username, String password, String confPassword){

        getView().updateProgressDialog(true);

        if(!password.equals(confPassword) || password.isEmpty()){
            getView().updateProgressDialog(false);
            getView().onSignUpFailed(mActivity.getString(R.string.signup_passwordMatchError));
        }else if(username.isEmpty()) {
            getView().updateProgressDialog(false);
            getView().onSignUpFailed(mActivity.getString(R.string.signup_usernameBlankError));
        }else {
            firebaseUIManager.createUserWithEmailAndPassword(mActivity, username, password, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        getView().updateProgressDialog(false);
                        getView().onSignUpSuccess();
                    }else{
                        getView().updateProgressDialog(false);
                        getView().onSignUpFailed(mActivity.getString(R.string.signup_firebaseError));

                    }
                }
            });
        }
    }
}
