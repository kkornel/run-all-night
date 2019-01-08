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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = "FriendsActivity";

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUsersRef;
    private DatabaseReference mFriendRequestsRef;

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
        DatabaseReference rootRef = firebaseDatabase.getReference();
        mUsersRef = rootRef.child(Database.USERS);
        mFriendRequestsRef = rootRef.child(Database.FRIENDS_REQUESTS);
    }

    private void showAddFriendDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.add_friend_dialog, null);
        final EditText emailEditText = vView.findViewById(R.id.friendsEmailTextView);
        final TextView infoTextView = vView.findViewById(R.id.addFriendResultTextView);

        infoTextView.setText("");

        final AlertDialog dialog;

        builder.setView(vView)
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

                ValueEventListener usersListener = new ValueEventListener() {
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

                                    DatabaseReference userFriendRef = mUsersRef.child(mUserUid).child(Database.FRIENDS).child(friendUid);
                                    ValueEventListener friendsListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                infoTextView.setText(getString(R.string.user_is_already_a_friends));
                                            } else {

                                                DatabaseReference userFriendRequestsRef = mFriendRequestsRef.child(mUserUid).child(friendUid);
                                                ValueEventListener userFriendRequestsListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() != null) {
                                                            infoTextView.setText(R.string.friends_dialog_already_sent);
                                                        } else {
                                                            infoTextView.setText("");
                                                            mFriendRequestsRef.child(mUserUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                                            mFriendRequestsRef.child(friendUid).child(mUserUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                                            Toast.makeText(FriendsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                                                        throw databaseError.toException();
                                                    }
                                                };
                                                userFriendRequestsRef.addListenerForSingleValueEvent(userFriendRequestsListener);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                                            throw databaseError.toException();
                                        }
                                    };
                                    userFriendRef.addListenerForSingleValueEvent(friendsListener);
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
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                        throw databaseError.toException();
                    }
                };
                mUsersRef.addListenerForSingleValueEvent(usersListener);
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FriendsListFragment friendsListFragment = new FriendsListFragment();
                return friendsListFragment;
            } else {
                FriendsRequestFragment friendsRequestFragment = new FriendsRequestFragment();
                return friendsRequestFragment;
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
