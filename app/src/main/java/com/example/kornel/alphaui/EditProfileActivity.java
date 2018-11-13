package com.example.kornel.alphaui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
    private String mUserUid;
    private FirebaseUser mUser;
    private DatabaseReference mUserRef;

    private String mAvatarUrl;
    private String mAvatarName;
    private Bitmap mAvatarBitmap;
    private String mFirstName;
    private String mSurname;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;

    private ProgressDialog mProgressDialog;

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
        mUserUid = mUser.getUid();

        mUserRef.child(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.ic_person_black_64dp)
                        .error(R.drawable.ic_error_red_64dp)
                        .into(mAvatarImageView);
                mFirstNameEditText.setHint(user.getFirstName());
                mSurnameEditText.setHint(user.getSurname());
                mEmailEditText.setHint(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAvatarName = null;
        mAvatarBitmap = null;
        mAvatarUrl = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();

            mAvatarName = getFileName(uri);

            try {
                mAvatarBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                mAvatarImageView.setImageBitmap(mAvatarBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mAvatarName = null;
            mAvatarBitmap = null;
            mAvatarUrl = null;
        }
    }

    private void onSaveClicked() {
        Utils.hideKeyboard(this);

        if (!validateForm()) {
            return;
        }

        showConfirmPasswordDialog();
    }

    private void uploadChanges() {
        if (!mFirstName.equals("")) {
            mUserRef.child(mUserUid).child(Database.FIRST_NAME).setValue(mFirstName);
        }

        if (!mSurname.equals("")) {
            mUserRef.child(mUserUid).child(Database.SURNAME).setValue(mSurname);
        }

        if (!mEmail.equals("")) {
            mUserRef.child(mUserUid).child(Database.EMAIL).setValue(mEmail);
            mUser.updateEmail(mEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.sendEmailVerification();
                                Toast.makeText(
                                        EditProfileActivity.this,
                                        getString(R.string.verification_email_sent),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }

        if (!mPassword.equals("")) {
            mUser.updatePassword(mPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                            }
                        }
                    });
        }

        if (mAvatarName != null && mAvatarBitmap != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference avatarsRef = storageRef.child(Database.AVATARS_STORAGE);

            String[] separated = mAvatarName.split("\\.");

            String newAvatarName = mUserUid + "." + separated[separated.length - 1];

            final StorageReference newAvatarRef
                    = avatarsRef.child(Database.AVATARS_STORAGE + "/" + newAvatarName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = newAvatarRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return newAvatarRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mAvatarUrl = downloadUri.toString();
                        if (!mAvatarUrl.equals("")) {
                            mUserRef.child(mUserUid).child(Database.AVATAR_URL).setValue(mAvatarUrl);
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

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
                showProgressDialog();

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
                            uploadChanges();
                            hideProgressDialog();
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

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private boolean validateForm() {
        boolean valid = true;

        mFirstName = mFirstNameEditText.getText().toString();
        if (!TextUtils.isEmpty(mFirstName)) {
            if (!ProfileInfoValidator.isNameValid(mFirstName)) {
                mFirstNameEditText.setError(getString(R.string.register_not_valid_name));
                valid = false;
            } else {
                mFirstNameEditText.setError(null);
            }
        }

        mSurname = mSurnameEditText.getText().toString();
        if (!TextUtils.isEmpty(mSurname)) {
            if (!ProfileInfoValidator.isNameValid(mSurname)) {
                mSurnameEditText.setError(getString(R.string.register_not_valid_name));
                valid = false;
            } else {
                mSurnameEditText.setError(null);
            }
        }

        mEmail = mEmailEditText.getText().toString();
        if (!TextUtils.isEmpty(mEmail)) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                mEmailEditText.setError(getString(R.string.register_invalid_email));
                valid = false;
            } else {
                mEmailEditText.setError(null);
            }
        }

        mPassword = mPasswordEditText.getText().toString();
        mConfirmPassword = mConfirmPasswordEditText.getText().toString();
        if (!TextUtils.isEmpty(mPassword)) {
            if (!ProfileInfoValidator.isPasswordValid(mPassword)) {
                mPasswordEditText.setError(getString(R.string.register_password_validation));
                valid = false;
            } else if (!mPassword.equals(mConfirmPassword)) {
                mPasswordEditText.setError(getString(R.string.register_passwords_not_match));
                mConfirmPasswordEditText.setError(getString(R.string.register_passwords_not_match));
                valid = false;
            } else if (TextUtils.isEmpty(mPassword)) {
                mPasswordEditText.setError(getString(R.string.register_required));
                valid = false;
            } else if (TextUtils.isEmpty(mConfirmPassword)) {
                mConfirmPasswordEditText.setError(getString(R.string.register_required));
                valid = false;
            } else {
                mPasswordEditText.setError(null);
                mConfirmPasswordEditText.setError(null);
            }
        }

        return valid;
    }

    private void browseForImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.edit_profile_chooser_title)), PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
