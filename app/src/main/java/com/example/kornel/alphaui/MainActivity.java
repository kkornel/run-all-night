package com.example.kornel.alphaui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Toolbar mToolbar;
    TextView mTitle;

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
                    mTitle.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    mToolbar.setTitle("Feed");
                    mToolbar.setNavigationIcon(null);
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_workout:
                    fragmentManager.beginTransaction().hide(active).show(workoutFragment).commit();
                    active = workoutFragment;
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText("MoonRunner");
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    invalidateOptionsMenu();
                    mToolbar.setNavigationIcon(null);
                    return true;
                case R.id.navigation_more:
                    fragmentManager.beginTransaction().hide(active).show(moreFragment).commit();
                    active = moreFragment;
                    mToolbar.setNavigationIcon(R.drawable.ic_directions_bike_black_24dp);
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText("More");
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitle =  (TextView) mToolbar.findViewById(R.id.toolbar_title);

        fragmentManager.beginTransaction().add(R.id.main_container, moreFragment, "MoreFragment").hide(moreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, feedFragment, "FeedFragment").hide(feedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, workoutFragment, "WorkoutFragment").commit();

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_workout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.settings_menu, menu);

        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_feed
                || mBottomNavigationView.getSelectedItemId() == R.id.navigation_workout) {
            MenuItem item = menu.findItem(R.id.settings_menu_item);
            item.setVisible(false);
            return true;
        }
        return true;
    }


}
