package com.example.kornel.alphaui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kornel.alphaui.utils.GpsBasedActivity;
import com.example.kornel.alphaui.utils.NonGpsBasedActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private FeedPagerAdapter mFeedPagerAdapter;

    private ViewPager mViewPager;

    private List<String> mGpsActivities;
    private List<String> mNonGpsActivities;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mFeedPagerAdapter = new FeedPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = rootView.findViewById(R.id.feedContainer);
        mViewPager.setAdapter(mFeedPagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.feed_tabs);

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

        return rootView;
    }

    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to
    // one of the sections/tabs/pages.
    public class FeedPagerAdapter extends FragmentPagerAdapter {

        public FeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FeedFriendsFragment feedFriendsFragment = new FeedFriendsFragment();
                feedFriendsFragment.setFeedFriendsList(mNonGpsActivities);
                return feedFriendsFragment;
            } else {
                FeedYouFragment feedYouFragment = new FeedYouFragment();
                feedYouFragment.setFeedYouList(mGpsActivities);
                return feedYouFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
