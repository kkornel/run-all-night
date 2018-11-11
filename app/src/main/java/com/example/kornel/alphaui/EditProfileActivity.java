package com.example.kornel.alphaui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ProfileInfoValidator;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mBrowseButton;
    private ImageView mAvatarImageView;
    private EditText mFirstNameEditText;
    private EditText mSurnameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mSaveButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setTitle(R.string.edit_profile_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBrowseButton = findViewById(R.id.browseButton);
        mBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseForImage();
            }
        });
        mAvatarImageView = findViewById(R.id.avatarImageView);
        mFirstNameEditText = findViewById(R.id.firstNameEditText);
        mSurnameEditText = findViewById(R.id.surnameEditText);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        mSaveButton = findViewById(R.id.saveChangesProfile);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = firebaseDatabase.getReference(Database.USERS);
        final String userUid = mUser.getUid();
        
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot usersSnapShot : dataSnapshot.getChildren()) {
                    if (usersSnapShot.getKey().equals(userUid)) {
                        User user = usersSnapShot.getValue(User.class);
                        mFirstNameEditText.setHint(user.getFirstName());
                        mSurnameEditText.setHint(user.getSurname());
                        mEmailEditText.setHint(user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void browseForImage() {
        // Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        // startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                mAvatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onSaveClicked() {
        if (!validateForm()) {
            return;
        }

        showConfirmPasswordDialog();

        String firstName = mFirstNameEditText.getText().toString();
        String surname = mSurnameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // createAccount(firstName, surname, email, password);
        Utils.hideKeyboard(this);
    }


    public void showConfirmPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.confirm_password_dialog, null);
        final EditText passwordTextView = vView.findViewById(R.id.passwordEditText);
        final TextView infoTextView = vView.findViewById(R.id.confirmPasswordResultTextView);

        infoTextView.setText("");

        final AlertDialog dialog;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(vView)
                // Add action buttons
                .setPositiveButton(R.string.confirm_password_confirm_button, null)
                .setNegativeButton(R.string.confirm_password_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoTextView.setText("");

                final String password = passwordTextView.getText().toString();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), password);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Zapisano zmiany", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            infoTextView.setText("Złe hasło!");
                        }
                    }
                });
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameEditText.getText().toString();
        // Pattern namePattern = Pattern.compile(NAME_REGEX);
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameEditText.setError(getString(R.string.register_required));
            valid = false;
        } else if (!ProfileInfoValidator.isNameValid(firstName)) {
            // } else if (!namePattern.matcher(firstName).find()) {
            mFirstNameEditText.setError(getString(R.string.register_not_valid_name));
            valid = false;
        } else {
            mFirstNameEditText.setError(null);
        }

        String surname = mSurnameEditText.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            mSurnameEditText.setError(getString(R.string.register_required));
            valid = false;
            // } else if (!namePattern.matcher(surname).find()) {
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
}
