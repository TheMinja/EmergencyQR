package com.retro.emergencyqr.framework.manager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.retro.emergencyqr.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tommy on 06/June/2019.
 */
public class FirebaseUIManager {
    private static final String LOG_TAG = "FirebaseUIManager";
    private Activity mActivity;
    private final FirebaseAuth mFirebaseAuth;
    private static FirebaseUIManager INSTANCE;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 1234;
    private CallbackManager mCallbackManager;
    private OnCompleteListener signInFacebookListener;

    /**
     * Get the singleton INSTANCE of this class
     *
     * @return signleton INSTANCE of this class
     */
    public static FirebaseUIManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseUIManager(activity);
        }
        return INSTANCE;
    }

    /**
     * Constructor.
     *
     * @param activity activity.
     */
    private FirebaseUIManager(Activity activity) {
        mActivity = activity;
        configureGoogleSignIn();
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FacebookCallback<LoginResult> mCallback = new Callback();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, mCallback);
    }

    /**
     * Sign In with Email/Password.
     *
     * @param activity           activity.
     * @param email              email.
     * @param password           password.
     * @param onCompleteListener listener.
     */
    public void signInWithEmailAndPassword(Activity activity, final String email,
                                           final String password, OnCompleteListener onCompleteListener) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, onCompleteListener);
    }

    public void createUserWithEmailAndPassword(Activity activity, String username, String password, OnCompleteListener<AuthResult> onCompleteListener){
        mFirebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(activity, onCompleteListener);
    }

    /**
     * Sign Out of firebase.
     */
    public void signOut() {
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut();
        LoginManager.getInstance().logOut();
    }

    /**
     * Fetch providers by email.
     *
     * @param email email.
     * @param task  task.
     */
    public void getProviderByEmail(final String email, OnCompleteListener<SignInMethodQueryResult> task) {
        mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task);
    }

    /**
     * Sign In with Google.
     *
     * @param activity activity.
     */
    public void signInWithGoogle(Activity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Google authentication.
     *
     * @param acct account.
     * @param task listener.
     */
    private void googleSignIn(GoogleSignInAccount acct, OnCompleteListener task) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task);
    }

    /**
     * Proxy to GitkitClient handle activity result
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param intent      intent
     * @return result of GitkitClient handle activity result
     */

    public void handleResult(int requestCode, int resultCode, Intent intent,
                             OnCompleteListener onCompleteListener) {
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    googleSignIn(account, onCompleteListener);
                }
            } catch (ApiException e) {
                onCompleteListener.onComplete(null);
                Log.e(LOG_TAG, "Google sign in failed");
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Config google sign in.
     */
    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
    }

    /**
     * Facebook callback.
     */
    private class Callback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult result) {
            handleFacebookAccessToken(result.getAccessToken());
        }

        @Override
        public void onCancel() {
            onError(new FacebookException());
        }

        @Override
        public void onError(FacebookException e) {
            signInFacebookListener.onComplete(null);
        }
    }

    /**
     * Sign In with Facebook.
     *
     * @param activity activity
     * @param listener listener.
     */
    public void signInWithFacebook(Activity activity, OnCompleteListener listener) {
        signInFacebookListener = listener;
        List<String> permission = Arrays.asList("email", "public_profile");
        LoginManager.getInstance().logInWithReadPermissions(activity, permission);
    }

    /**
     * Handle facebook access token result.
     *
     * @param token access token.
     */
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        if (null != signInFacebookListener) {
            mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(signInFacebookListener);
        }
    }

    public FirebaseUser getCurrentUser(){
        return mFirebaseAuth.getCurrentUser();
    }

}
