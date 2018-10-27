package com.example.kornel.alphaui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
        // implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    public static final String ACTIVITY_RESULT = "result";

    private final int PICK_ACTIVITY_REQUEST = 1;

    // Welcome CardView
    private TextView mWelcomeTextView;
    private TextView mLastTrainingTextView;

    // Weather CardView
    private CardView mWeatherCardView;
    private ImageView mWeatherImageView;
    private TextView mWeatherTextView;
    private TextView mWeatherInfoTextView;
    private TextView mTempTextView;

    // Activity CardView
    private CardView mActivityCardView;
    private ImageView mActivityImageView;
    private TextView mActivityNameTextView;

    // Music CardView
    private CardView mMusicCardView;
    private ImageView mMusicImageView;
    private TextView mSelectMusicTextView;

    private Button mStartActivityButton;

    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mStartActivityButton.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mStartActivityButton.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mStartActivityButton.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWelcomeTextView = findViewById(R.id.welcomeTextView);
        mLastTrainingTextView = findViewById(R.id.lastTrainingTextView);

        mWeatherCardView = findViewById(R.id.weatherCardView);
        mWeatherImageView = findViewById(R.id.weatherImageView);
        mWeatherTextView = findViewById(R.id.weatherTextView);
        mWeatherInfoTextView = findViewById(R.id.weatherInfoTextView);
        mTempTextView = findViewById(R.id.tempTextView);

        mActivityCardView = findViewById(R.id.activityCardView);
        mActivityCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseSportActivity.class);
                startActivityForResult(intent, PICK_ACTIVITY_REQUEST);
            }
        });
        mActivityImageView = findViewById(R.id.activityImageView);
        mActivityNameTextView = findViewById(R.id.activityNameTextView);

        mMusicCardView = findViewById(R.id.musicCardView);
        mMusicCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MUSIC);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mMusicImageView = findViewById(R.id.musicImageView);
        mSelectMusicTextView = findViewById(R.id.selectMusicTextView);

        mWeatherCardView = findViewById(R.id.weatherCardView);

        mStartActivityButton = findViewById(R.id.startActivityButton);
        mStartActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "isSpotifyInstalled: " + isSpotifyInstalled());
                Log.d(TAG, "isGpsEnabled: " + isGpsEnabled());
                Log.d(TAG, "isInternetConnection: " + isInternetConnection());
            }
        });

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ACTIVITY_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(ACTIVITY_RESULT);
                mActivityNameTextView.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isSpotifyInstalled() {
        PackageManager pm = getPackageManager();
        boolean isSpotifyInstalled;
        try {
            pm.getPackageInfo("com.spotify.music", 0);
            isSpotifyInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isSpotifyInstalled = false;
        }
        return isSpotifyInstalled;
    }

    private void openSpotify() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
        intent.putExtra(Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + getPackageName()));
                // Uri.parse("android-app://" + context.getPackageName()));
        startActivity(intent);
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private boolean isInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission")
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    // @Override
    // public void onBackPressed() {
    //     DrawerLayout drawer = findViewById(R.id.drawer_layout);
    //     if (drawer.isDrawerOpen(GravityCompat.START)) {
    //         drawer.closeDrawer(GravityCompat.START);
    //     } else {
    //         super.onBackPressed();
    //     }
    // }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     // Inflate the menu; this adds items to the action bar if it is present.
    //     getMenuInflater().inflate(R.menu.main, menu);
    //     return true;
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //     // Handle action bar item clicks here. The action bar will
    //     // automatically handle clicks on the Home/Up button, so long
    //     // as you specify a parent activity in AndroidManifest.xml.
    //     int id = item.getItemId();
    //
    //     //noinspection SimplifiableIfStatement
    //     if (id == R.id.action_settings) {
    //         return true;
    //     }
    //
    //     return super.onOptionsItemSelected(item);
    // }

    // @SuppressWarnings("StatementWithEmptyBody")
    // @Override
    // public boolean onNavigationItemSelected(MenuItem item) {
    //     // Handle navigation view item clicks here.
    //     int id = item.getItemId();
    //
    //     if (id == R.id.nav_camera) {
    //         // Handle the camera action
    //     } else if (id == R.id.nav_gallery) {
    //
    //     } else if (id == R.id.nav_slideshow) {
    //
    //     } else if (id == R.id.nav_manage) {
    //
    //     } else if (id == R.id.nav_share) {
    //
    //     } else if (id == R.id.nav_send) {
    //
    //     }
    //
    //     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    //     drawer.closeDrawer(GravityCompat.START);
    //     return true;
    // }

    // @Override
    // protected void onCreate(Bundle savedInstanceState) {

        // DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //         this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawer.addDrawerListener(toggle);
        // toggle.syncState();

        // NavigationView navigationView = findViewById(R.id.nav_view);
        // navigationView.setNavigationItemSelectedListener(this);


    // }
}
