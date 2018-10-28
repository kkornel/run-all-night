package com.example.kornel.alphaui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final Fragment feedFragment = new FeedFragment();
    private final Fragment workoutFragment = new WorkoutFragment();
    private final Fragment moreFragment = new MoreFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = workoutFragment;

    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    fragmentManager.beginTransaction().hide(active).show(feedFragment).commit();
                    active = feedFragment;
                    return true;
                case R.id.navigation_workout:
                    fragmentManager.beginTransaction().hide(active).show(workoutFragment).commit();
                    active = workoutFragment;
                    return true;
                case R.id.navigation_more:
                    fragmentManager.beginTransaction().hide(active).show(moreFragment).commit();
                    active = moreFragment;
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

        fragmentManager.beginTransaction().add(R.id.main_container, moreFragment, "MoreFragment").hide(moreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, feedFragment, "FeedFragment").hide(feedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, workoutFragment, "WorkoutFragment").commit();

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_workout);
    }
}
