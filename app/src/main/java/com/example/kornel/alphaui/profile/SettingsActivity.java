package com.example.kornel.alphaui.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;
import com.example.kornel.alphaui.login.LoginActivity;
import com.example.kornel.alphaui.utils.Database;
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

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private Button mEditProfileButton;
    private Button mLogoutButton;
    private Button mDeleteAccountButton;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private String mUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mEditProfileButton = findViewById(R.id.editProfileButton);
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        mLogoutButton = findViewById(R.id.logoutButton);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        mDeleteAccountButton = findViewById(R.id.deleteAccountButton);
        mDeleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                mUserUid = user.getUid();
                showConfirmationDialog();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage(R.string.deletion_account_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int id) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                        final LayoutInflater inflater = getLayoutInflater();

                        View vView = inflater.inflate(R.layout.confirm_password_dialog, null);
                        final EditText passwordTextView = vView.findViewById(R.id.passwordEditText);
                        final TextView infoTextView = vView.findViewById(R.id.confirmPasswordResultTextView);

                        infoTextView.setText("");

                        final AlertDialog dialog;

                        builder.setView(vView)
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
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            mRootRef = database.getReference();
                                            mUsersRef = mRootRef.child(Database.USERS);

                                            Executor executor = Executors.newSingleThreadExecutor();

                                            Runnable r1 = new Runnable() {
                                                @Override
                                                public void run() {
                                                    deleteUserStorage();
                                                }
                                            };

                                            Runnable r2 = new Runnable() {
                                                @Override
                                                public void run() {
                                                    deleteUserDatabase();
                                                }
                                            };

                                            Runnable r3 = new Runnable() {
                                                @Override
                                                public void run() {
                                                    deleteUser();
                                                }
                                            };

                                            executor.execute(r1);
                                            executor.execute(r2);
                                            executor.execute(r3);

                                            hideProgressDialog();
                                            dialog.dismiss();
                                        } else {
                                            infoTextView.setText(getString(R.string.wrong_password));
                                            hideProgressDialog();
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

    private void deleteUserStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference avatarsRef = storageRef.child(Database.STORAGE_AVATARS);

        StorageReference userAvatarsRef = avatarsRef.child(mUserUid + ".jpg");

        userAvatarsRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Storage avatar deleted." );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Storage avatar deletion error. -> " + exception.getMessage());
            }
        });

        DatabaseReference workoutsRef = mRootRef.child(Database.WORKOUTS).child(mUserUid);

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    WorkoutSummary workoutSummary = ds.getValue(WorkoutSummary.class);
                    String picUrl = workoutSummary.getPicUrl();

                    if (picUrl != null && !picUrl.equals("")) {
                        StorageReference userPicturesRef = FirebaseStorage.getInstance().getReferenceFromUrl(picUrl);
                        userPicturesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Storage pictures deleted." );
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG, "onFailure: Storage pictures deletion error. -> " + exception.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });
    }

    private void deleteUserDatabase() {
                deleteFriendRequests();
    }

    private void deleteUserRootDb() {
        mUsersRef.child(mUserUid).removeValue();
    }

    private void deleteFriendRequests() {
        final DatabaseReference reqRef = mRootRef.child(Database.FRIENDS_REQUESTS);
        final DatabaseReference userReqRef = reqRef.child(mUserUid);

        userReqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String friendId = ds.getKey();
                    reqRef.child(friendId).child(mUserUid).removeValue();
                }
                userReqRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteFriendsList();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });
    }

    private void deleteFriendsList() {
        DatabaseReference userFriendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);

        userFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Boolean> friends = (HashMap) dataSnapshot.getValue();

                for (final String friendUid : friends.keySet()) {
                    mUsersRef.child(mUserUid).child(Database.FRIENDS).child(friendUid).removeValue();
                    mUsersRef.child(friendUid).child(Database.FRIENDS).child(mUserUid).removeValue();
                }

                deleteWorkouts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });
    }

    public void deleteWorkouts() {
        DatabaseReference workoutsRef = mRootRef.child(Database.WORKOUTS).child(mUserUid);

        workoutsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteSharedLocations();
            }
        });
    }

    private void deleteSharedLocations() {
        final DatabaseReference sharedLocRef = mRootRef.child(Database.SHARED_LOCATIONS);

        mUsersRef.child(mUserUid).child(Database.SHARED_LOCATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Boolean> sharedLocations = (HashMap) dataSnapshot.getValue();

                for (final String sharedLoc : sharedLocations.keySet()) {
                    sharedLocRef.child(sharedLoc).child(mUserUid).removeValue();
                }

                deleteUserRootDb();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        });
    }

    private void deleteUser() {
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(
                                    SettingsActivity.this,
                                    getString(R.string.account_deleted),
                                    Toast.LENGTH_LONG)
                                    .show();
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
