package com.example.kornel.alphaui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import static com.example.kornel.alphaui.LoginActivity.INTENT_EXTRA_USER_EMAIL;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText mFirstNameEditText;
    private EditText mSurnameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private Button mBackToLoginButton;

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
        mBackToLoginButton = findViewById(R.id.backToLoginButton);
        mBackToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLogin(null);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(final String firstName, final String surname, final String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification(user);

                            String userUid = user.getUid();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference(Database.USERS);

                            usersRef.child(userUid).child(Database.FIRSTNAME).setValue(firstName);
                            usersRef.child(userUid).child(Database.SURNAME).setValue(surname);
                            usersRef.child(userUid).child(Database.EMAIL).setValue(email);

                            hideProgressDialog();
                            backToLogin(email);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(
                                    mRegisterButton,
                                    task.getException().getMessage(),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void backToLogin(String email) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        if (email != null) {
            loginIntent.putExtra(INTENT_EXTRA_USER_EMAIL, email);
        }
        startActivity(loginIntent);
    }

    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Successfully created account. \nVerification email send.",
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Snackbar.make(
                                    mRegisterButton,
                                    "Failed to send verification email.",
                                    Snackbar.LENGTH_LONG)
                                    .show();
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
        Utils.hideKeyboard(this);
    }


    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameEditText.getText().toString();
        Pattern namePattern = Pattern.compile("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEditText.setError("Required.");
            valid = false;
        } else if (!namePattern.matcher(firstName).find()) {
            mFirstNameEditText.setError("Not valid");
            valid = false;
        } else {
            mFirstNameEditText.setError(null);
        }

        String surname = mSurnameEditText.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            mSurnameEditText.setError("Required.");
            valid = false;
        } else if (!namePattern.matcher(surname).find()) {
            mSurnameEditText.setError("Not valid");
            valid = false;
        } else {
            mSurnameEditText.setError(null);
        }

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        } else if (!isPasswordValid(password)) {
            mPasswordEditText.setError("Valid.. 8chars etc..");
            valid = false;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Required.");
            valid = false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordEditText.setError("Required.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
            mConfirmPasswordEditText.setError(null);
        }

        return valid;
    }

    private boolean isPasswordValid(String password) {
        Pattern specialCharsPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern upperCasePattern = Pattern.compile("[A-Z ]");
        Pattern lowerCasePattern = Pattern.compile("[a-z ]");
        Pattern digitsPattern = Pattern.compile("[0-9 ]");
        int passwordMinLength = 8;

        if (password.length() < passwordMinLength) {
            return false;
        }
        if (!specialCharsPattern.matcher(password).find()) {
            return false;
        }
        if (!upperCasePattern.matcher(password).find()) {
            return false;
        }
        if (!lowerCasePattern.matcher(password).find()) {
            return false;
        }
        if (!digitsPattern.matcher(password).find()) {
            return false;
        }

        return true;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Processing...");
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
