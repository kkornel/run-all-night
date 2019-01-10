package com.example.kornel.alphaui.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;
import com.example.kornel.alphaui.utils.NonGpsBasedWorkout;

import java.util.ArrayList;
import java.util.List;

public class ChooseWorkoutActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private List<String> mGpsWorkouts;
    private List<String> mNonGpsWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_workout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.choose_workout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mGpsWorkouts = new ArrayList<>();
        mNonGpsWorkouts = new ArrayList<>();

        for (GpsBasedWorkout workout : GpsBasedWorkout.values()) {
            mGpsWorkouts.add(workout.toString());
        }
        for (NonGpsBasedWorkout workout : NonGpsBasedWorkout.values()) {
            mNonGpsWorkouts.add(workout.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ChooseWorkoutFragment chooseWorkoutFragment = new ChooseWorkoutFragment();

            if (position == 0) {
                chooseWorkoutFragment.setWorkoutsList(mGpsWorkouts);
            } else {
                chooseWorkoutFragment.setWorkoutsList(mNonGpsWorkouts);
            }

            return chooseWorkoutFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
