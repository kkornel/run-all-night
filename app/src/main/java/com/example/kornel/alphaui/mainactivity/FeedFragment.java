package com.example.kornel.alphaui.mainactivity;


import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.example.kornel.alphaui.FriendWorkout;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.gpsworkout.WorkoutGpsSummary;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.Friend;
import com.example.kornel.alphaui.utils.GpsBasedWorkout;
import com.example.kornel.alphaui.utils.NonGpsBasedWorkout;
import com.example.kornel.alphaui.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mWorkoutsRef;
    private DatabaseReference mUsersRef;

    private FeedPagerAdapter mFeedPagerAdapter;

    private ViewPager mViewPager;

    private HashMap<String, Boolean> mFriendsIds;
    private List<WorkoutGpsSummary> mMyWorkouts;
    private List<WorkoutGpsSummary> mFriendsWorkouts;
    private FeedYouFragment mFeedYouFragment;
    private FeedFriendsFragment mFeedFriendsFragment;

    private List<String> mGpsWorkouts;
    private List<String> mNonGpsWorkouts;

    private ValueEventListener mFeedYouListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mMyWorkouts = new ArrayList<>();
            if (dataSnapshot.getValue() == null && mFeedYouFragment != null) {
                mFeedYouFragment.loadNewData(mMyWorkouts);
            } else {
                for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                    mMyWorkouts.add(workoutSnapshot.getValue(WorkoutGpsSummary.class));
                    if (mFeedYouFragment != null
                            && dataSnapshot.getChildrenCount() == mMyWorkouts.size()) {
                        mFeedYouFragment.loadNewData(mMyWorkouts);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    private ValueEventListener mFeedFriendsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                mMyWorkouts.add(workoutSnapshot.getValue(WorkoutGpsSummary.class));
                if (mFeedYouFragment != null) {
                    mFeedYouFragment.setFeedYouList(mMyWorkouts);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

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

        mFriendsIds = new HashMap<>();
        mMyWorkouts = new ArrayList<>();
        mFriendsWorkouts = new ArrayList<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mUsersRef = database.getReference(Database.USERS);
        mWorkoutsRef = database.getReference("workouts");

        mWorkoutsRef.child(mUserUid).addValueEventListener(mFeedYouListener);

        final List<FriendWorkout> friendWorkouts = new ArrayList<>();

        // Get all friends
        mUsersRef.child(mUserUid).child(Database.FRIENDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriendsIds = (HashMap) dataSnapshot.getValue();
                for (final String friendId : mFriendsIds.keySet()) {
                    mWorkoutsRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                final HashMap<String, GpsBasedWorkout> hashMap = (HashMap) dataSnapshot.getValue();
                                Log.d(TAG, "onDataChange: " + hashMap.toString());
                                mUsersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        for (Object value : hashMap.values()) {
                                            FriendWorkout friendWorkout = new FriendWorkout(
                                                    user.getFullName(),
                                                    user.getAvatarUrl(),
                                                    (GpsBasedWorkout) value);
                                            friendWorkouts.add(friendWorkout);
                                        }
                                        Log.d(TAG, "onDataChange2: " + friendWorkouts.toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        for (GpsBasedWorkout activity : GpsBasedWorkout.values()) {
            mGpsWorkouts.add(activity.toString());
        }
        for (NonGpsBasedWorkout activity : NonGpsBasedWorkout.values()) {
            mNonGpsWorkouts.add(activity.toString());
        }

        List<FriendWorkout> friendsWorkoutsList = new ArrayList<>();
        final HashMap<String, GpsBasedWorkout> hashMap;
        Log.d(TAG, "onCreateView: " + mFriendsIds.size());


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mWorkoutsRef.child(mUserUid).removeEventListener(mFeedYouListener);
        // mWorkoutsRef.child(mUserUid).removeEventListener(mFeedYouListener);
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
                mFeedFriendsFragment = new FeedFriendsFragment();
                mFeedFriendsFragment.setFeedFriendsList(mNonGpsWorkouts);
                return mFeedFriendsFragment;
            } else {
                mFeedYouFragment = new FeedYouFragment();
                mFeedYouFragment.setFeedYouList(mMyWorkouts);
                return mFeedYouFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
