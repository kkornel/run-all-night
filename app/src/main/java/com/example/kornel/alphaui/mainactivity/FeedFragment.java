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
    private DatabaseReference mRootRef;
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
        mRootRef = database.getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mWorkoutsRef = mRootRef.child(Database.WORKOUTS);

        mWorkoutsRef.child(mUserUid).addValueEventListener(mFeedYouListener);

        final List<FriendWorkout> friendWorkouts = new ArrayList<>();

        readFriendsWorkouts();

        for (GpsBasedWorkout activity : GpsBasedWorkout.values()) {
            mGpsWorkouts.add(activity.toString());
        }
        for (NonGpsBasedWorkout activity : NonGpsBasedWorkout.values()) {
            mNonGpsWorkouts.add(activity.toString());
        }

        return rootView;
    }

    private void readFriendsWorkouts() {
        final DatabaseReference friendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);
        ValueEventListener friendsIdsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String friendUid = ds.getKey();

                    DatabaseReference friendUidRef = mUsersRef.child(friendUid);
                    ValueEventListener friendsProfilesListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String firstName = ds.child(Database.FIRST_NAME).getValue(String.class);
                                String surname = ds.child(Database.SURNAME).getValue(String.class);
                                final String avatarUrl = ds.child(Database.AVATAR_URL).getValue(String.class);
                                final String friendName = firstName + " " + surname;

                                DatabaseReference friendUidWorkoutsRef = mWorkoutsRef.child(friendUid);
                                ValueEventListener friendsWorkoutsListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        List<FriendWorkout> friendWorkoutsList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            WorkoutGpsSummary workout = ds.getValue(WorkoutGpsSummary.class);

                                            FriendWorkout friendWorkout = new FriendWorkout(friendName, avatarUrl, workout);
                                            friendWorkoutsList.add(friendWorkout);
                                            // Workout workout = ds.getValue(Workout.class);
                                            // Log.d(TAG, workout.getWorkoutName());
                                            //
                                            // Create an object of FriendWorkout as needed
                                            // FriendWorkout FriendWorkout = new FriendWorkout(friendName, workout);
                                        }
                                        mFeedFriendsFragment.setFeedFriendsList(friendWorkoutsList);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, databaseError.getMessage());
                                    }
                                };
                                friendUidWorkoutsRef.addListenerForSingleValueEvent(friendsWorkoutsListener);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    };
                    friendUidRef.addListenerForSingleValueEvent(friendsProfilesListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
        friendsRef.addListenerForSingleValueEvent(friendsIdsListener);
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
                mFeedFriendsFragment.setFeedFriendsList(new ArrayList<FriendWorkout>());
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