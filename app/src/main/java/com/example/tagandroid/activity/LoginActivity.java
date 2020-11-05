package com.example.tagandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tagandroid.R;
import com.example.tagandroid.analytics.AnalyticsEvents;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private AnalyticsEvents analyticsEvents;
    private Tracker analyticsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Sign in");
        auth = FirebaseAuth.getInstance();
        analyticsEvents = AnalyticsEvents.getAnalyticsEventsInstance();
        setLoginButtonConfig();
        setGoogleSignInConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = analyticsEvents.getDefaultTracker(this);
        sendScreenViewToAnalytics();
        sendVisitEventToAnalytics();
    }

    private void sendVisitEventToAnalytics() {
        analyticsTracker.setScreenName("Login Screen");
        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("User")
                .setAction("Visit")
                .setCustomDimension(1, "false")
                .setCustomDimension(2, null)
                .build());
    }

    private void sendScreenViewToAnalytics() {
        analyticsTracker.setScreenName("Login Screen");
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

        builder
                .setCustomDimension(1, "false")
                .setCustomDimension(2, null);

        analyticsTracker.send(builder.build());
    }


    private void setLoginButtonConfig() {
        Button loginButton = findViewById(R.id.activity_login_signin_button);
        final Context context = this;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyticsEvents.login("App Login", context);
                analyticsEvents.getFirebaseAnalytics().setUserId("123abc456def");
                sendLoginEventToAnalytics("App Login");
                openProductsActivity();
                Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendLoginEventToAnalytics(String method) {
        analyticsTracker.setScreenName("Login Screen");
        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("User")
                .setAction("Login")
                .setLabel(method)
                .build());
    }

    private void setGoogleSignInConfig() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton loginGoogleButton = findViewById(R.id.activity_login_google_button);

        loginGoogleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("SGF", "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final Context context = this;
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            analyticsEvents.login("Google Login", context);
                            openProductsActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Could not log in user", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void openProductsActivity() {
        startActivity(new Intent(this, ProductsActivity.class));
        finish();
        Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_LONG).show();
    }

}
