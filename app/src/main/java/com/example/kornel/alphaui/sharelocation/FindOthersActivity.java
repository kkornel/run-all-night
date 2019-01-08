package com.example.kornel.alphaui;

import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.List;

public class FindOthersActivity extends AppCompatActivity implements FindOthersMapFragment.OnFindOthersCallback {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private FindOthersListFragment mFindOthersListFragment;
    private FindOthersMapFragment mFindOthersMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_others);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Znajdź innych");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onFindOthersSuccess() {
        mFindOthersListFragment.loadNewData();
    }

    @Override
    public void onMapUpdate(SharedLocationInfo sharedLoc) {
        mFindOthersMapFragment.onMapUpdate(sharedLoc);
    }

    @Override
    public void onNewRequest(Location location) {
        mFindOthersMapFragment.onNewRequest(location);
    }

    @Override
    public void onGotAllSharedLocations(List<SharedLocationInfo> sharedLocationInfoList) {
        mFindOthersMapFragment.onGotSharedLocationInfoList(sharedLocationInfoList);
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mFindOthersListFragment = new FindOthersListFragment();
                mFindOthersListFragment.setCallback(FindOthersActivity.this);
                return mFindOthersListFragment;
            } else {
                mFindOthersMapFragment = new FindOthersMapFragment();
                mFindOthersMapFragment.setCallback(FindOthersActivity.this);
                return mFindOthersMapFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
