package com.example.facebooksignin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements FacebookSignInManager.Callback {

    private LoginButton btnSignIn;
    private FacebookSignInManager facebookSignInManager;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignIn);
        txtResult = findViewById(R.id.txtResult);

        facebookSignInManager = FacebookSignInManager.getInstance(this);
        facebookSignInManager.setupFacebookAuth();
        facebookSignInManager.setLoginButton(btnSignIn);
    }

    @Override
    public void onStart() {
        super.onStart();
        facebookSignInManager.isUserAlreadySignedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "!");
        // Pass the activity result back to the Facebook SDK
        facebookSignInManager.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override public void getProfile(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();

            String profile = "Name: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Photo: " + photoUrl + "\n" +
                    "Verified: " + String.valueOf(emailVerified);

            Log.d("TAG", profile);
            txtResult.setText(profile);
        } else {
            Toast.makeText(MainActivity.this, "No account found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void updateUI() {
        txtResult.setText("");
    }

    void getHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.yourappname.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
                Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(),
                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }
    }

}