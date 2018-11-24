package com.example.kornel.alphaui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kornel.alphaui.mainactivity.MainActivity;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    public static final String INTENT_EXTRA_FIREBASE_USER = "firebase_user";
    public static final String INTENT_EXTRA_USER_EMAIL = "user_email";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mCreateAccountButton;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.login_toolbar_title);

        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mLoginButton = findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(LoginActivity.this)) {
                    Snackbar.make(
                            mLoginButton,
                            R.string.no_internet,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                } else {
                    signIn(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
                }
            }
        });
        mCreateAccountButton = findViewById(R.id.createAccountButton);
        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        if (getIntent().getExtras() != null) {
            String email = getIntent().getStringExtra(INTENT_EXTRA_USER_EMAIL);
            mEmailEditText.setText(email);
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO: Enable this
        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser user = mAuth.getCurrentUser();
        // if (user != null) {
        //     login(user);
        // }
    }

    private void signIn(String email, String password) {
        Utils.hideKeyboard(this);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // hideProgressDialog();

                            // TODO: Enable this
                            if (!user.isEmailVerified()) {
                                Snackbar.make(
                                        mLoginButton,
                                        R.string.login_email_not_verified,
                                        Snackbar.LENGTH_LONG)
                                        .show();
                                // return;
                            }

                            login(user);
                        } else {
                            Snackbar.make(
                                    mLoginButton,
                                    R.string.login_failed,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    private void login(FirebaseUser user) {
        // TODO: Do I need to send user as extra?
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.putExtra(INTENT_EXTRA_FIREBASE_USER, user);
        startActivity(loginIntent);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.login_required));
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.login_required));
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.login_progress_dialog));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
