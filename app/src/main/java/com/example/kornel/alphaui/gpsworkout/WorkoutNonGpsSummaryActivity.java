package com.example.kornel.alphaui.gpsworkout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.BuildConfig;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.mainactivity.MainActivity;
import com.example.kornel.alphaui.mainactivity.WorkoutLog;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.IconUtils;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.example.kornel.alphaui.utils.WorkoutUtils.WORKOUT_DETAILS_EXTRA_INTENT;

public class WorkoutNonGpsSummaryActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutNonGpsSummaryAct";

    public static final int MAX_CHARS_IN_EDIT_TEXT = 100;

    private static final int REQUEST_CODE_PERMISSIONS_WRITE_STORAGE = 79;
    private static final int REQUEST_CODE_PERMISSIONS_CAMERA = 80;

    private static final int REQUEST_PICK_IMAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 321;

    private static final String PHOTO_URI = "uri";

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mDateTextView;

    private CardView mSummaryCardView;
    private ImageView mDurationImageView;
    private TextView mDurationTextView;

    private CardView mStatusCardView;
    private EditText mStatusEditText;

    private CardView mPhotoCardView;
    private ImageView mPhotoIconImageView;
    private Button mBrowseButton;
    private CardView mSelectedPhotoCardView;
    private ImageView mSelectedImageImageVIew;
    private Button mDeletePhotoButton;

    private CardView mPrivacyCardView;
    private Spinner mPrivacySettingsSpinner;

    private Button mSaveButton;
    private Button mDeleteButton;

    private AlertDialog mCameraGalleryDialog;
    private Bitmap mSelectedPhotoBitmap;
    private String mCurrentPhotoPath;
    private String mPhotoName;
    private Uri mCurrentPhotoUri;

    private DatabaseReference mWorkoutRef;
    private DatabaseReference mUserRef;
    private String mUserUid;
    private DatabaseReference mRootRef;

    private WorkoutSummary mWorkoutSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_non_gps_summary);

        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWorkoutSummary = getIntent().getExtras().getParcelable(WORKOUT_DETAILS_EXTRA_INTENT);

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
        mDateTextView.setText(mWorkoutSummary.getFullDateStringPlWithTime());


        mSummaryCardView = findViewById(R.id.summaryCardView);
        mDurationImageView = findViewById(R.id.durationImageView);
        mDurationTextView = findViewById(R.id.durationTextView);

        mDurationTextView.setText(mWorkoutSummary.getDurationString());


        mStatusCardView = findViewById(R.id.statusCardView);
        mStatusEditText = findViewById(R.id.statusEditText);
        mStatusEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS_IN_EDIT_TEXT)});

        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoIconImageView = findViewById(R.id.photoIconImageView);
        mBrowseButton = findViewById(R.id.browseButton);
        mBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasWriteStoragePermissions()) {
                    requestWriteStoragePermissions();
                } else {
                    showCameraGalleryDialog();
                }
            }
        });
        mSelectedPhotoCardView = findViewById(R.id.selectedPhotoCardView);
        mSelectedImageImageVIew = findViewById(R.id.selectedImageImageView);
        mDeletePhotoButton = findViewById(R.id.deletePhotoButton);
        mDeletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedPhoto();
            }
        });

        mPrivacyCardView = findViewById(R.id.privacyCardView);
        mPrivacySettingsSpinner = findViewById(R.id.privacySettingsSpinner);


        mSaveButton = findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });
        mDeleteButton = findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWorkout();
            }
        });

        if (savedInstanceState != null) {
            mCurrentPhotoUri = savedInstanceState.getParcelable(PHOTO_URI);
            try {
                mSelectedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCurrentPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mPhotoName = "";
            mSelectedPhotoBitmap = null;
        }

        if (mSelectedPhotoBitmap == null && mPhotoName.equals("")) {
            hideSelectedPhotoCardView();
        } else {
            mSelectedImageImageVIew.setImageBitmap(mSelectedPhotoBitmap);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PHOTO_URI, mCurrentPhotoUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCameraGalleryDialog != null) {
            mCameraGalleryDialog.dismiss();
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();

            mPhotoName = getFileName(uri);
            showSelectedPhotoCardView();

            try {
                mSelectedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                mSelectedImageImageVIew.setImageBitmap(mSelectedPhotoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            showSelectedPhotoCardView();
            galleryAddPic();

            try {
                mSelectedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCurrentPhotoUri);

                mSelectedPhotoBitmap = rotateImage(mSelectedPhotoBitmap, 90);

                mSelectedImageImageVIew.setImageBitmap(mSelectedPhotoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mSelectedPhotoBitmap = null;
            hideSelectedPhotoCardView();
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        deleteWorkout();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_delete_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_workout:
                saveWorkout();
                return true;
            case R.id.delete_workout:
                deleteWorkout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveWorkout() {
        if (!NetworkUtils.isConnected(WorkoutNonGpsSummaryActivity.this)) {
            requestInternetConnection();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mRootRef = database.getReference();
        mWorkoutRef = mRootRef.child(Database.WORKOUTS);
        mUserRef = mRootRef.child(Database.USERS);

        mUserUid = user.getUid();
        final String key = mWorkoutRef.child(mUserUid).push().getKey();

        String status = mStatusEditText.getText().toString();
        if (status != null && !status.equals("")) {
            mWorkoutSummary.setStatus(status);
        }

        String privacy = mPrivacySettingsSpinner.getItemAtPosition(mPrivacySettingsSpinner.getSelectedItemPosition()).toString();

        mWorkoutSummary.setPrivacy(privacy.equals(getString(R.string.only_me)));

        if ((mPhotoName != null || !mPhotoName.equals("")) && mSelectedPhotoBitmap != null) {
            uploadImage(key);
        } else {
            uploadWorkout(key);
        }
    }

    private void uploadImage(final String key) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference picsRef = storageRef.child(Database.PICTURES);

        String[] separated = mPhotoName.split("\\.");

        String newPhotoName = key + "." + separated[separated.length - 1];

        final StorageReference userWorkoutPicRef = picsRef.child(mUserUid + "/" + newPhotoName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mSelectedPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userWorkoutPicRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(WorkoutNonGpsSummaryActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: " + exception.getMessage());
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
                    Toast.makeText(WorkoutNonGpsSummaryActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return userWorkoutPicRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String photoUrl = downloadUri.toString();
                    onUploadCompleted(photoUrl, key);
                } else {
                    Toast.makeText(WorkoutNonGpsSummaryActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onUploadCompleted(String photoUri, String key) {
        mWorkoutSummary.setPicUrl(photoUri);

        uploadWorkout(key);
    }

    private void uploadWorkout(final String key) {
        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int totalWorkouts = user.getTotalWorkouts() + 1;
                long totalDuration = user.getTotalDuration() + mWorkoutSummary.getDuration();

                mUserRef.child(mUserUid).child(Database.LAST_WORKOUT).setValue(key);
                mUserRef.child(mUserUid).child(Database.TOTAL_DURATION).setValue(totalDuration);
                mUserRef.child(mUserUid).child(Database.TOTAL_WORKOUTS).setValue(totalWorkouts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        mUserRef.child(mUserUid).addListenerForSingleValueEvent(userEventListener);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + Database.WORKOUTS + "/" + mUserUid + "/" + key, mWorkoutSummary);

        mRootRef.updateChildren(childUpdates);

        Toast.makeText(WorkoutNonGpsSummaryActivity.this, getString(R.string.workout_saved), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(WorkoutNonGpsSummaryActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteWorkout() {
        new AlertDialog.Builder(WorkoutNonGpsSummaryActivity.this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete_workout)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(WorkoutNonGpsSummaryActivity.this, getString(R.string.workout_deleted), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WorkoutNonGpsSummaryActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    private void browseForImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.edit_profile_chooser_title)), REQUEST_PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                WorkoutLog.printStack(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.kornel.alphaui",
                        photoFile);

                mCurrentPhotoUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mPhotoName = getFileName(contentUri);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showCameraGalleryDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.select_photo_dialog, null);
        final Button cameraButton = vView.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasCameraPermissions()) {
                    requestCameraPermissions();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        final Button galleryButton = vView.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseForImageIntent();
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(vView).setTitle(R.string.pick_camera_gallery);

        mCameraGalleryDialog = builder.create();
        mCameraGalleryDialog.show();
    }

    private void deleteSelectedPhoto() {
        mSelectedPhotoBitmap = null;
        mSelectedImageImageVIew.setImageBitmap(mSelectedPhotoBitmap);
        hideSelectedPhotoCardView();
    }

    private void hideSelectedPhotoCardView() {
        mSelectedPhotoCardView.setVisibility(View.GONE);
    }

    private void showSelectedPhotoCardView() {
        mSelectedPhotoCardView.setVisibility(View.VISIBLE);
    }

    public void requestInternetConnection() {
        Snackbar.make(
                mSummaryCardView,
                R.string.enable_internet,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        startActivity(viewIntent);
                    }
                })
                .show();
    }

    private boolean hasCameraPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(WorkoutNonGpsSummaryActivity.this,
                            Manifest.permission.CAMERA);

            if (shouldProvideRationale) {
                ActivityCompat.requestPermissions(WorkoutNonGpsSummaryActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSIONS_CAMERA);
            } else {
                showApplicationSettingsSnackBar();
            }
        }
    }

    private boolean hasWriteStoragePermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(WorkoutNonGpsSummaryActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (shouldProvideRationale) {
                ActivityCompat.requestPermissions(WorkoutNonGpsSummaryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSIONS_WRITE_STORAGE);
            } else {
                showApplicationSettingsSnackBar();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(WorkoutNonGpsSummaryActivity.this, getString(R.string.camera_permission_rationale), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_CODE_PERMISSIONS_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(WorkoutNonGpsSummaryActivity.this, getString(R.string.storage_permission_rationale), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void openApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showApplicationSettingsSnackBar() {
        Snackbar.make(
                mSaveButton,
                R.string.permission_rationale_settings,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApplicationSettings();
                    }
                })
                .show();
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
}
