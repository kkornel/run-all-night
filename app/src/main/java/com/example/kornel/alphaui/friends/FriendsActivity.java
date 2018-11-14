package com.example.kornel.alphaui.friends;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
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

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = "FriendsActivity";

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUserRef;
    private DatabaseReference mFriendReqRef;

    private ValueEventListener mRequestListener;
    private ValueEventListener mFriendsListener;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    FriendsListFragment mFriendsListFragment;
    FriendsRequestFragment mFriendsRequestFragment;

    private ViewPager mViewPager;

    interface OnInviteDialogShow {
        void acceptInvite(String friendUid);

        void cancelInvite(String friendUid);
    }

    interface OnDeleteFriendDialog {
        void deleteFriend(String friendUid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog();
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mUserUid = mUser.getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mFriendReqRef = firebaseDatabase.getReference(Database.FRIENDS_REQUESTS);
        mUserRef = firebaseDatabase.getReference(Database.USERS);

        // TODO: wczytać tylko to co potrzbne czy całość? Żeby potem przenieść np. do nowego okna
        mFriendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Boolean> friends = (HashMap) dataSnapshot.getValue();

                if (friends == null) {
                    mFriendsListFragment.loadNewData(new ArrayList<User>());
                } else {
                    final List<User> friendsList = new ArrayList<>();
                    for (final String key : friends.keySet()) {
                        mUserRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                user.setUserUid(key);
                                friendsList.add(user);

                                if (friendsList.size() == friends.size()) {
                                    mFriendsListFragment.loadNewData(friendsList);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        mRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<FriendRequest> friendsRequestList = new ArrayList<>();
                for (DataSnapshot request : dataSnapshot.getChildren()) {
                    FriendRequest friendRequest = new FriendRequest(
                            request.getKey(),
                            (String) request.getValue());
                    friendsRequestList.add(friendRequest);
                }

                if (friendsRequestList.size() == 0) {
                    mFriendsRequestFragment.loadNewData(new ArrayList<FriendRequest>());
                }

                final List<FriendRequest> updatedFriendsRequestList = new ArrayList<>();
                for (final FriendRequest friendRequest : friendsRequestList) {
                    mUserRef.child(friendRequest.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String friendName = dataSnapshot.child(Database.FIRST_NAME).getValue()
                                    + " " + dataSnapshot.child(Database.SURNAME).getValue();
                            String avatarUrl = dataSnapshot.child(Database.AVATAR_URL).getValue(String.class);
                            FriendRequest updatedFriendRequest = new FriendRequest(
                                    friendRequest.getFriendUid(),
                                    friendRequest.getRequestType(),
                                    friendName,
                                    avatarUrl);
                            updatedFriendsRequestList.add(updatedFriendRequest);

                            if(friendsRequestList.size() == updatedFriendsRequestList.size()) {
                                mFriendsRequestFragment.loadNewData(updatedFriendsRequestList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        mFriendReqRef.child(mUserUid).addValueEventListener(mRequestListener);
        mUserRef.child(mUserUid).child(Database.FRIENDS).addValueEventListener(mFriendsListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFriendReqRef.child(mUserUid).removeEventListener(mRequestListener);
        mUserRef.child(mUserUid).child(Database.FRIENDS).removeEventListener(mFriendsListener);
    }

    private void showAddFriendDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.add_friend_dialog, null);
        final EditText emailEditText = vView.findViewById(R.id.friendsEmailTextView);
        final TextView infoTextView = vView.findViewById(R.id.addFriendResultTextView);

        infoTextView.setText("");

        final AlertDialog dialog;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(vView)
                // Add action buttons
                .setPositiveButton(R.string.friends_dialog_add_button, null)
                .setNegativeButton(R.string.friends_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoTextView.setText("");

                final String email = emailEditText.getText().toString();

                mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child(Database.EMAIL).getValue().equals(email)) {
                                mUserUid = mUser.getUid();
                                final String friendUid = userSnapshot.getKey();

                                if (mUserUid.equals(friendUid)) {
                                    infoTextView.setText(getString(R.string.can_not_send_invitation_to_yrself));
                                } else {

                                    mUserRef.child(mUserUid).child(Database.FRIENDS).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                infoTextView.setText(getString(R.string.user_is_already_a_friends));
                                            } else {
                                                mFriendReqRef.child(mUserUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() != null) {
                                                            infoTextView.setText(R.string.friends_dialog_already_sent);
                                                        } else {
                                                            infoTextView.setText("");
                                                            mFriendReqRef.child(mUserUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                                            mFriendReqRef.child(friendUid).child(mUserUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                                            Toast.makeText(FriendsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        throw databaseError.toException();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            infoTextView.setText(R.string.friends_dialog_wrong_email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });
    }

    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to
    // one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if (position == 0) {
                mFriendsListFragment = new FriendsListFragment();
                mFriendsListFragment.setFriendsList(new ArrayList<User>());
                return mFriendsListFragment;
            } else {
                mFriendsRequestFragment = new FriendsRequestFragment();
                mFriendsRequestFragment.setFriendsList(new ArrayList<FriendRequest>());
                return mFriendsRequestFragment;
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
