package com.retro.emergencyqr.framework.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.retro.emergencyqr.framework.manager.FirebaseUIManager;
import com.retro.emergencyqr.framework.view.LoginView;

/**
 * Created by tommy on 06/June/2019.
 */
public class LoginPresenter extends BasePresenter<LoginView> {
    private Activity mActivity;
    private FirebaseUIManager firebaseUIManager;

    public LoginPresenter(Activity activity) {
        this.mActivity = activity;
        firebaseUIManager = FirebaseUIManager.getInstance(mActivity);
    }

    /**
     * Sign In firebase with Username and Password.
     *
     * @param userName username.
     * @param password password.
     */
    public void siginWithUsernamePass(final String userName, final String password) {
        if (getView() != null) {
            getView().updateProgressDialog(true);
        }
        if (!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(password)) {
            firebaseUIManager.signInWithEmailAndPassword(mActivity, userName, password, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (null != task) {
                            if (task.isSuccessful()) {
                            FirebaseUser user = ((Task<AuthResult>) task).getResult().getUser();
                            handleFirebaseUser(user);
                        } else {
                            getView().updateProgressDialog(false);
                            getView().onLoginFailed("Login Error");
                        }
                    } else {
                        getView().updateProgressDialog(false);
                        getView().onLoginFailed("Login Error");
                    }
                }
            });
        }
    }

    public void signInWithGoogle(){
        firebaseUIManager.signInWithGoogle(mActivity);

    }

    public void handleGoogleSignInResult(int requestCode, int resultCode, Intent intent, OnCompleteListener onCompleteListener){
        firebaseUIManager.handleResult(requestCode, resultCode, intent, onCompleteListener);
    }


    /**
     * Handle firebase user return.
     *
     * @param user user.
     */
    private void handleFirebaseUser(final FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    final String token = task.getResult().getToken();
                    final String gitId = user.getUid();
                    getView().onLoginSuccess();
                } else {
                    getView().onLoginFailed(task.getException().toString());
                }
            }
        });
    }

    public FirebaseUser getCurrentUser(){
        return firebaseUIManager.getCurrentUser();
    }
}
