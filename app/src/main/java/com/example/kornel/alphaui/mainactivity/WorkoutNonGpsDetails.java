package com.example.kornel.alphaui.mainactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;

import com.example.kornel.alphaui.gpsworkout.WorkoutSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.IconUtils;

import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.NetworkUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.internal.view.SupportMenuItem.SHOW_AS_ACTION_ALWAYS;
import static com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummaryActivity.MAX_CHARS_IN_EDIT_TEXT;
import static com.example.kornel.alphaui.mainactivity.FeedYouFragment.WORKOUT_INTENT_EXTRA;

public class WorkoutNonGpsDetails extends AppCompatActivity {
    private static final String TAG = "WorkoutNonGpsDetails";

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mDateTextView;


    private CardView mSummaryCardView;
    private ImageView mDurationImageView;
    private TextView mDurationTextView;

    private TextView mStatusLabel;
    private CardView mStatusCardView;
    private TextView mStatusTextView;
    private ImageButton mStatusEditButton;

    private TextView mPhotoLabel;
    private CardView mPhotoCardView;
    private ImageView mPhotoImageView;
    private ImageButton mPhotoDeleteButton;

    private CardView mPrivacyCardView;
    private TextView mPrivacyTextView;
    private Button mPrivacyChangeButton;

    private WorkoutSummary mWorkoutSummary;
    private boolean mWorkoutEdited;
    private boolean mPhotoDeleted;

    private Menu mMenu;

    private DatabaseReference mRootRef;

    private String mUserUid;
    private String mWorkoutKey;
    private StorageReference mPicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_non_gps_details);

        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWorkoutSummary = getIntent().getExtras().getParcelable(WORKOUT_INTENT_EXTRA);

        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView = findViewById(R.id.activityTypeTextView);
        mDateTextView = findViewById(R.id.dateTextView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkoutSummary.getWorkoutName()), getApplicationContext().getTheme()));
        } else {
            mActivityIconImageView.setImageDrawable(getResources().getDrawable(IconUtils.getWorkoutIcon(mWorkoutSummary.getWorkoutName())));
        }
        mActivityTypeTextView.setText(mWorkoutSummary.getWorkoutName());
        mDateTextView.setText(mWorkoutSummary.getDateStringPlWithTime());


        mSummaryCardView = findViewById(R.id.summaryCardView);
        mDurationImageView = findViewById(R.id.durationImageView);
        mDurationTextView = findViewById(R.id.durationTextView);

        mDurationTextView.setText(mWorkoutSummary.getDurationString());


        mStatusLabel = findViewById(R.id.statusLabel);
        mStatusCardView = findViewById(R.id.statusCardView);
        mStatusTextView = findViewById(R.id.statusTextView);
        mStatusEditButton = findViewById(R.id.editDescriptionButton);
        mStatusEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditStatus();
            }
        });

        if (mWorkoutSummary.getStatus() == null || mWorkoutSummary.getStatus().equals("")) {
            mStatusTextView.setText(getString(R.string.edit_to_add_description));
        } else {
            mStatusTextView.setText(mWorkoutSummary.getStatus());
        }

        mPhotoLabel = findViewById(R.id.photoLabel);
        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoImageView = findViewById(R.id.photoImageView);
        mPhotoDeleteButton = findViewById(R.id.deletePhotoButton);
        mPhotoDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePhoto();
            }
        });

        if (mWorkoutSummary.getPicUrl() == null || mWorkoutSummary.getPicUrl().equals("")) {
            mPhotoLabel.setVisibility(View.GONE);
            mPhotoCardView.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(mWorkoutSummary.getPicUrl())
                    .into(mPhotoImageView);
        }

        mPrivacyCardView = findViewById(R.id.privacyCardView);
        mPrivacyTextView = findViewById(R.id.privacyTextView);
        mPrivacyTextView.setText(mWorkoutSummary.getPrivacy() ? getString(R.string.only_me) : getString(R.string.friends));
        mPrivacyChangeButton = findViewById(R.id.changePrivacyButton);
        mPrivacyChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangePrivacy();
            }
        });

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mRootRef = database.getReference();

        mUserUid = user.getUid();
        mWorkoutKey = mWorkoutSummary.getWorkoutKey();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        mPicsRef = storageRef.child(Database.PICTURES);

        mPhotoDeleted = false;
        mWorkoutEdited = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mWorkoutEdited) {
            dismissChangesDialogShow();
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mWorkoutEdited) {
            dismissChangesDialogShow();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_delete_workout, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_workout:
                saveChanges();
                return true;
            case R.id.delete_workout:
                deleteWorkout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDeletePhoto() {
        new AlertDialog.Builder(WorkoutNonGpsDetails.this)
                .setTitle(R.string.photo_delete_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePhoto();
                        Toast.makeText(WorkoutNonGpsDetails.this, getString(R.string.photo_will_be_deleted_after_saving), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void deletePhoto() {
        mPhotoLabel.setVisibility(View.GONE);
        mPhotoCardView.setVisibility(View.GONE);
        mPhotoDeleted = true;
        onWorkoutEdited();
    }

    private void onEditStatus() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_description, null);
        final EditText editText = view.findViewById(R.id.editStatusEditText);
        if (mStatusTextView.getText().toString().equals(getString(R.string.edit_to_add_description))) {
            editText.setText("");
        } else {
            editText.setText(mStatusTextView.getText());
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS_IN_EDIT_TEXT)});
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        new AlertDialog.Builder(WorkoutNonGpsDetails.this)
                .setView(view)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newStatus = editText.getText().toString();
                        mStatusTextView.setText(newStatus);
                        mWorkoutSummary.setStatus(newStatus);
                        onWorkoutEdited();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();

        Utils.hideKeyboard(this);
    }

    private void onChangePrivacy() {
        CharSequence[] cs = {getString(R.string.friends), getString(R.string.only_me)};
        final int checkedItemId = mWorkoutSummary.getPrivacy() ? 1 : 0;
        new AlertDialog.Builder(WorkoutNonGpsDetails.this)
                .setTitle(R.string.pick_settings)
                .setSingleChoiceItems(cs, checkedItemId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkedItemId != which) {
                            boolean isPrivate = which == 1 ? true : false;
                            mWorkoutSummary.setPrivacy(isPrivate);
                            if (isPrivate) {
                                mPrivacyTextView.setText(getString(R.string.only_me));
                            } else {
                                mPrivacyTextView.setText(getString(R.string.friends));
                            }
                            onWorkoutEdited();
                        }

                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void onWorkoutEdited() {
        mWorkoutEdited = true;
        mMenu.getItem(0).setVisible(true);
    }

    private void dismissChangesDialogShow() {
        new AlertDialog.Builder(WorkoutNonGpsDetails.this)
                .setTitle(R.string.have_unsaved_changes)
                .setMessage(R.string.still_want_to_exit)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }


    private void saveChanges() {
        if (!NetworkUtils.isConnected(WorkoutNonGpsDetails.this)) {
            NetworkUtils.requestInternetConnection(mWorkoutCardView);
            return;
        }

        if (mPhotoDeleted) {
            deletePhotoFromDatabase();
            mPhotoImageView.setVisibility(View.GONE);
            mWorkoutSummary.setPicUrl(null);
        }

        Map<String, Object> workoutValues = mWorkoutSummary.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + Database.WORKOUTS + "/" + mUserUid + "/" + mWorkoutKey, workoutValues);

        mRootRef.updateChildren(childUpdates);

        mWorkoutEdited = false;

        Toast.makeText(this, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();

        finish();
    }

    private void deleteWorkout() {
        new AlertDialog.Builder(WorkoutNonGpsDetails.this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete_workout)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        delete();

                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void deletePhotoFromDatabase() {
        mPicsRef.child(mUserUid).child(mWorkoutKey + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: photo deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "onCancelled: " + exception.getMessage());
            }
        });
    }

    private void delete() {
        if (!NetworkUtils.isConnected(WorkoutNonGpsDetails.this)) {
            NetworkUtils.requestInternetConnection(mWorkoutCardView);
            return;
        }

        final DatabaseReference userRef = mRootRef.child(Database.USERS);
        final DatabaseReference workoutRef = mRootRef.child(Database.WORKOUTS);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getLastWorkout().equals(mWorkoutKey)) {

                    final ArrayList<WorkoutSummary> workouts = new ArrayList<>();
                    ValueEventListener workoutListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                userRef.child(mUserUid).child(Database.LAST_WORKOUT).setValue(null);
                            } else {
                                WorkoutSummary w1 = null;
                                WorkoutSummary w2 = null;

                                for (DataSnapshot workout : dataSnapshot.getChildren()) {
                                    w1 = workout.getValue(WorkoutSummary.class);

                                    if (w2 == null) {
                                        w2 = w1;
                                        w2.setWorkoutKey(workout.getKey());
                                        continue;
                                    }

                                    if (w1.getDateMilliseconds() > w2.getDateMilliseconds()) {
                                        Log.d(TAG, w1.getDateMilliseconds() + " > " + w2.getDateMilliseconds());
                                        w2 = w1;
                                        w2.setWorkoutKey(workout.getKey());
                                    }
                                }


                                userRef.child(mUserUid).child(Database.LAST_WORKOUT).setValue(w2.getWorkoutKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };

                    workoutRef.child(mUserUid).addListenerForSingleValueEvent(workoutListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userRef.child(mUserUid).addListenerForSingleValueEvent(userListener);

        if (mWorkoutSummary.getPicUrl() != null && !mWorkoutSummary.getPicUrl().equals("")) {
            deletePhotoFromDatabase();
        } else {
            Log.d(TAG, "onSuccess: Workout does not contain photo");
        }

        workoutRef.child(mUserUid).child(mWorkoutKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(WorkoutNonGpsDetails.this, getString(R.string.workout_deleted), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "onCancelled: " + exception.getMessage());
            }
        });
    }
}
