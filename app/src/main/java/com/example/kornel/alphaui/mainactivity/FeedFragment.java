package com.example.kornel.alphaui.mainactivity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kornel.alphaui.R;

public class FeedFragment extends Fragment {
    private FeedPagerAdapter mFeedPagerAdapter;
    private ViewPager mViewPager;

    private FeedFriendsFragment mFeedFriendsFragment;
    private FeedYouFragment mFeedYouFragment;

    public FeedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mFeedPagerAdapter = new FeedPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager = rootView.findViewById(R.id.feedContainer);
        mViewPager.setAdapter(mFeedPagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.feed_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return rootView;
    }

    public class FeedPagerAdapter extends FragmentPagerAdapter {

        public FeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mFeedFriendsFragment = new FeedFriendsFragment();
                return mFeedFriendsFragment;
            } else {
                mFeedYouFragment = new FeedYouFragment();
                return mFeedYouFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}