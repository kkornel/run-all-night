package com.example.kornel.alphaui.sharelocation;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.kornel.alphaui.R;

public class ShareYourLocationActivity extends AppCompatActivity implements ShareYourLocationMapFragment.OnDataChanged {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ShareYourLocationMapFragment mShareYourLocationMapFragment;
    private ShareYourLocationListFragment mShareYourLocationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_your_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.share_your_location_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onUploadCompleted() {
        Handler workoutHandler = new Handler();
        Runnable workoutRunnable = new Runnable() {
            @Override
            public void run() {
                mShareYourLocationListFragment.queryForYourShares();
            }
        };

        // I'm doing this after delay, because after deleting last workout from database
        // it was fetching old, not updated data.
        workoutHandler.postDelayed(workoutRunnable, 200);
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mShareYourLocationMapFragment = new ShareYourLocationMapFragment();
                mShareYourLocationMapFragment.setCallBack(ShareYourLocationActivity.this);
                return mShareYourLocationMapFragment;
            } else {
                mShareYourLocationListFragment = new ShareYourLocationListFragment();
                return mShareYourLocationListFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
