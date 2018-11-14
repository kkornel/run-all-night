package com.example.kornel.alphaui.mainactivity;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.kornel.alphaui.profile.ProfileDetailsActivity;
import com.example.kornel.alphaui.R;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser mUser;

    private Toolbar mToolbar;
    private TextView mTitle;

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
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, ProfileDetailsActivity.class);
                            startActivity(intent);
                        }
                    });
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText("More");
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(this, "sadd", Toast.LENGTH_SHORT).show();
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

        fragmentManager.beginTransaction().add(R.id.main_container, moreFragment, "MoreFragment").hide(moreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, feedFragment, "FeedFragment").hide(feedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, workoutFragment, "WorkoutFragment").commit();

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_workout);

        // if (savedInstanceState != null) {
            // mUser = (FirebaseUser) getIntent().getExtras().get(INTENT_EXTRA_FIREBASE_USER);
        // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_feed
                || mBottomNavigationView.getSelectedItemId() == R.id.navigation_workout) {
            MenuItem item = menu.findItem(R.id.settings_menu_item);
            item.setVisible(false);
        }
        return true;
    }
}
