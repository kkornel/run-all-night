package com.example.kornel.alphaui;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText mFirstNameEditText;
    private EditText mSurnameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;

    private FirebaseAuth mAuth;

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstNameEditText = findViewById(R.id.firstNameEditText);
        mSurnameEditText = findViewById(R.id.surnameEditText);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        mRegisterButton = findViewById(R.id.registerButton);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClicked();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String firstName, String surname, String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onRegisterClicked() {
        if (!validateForm()) {
            return;
        }
        String firstName = mFirstNameEditText.getText().toString();
        String surname = mSurnameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        createAccount(firstName, surname, email, password);
    }


    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameEditText.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEditText.setError("Required.");
            valid = false;
        } else {
            mFirstNameEditText.setError(null);
        }

        String surname = mSurnameEditText.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            mSurnameEditText.setError("Required.");
            valid = false;
        } else {
            mSurnameEditText.setError(null);
        }

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Required.");
            valid = false;
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEditText.setError("Invalid email.");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        if (!password.equals(confirmPassword)) {
            mPasswordEditText.setError("Passwords do not match.");
            mConfirmPasswordEditText.setError("Passwords do not match.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
            mConfirmPasswordEditText.setError(null);
        }

        if (!isValidPassword(password)) {
            mPasswordEditText.setError("Valid.. 8chars etc..");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Required.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordEditText.setError("Required.");
            valid = false;
        } else {
            mConfirmPasswordEditText.setError(null);
        }

        return valid;
    }

    private boolean isValidPassword(final String password) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
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
