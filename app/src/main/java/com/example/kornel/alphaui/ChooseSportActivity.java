package com.example.kornel.alphaui;

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

import com.example.kornel.alphaui.utils.GpsBasedActivity;
import com.example.kornel.alphaui.utils.NonGpsBasedActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseSportActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private List<String> mGpsActivities;
    private List<String> mNonGpsActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_workout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mGpsActivities = new ArrayList<>();
        mNonGpsActivities = new ArrayList<>();

        for (GpsBasedActivity activity : GpsBasedActivity.values()) {
            mGpsActivities.add(activity.toString());
        }
        for (NonGpsBasedActivity activity : NonGpsBasedActivity.values()) {
            mNonGpsActivities.add(activity.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to
    // one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GpsBasedFragment gpsBasedFragment = new GpsBasedFragment();

            if (position == 0) {
                gpsBasedFragment.setActivitiesList(mGpsActivities);
            } else {
                gpsBasedFragment.setActivitiesList(mNonGpsActivities);
            }

            // return PlaceholderFragment.newInstance(position + 1);
            return gpsBasedFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
