package com.example.kornel.alphaui.gpsworkout;

import android.content.Intent;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.mainactivity.MainActivity;
import com.example.kornel.alphaui.mainactivity.WorkoutLog;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.weather.NetworkUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.example.kornel.alphaui.gpsworkout.StartGpsWorkoutActivity.WORKOUT_DETAILS_EXTRA_INTENT;

public class WorkoutSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "WorkoutSummaryActivity";

    private static final int REQUEST_PICK_IMAGE = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 321;

    private CardView mWorkoutCardView;
    private ImageView mActivityIconImageView;
    private TextView mActivityTypeTextView;
    private TextView mDateTextView;

    private GoogleMap mMap;

    private CardView mSummaryCardView;
    private ImageView mDurationImageView;
    private TextView mDurationTextView;
    private ImageView mAvgPaceImageView;
    private TextView mAvgPaceTextView;
    private ImageView mAvgSpeedImageView;
    private TextView mAvgSpeedTextView;
    private ImageView mDistanceImageView;
    private TextView mDistanceTextView;
    private ImageView mMaxPaceImageView;
    private TextView mMaxPaceTextView;
    private ImageView mMaxSpeedImageView;
    private TextView mMaxSpeedTextView;

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

    private CardView mWeatherCardView;
    private TextView mWeatherSummaryTextView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTempTextView;

    private CardView mLapsCardView;

    private Button mSaveButton;
    private Button mDeleteButton;

    private AlertDialog mCameraGalleryDialog;
    private Bitmap mSelectedPhotoBitmap;
    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        getSupportActionBar().setTitle(R.string.summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView = findViewById(R.id.activityTypeTextView);
        mDateTextView = findViewById(R.id.dateTextView);

        SupportMapFragment map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summaryMap));
        map.getMapAsync(this);

        mSummaryCardView = findViewById(R.id.summaryCardView);
        mDurationImageView = findViewById(R.id.durationImageView);
        mDurationTextView = findViewById(R.id.durationTextView);
        mAvgPaceImageView = findViewById(R.id.avgPaceImageView);
        mAvgPaceTextView = findViewById(R.id.avgPaceTextView);
        mAvgSpeedImageView = findViewById(R.id.avgSpeedImageView);
        mAvgSpeedTextView = findViewById(R.id.avgSpeedTextView);
        mDistanceImageView = findViewById(R.id.distanceImageView);
        mDistanceTextView = findViewById(R.id.distanceTextView);
        mMaxPaceImageView = findViewById(R.id.maxPaceImageView);
        mMaxPaceTextView = findViewById(R.id.maxPaceTextView);
        mMaxSpeedImageView = findViewById(R.id.maxSpeedImageView);
        mMaxSpeedTextView = findViewById(R.id.maxSpeedTextView);

        mStatusCardView = findViewById(R.id.statusCardView);
        mStatusEditText = findViewById(R.id.statusEditText);

        mPhotoCardView = findViewById(R.id.photoCardView);
        mPhotoIconImageView = findViewById(R.id.photoIconImageView);
        mBrowseButton = findViewById(R.id.browseButton);
        mBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraGalleryDialog();
            }
        });
        mSelectedPhotoCardView = findViewById(R.id.selectedPhotoCardView);
        hideSelectedPhotoCardView();
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

        mWeatherCardView = findViewById(R.id.weatherCardView);
        mWeatherSummaryTextView = findViewById(R.id.weatherSummaryTextView);
        mWeatherImageView = findViewById(R.id.weatherImageView);
        mWeatherTempTextView = findViewById(R.id.weatherTempTextView);

        mLapsCardView = findViewById(R.id.lapsCardView);

        final WorkoutGpsSummary workoutSummary = getIntent().getExtras().getParcelable(WORKOUT_DETAILS_EXTRA_INTENT);
        Log.d("finishWorkout", "finishWorkout: " + workoutSummary);
        mSaveButton = findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtils.isConnected(WorkoutSummaryActivity.this)) {
                    requestInternetConnection();
                    return;
                }

                saveWorkout(workoutSummary);

                Toast.makeText(WorkoutSummaryActivity.this, "Workout saved", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(WorkoutSummaryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mDeleteButton = findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WorkoutSummaryActivity.this, "Workout not saved", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(WorkoutSummaryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        mWorkoutCardView = findViewById(R.id.workoutCardView);
        mActivityIconImageView = findViewById(R.id.activityIconImageView);
        mActivityTypeTextView.setText(workoutSummary.getWorkoutName());
        mDateTextView.setText(workoutSummary.getDateStringPlWithTime());



        mDurationTextView.setText(workoutSummary.getDuration());
        mDistanceTextView.setText(workoutSummary.getDistance());
        mAvgPaceTextView.setText(workoutSummary.getAvgPace()+ "min/km");
        mMaxPaceTextView.setText(workoutSummary.getMaxPace()+ "min/km");
        mAvgSpeedTextView.setText(workoutSummary.getAvgSpeed()+ "km/h");
        mMaxSpeedTextView.setText(workoutSummary.getMaxSpeed() + "km/h");


        mWeatherCardView = findViewById(R.id.weatherCardView);
        mWeatherSummaryTextView = findViewById(R.id.weatherSummaryTextView);
        mWeatherImageView = findViewById(R.id.weatherImageView);
        mWeatherTempTextView = findViewById(R.id.weatherTempTextView);

        mLapsCardView = findViewById(R.id.lapsCardView);


        //
        // mTimeTextView.setText(workoutSummary.getDuration());
        //
        // double distance = workoutSummary.getDistance();
        // distance /= 1000;
        // // String.format("%.5g%n", distance);
        // DecimalFormat df = new DecimalFormat("#.##");
        // mDistanceTextView.setText(df.format(distance));
        //
        // mSaveButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //
        //         if (!hasInternetConnection()) {
        //             requestInternetConnection();
        //             return;
        //         }
        //
        //         saveWorkout(workoutSummary);
        //
        //         Toast.makeText(WorkoutSummaryActivity.this, "Workout saved", Toast.LENGTH_LONG).show();
        //
        //         Intent intent = new Intent(WorkoutSummaryActivity.this, MainActivity.class);
        //         startActivity(intent);
        //     }
        // });
        //
        // mDontSaveButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         Toast.makeText(WorkoutSummaryActivity.this, "Workout not saved", Toast.LENGTH_LONG).show();
        //
        //         Intent intent = new Intent(WorkoutSummaryActivity.this, MainActivity.class);
        //         startActivity(intent);
        //     }
        // });
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSelectedImageImageVIew.setImageBitmap(mSelectedPhotoBitmap);

        } else {
            mSelectedPhotoBitmap = null;
            hideSelectedPhotoCardView();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_delete_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_workout:
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_workout:
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveWorkout(WorkoutGpsSummary workoutSummary) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference workoutRef = database.getReference("workouts");
        DatabaseReference userRef = database.getReference(Database.USERS);

        String userUid = user.getUid();
        String key = workoutRef.child(userUid).push().getKey();

        // HashMap<String, Object> result = new HashMap<>();
        // result.put("name", workoutSummary.getWorkoutName());
        // result.put("time", workoutSummary.getDuration());
        // result.put("distance", workoutSummary.getDistance());
        // result.put("path", workoutSummary.g);

        userRef.child(userUid).child("lastWorkout").setValue(key);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/workouts/" + userUid + "/" + key, workoutSummary);
        // childUpdates.put("/users/" + userUid, key);

        database.getReference().updateChildren(childUpdates);
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
                dispatchTakePictureIntent();
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
}
