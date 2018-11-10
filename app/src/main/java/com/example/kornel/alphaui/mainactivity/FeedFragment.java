package com.example.kornel.alphaui.mainactivity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;
import com.example.kornel.alphaui.utils.NonGpsBasedWorkout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";

    private FeedPagerAdapter mFeedPagerAdapter;

    private ViewPager mViewPager;

    private List<String> mGpsWorkouts;
    private List<String> mNonGpsWorkouts;

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

        mGpsWorkouts = new ArrayList<>();
        mNonGpsWorkouts = new ArrayList<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference workoutRef = database.getReference("workouts");
        DatabaseReference userRef = database.getReference(Database.USERS);



        workoutRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                    myWorkouts.add(workoutSnapshot.getValue(WorkoutGpsSummary.class));
                    if (mFeedYouFragment!= null) {
                        mFeedYouFragment.setFeedYouList(myWorkouts);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (GpsBasedWorkout activity : GpsBasedWorkout.values()) {
            mGpsWorkouts.add(activity.toString());
        }
        for (NonGpsBasedWorkout activity : NonGpsBasedWorkout.values()) {
            mNonGpsWorkouts.add(activity.toString());
        }

        return rootView;
    }

    private List<WorkoutGpsSummary> myWorkouts = new ArrayList<>();
    private FeedYouFragment mFeedYouFragment;

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
                feedFriendsFragment.setFeedFriendsList(mNonGpsWorkouts);
                return feedFriendsFragment;
            } else {
                mFeedYouFragment = new FeedYouFragment();
                mFeedYouFragment.setFeedYouList(myWorkouts);
                return mFeedYouFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
