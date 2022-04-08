package com.example.facebooksignin;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FacebookSignInManager {

    private static FacebookSignInManager instance = null;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private Context context;
    private Callback callback;

    private FacebookSignInManager() {
    }

    public static FacebookSignInManager getInstance(Context context) {
        if (instance == null) {
            instance = new FacebookSignInManager();
        }
        instance.init(context);
        return instance;
    }

    private void init(Context context) {
        this.context = context;
        this.callback = (Callback) context;
    }

    public void setupFacebookAuth() {
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                                 AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //logged out
                    callback.updateUI();
                    FirebaseAuth.getInstance().signOut();
                } else {

                }
            }
        };

        accessTokenTracker.startTracking();
    }

    public void setLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);

                            getProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    void getProfile(FirebaseUser user) {
        callback.getProfile(user);
    }

    boolean isUserAlreadySignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(context, "Already Signed In.", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Userfound");
            Log.d("TAG", currentUser.getEmail());
            return true;
        } else {
            return false;
        }
    }

    CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    interface Callback {
        void getProfile(FirebaseUser user);

        void updateUI();
    }
}
