package com.example.kornel.alphaui.mainactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kornel.alphaui.profile.ProfileDetailsActivity;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.weather.WeatherLog;

public class MainActivity extends AppCompatActivity {
    private static final String MORE_FRAGMENT = "MoreFragment";
    private static final String FEED_FRAGMENT = "FeedFragment";
    private static final String WORKOUT_FRAGMENT = "WorkoutFragment";

    private Toolbar mToolbar;
    private TextView mTitle;

    private final FeedFragment mFeedFragment = new FeedFragment();
    private final WorkoutFragment mWorkoutFragment = new WorkoutFragment();
    private final MoreFragment mMoreFragment = new MoreFragment();
    private final FragmentManager mFragmentManager = getSupportFragmentManager();
    private Fragment mActive = mWorkoutFragment;

    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_feed:
                    mFragmentManager.beginTransaction().hide(mActive).show(mFeedFragment).commit();
                    mActive = mFeedFragment;
                    mTitle.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    mToolbar.setTitle(R.string.feed_toolbar_title);
                    mToolbar.setNavigationIcon(null);
                    mToolbar.setTitleTextColor(Color.WHITE);
                    invalidateOptionsMenu();
                    return true;

                case R.id.navigation_workout:
                    mFragmentManager.beginTransaction().hide(mActive).show(mWorkoutFragment).commit();
                    mActive = mWorkoutFragment;
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText(getString(R.string.mainacitivity_title));
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    invalidateOptionsMenu();
                    mToolbar.setNavigationIcon(null);
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.cardviewColor));
                    return true;

                case R.id.navigation_more:
                    mFragmentManager.beginTransaction().hide(mActive).show(mMoreFragment).commit();
                    mActive = mMoreFragment;
                    mToolbar.setNavigationIcon(R.drawable.ic_profile_white_2);
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, ProfileDetailsActivity.class);
                            startActivity(intent);
                        }
                    });
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText(getString(R.string.more_toolbar_title));
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        WeatherLog.setDebuggable(true);
        MainActivityLog.setDebuggable(true);
        WorkoutLog.setDebuggable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitle = mToolbar.findViewById(R.id.toolbar_title);

        mFragmentManager.beginTransaction().add(R.id.main_container, mMoreFragment, MORE_FRAGMENT).hide(mMoreFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.main_container, mFeedFragment, FEED_FRAGMENT).hide(mFeedFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.main_container, mWorkoutFragment, WORKOUT_FRAGMENT).commit();

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_workout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_feed
                || mBottomNavigationView.getSelectedItemId() == R.id.navigation_workout) {
            MenuItem item = menu.findItem(R.id.settings_menu_item);
            item.setVisible(false);
        }
        return true;
    }
}
