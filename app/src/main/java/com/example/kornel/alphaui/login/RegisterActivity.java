package com.example.kornel.alphaui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ProfileInfoValidator;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.example.kornel.alphaui.login.LoginActivity.INTENT_EXTRA_USER_EMAIL;

public class RegisterActivity extends AppCompatActivity {
    private EditText mFirstNameEditText;
    private EditText mSurnameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private Button mBackToLoginButton;

    private TextView mInputValidationError;

    private FirebaseAuth mAuth;

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.register_toolbar_title);

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

        mInputValidationError = findViewById(R.id.inputValidationError);
        mInputValidationError.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(final String firstName, final String surname, final String email, String password) {
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification(user);

                            final String userUid = user.getUid();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference usersRef = database.getReference(Database.USERS);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference defaultRef = storageRef.child(Database.STORAGE_AVATARS).child(Database.DEFAULT_IMG_NAME);
                            defaultRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        String avatarUrl = downloadUri.toString();
                                        if (!avatarUrl.equals("")) {
                                            User newUser = new User(firstName, surname, email);
                                            newUser.setAvatarUrl(avatarUrl);
                                            usersRef.child(userUid).setValue(newUser);
                                            hideProgressDialog();
                                            backToLogin(email);
                                        }
                                    }
                                }
                            });
                        } else {
                            Snackbar.make(
                                    mRegisterButton,
                                    R.string.register_email_in_use,
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.register_forget_password, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetPassword(email);
                                        }
                                    })
                                    .show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void resetPassword(String emailAddress) {
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(
                                    mRegisterButton,
                                    R.string.register_password_reset,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
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
                                    getString(R.string.register_successful),
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Snackbar.make(
                                    mRegisterButton,
                                    R.string.register_failed,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    private void onRegisterClicked() {
        if (!validateForm()) {
            mInputValidationError.setVisibility(View.VISIBLE);
            return;
        }

        Utils.hideKeyboard(this);

        if (!NetworkUtils.isConnected(RegisterActivity.this)) {
            Snackbar.make(
                    mRegisterButton,
                    R.string.no_internet,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .show();
        } else {
            String firstName = mFirstNameEditText.getText().toString();
            String surname = mSurnameEditText.getText().toString();
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            createAccount(firstName, surname, email, password);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameEditText.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEditText.setError(getString(R.string.register_required));
            valid = false;
        } else if (!ProfileInfoValidator.isNameValid(firstName)) {
            mFirstNameEditText.setError(getString(R.string.register_not_valid_name));
            valid = false;
        } else {
            mFirstNameEditText.setError(null);
        }

        String surname = mSurnameEditText.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            mSurnameEditText.setError(getString(R.string.register_required));
            valid = false;
        } else if (!ProfileInfoValidator.isNameValid(firstName)) {
            mSurnameEditText.setError(getString(R.string.register_not_valid_name));
            valid = false;
        } else {
            mSurnameEditText.setError(null);
        }

        String email = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.register_required));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEditText.setError(getString(R.string.register_invalid_email));
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        if (!ProfileInfoValidator.isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.register_password_validation));
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            mPasswordEditText.setError(getString(R.string.register_passwords_not_match));
            mConfirmPasswordEditText.setError(getString(R.string.register_passwords_not_match));
            valid = false;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.register_required));
            valid = false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordEditText.setError(getString(R.string.register_required));
            valid = false;
        } else {
            mPasswordEditText.setError(null);
            mConfirmPasswordEditText.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.register_progress_dialog));
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
