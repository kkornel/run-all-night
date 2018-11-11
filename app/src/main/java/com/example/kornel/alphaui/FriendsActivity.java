package com.example.kornel.alphaui;

import android.content.DialogInterface;
import android.provider.ContactsContract;
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
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kornel.alphaui.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private static final String TAG = "FriendsActivity";

    private List<String> mFriendsList;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

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

        mFriendsList = Arrays.asList("asd", "asdasd", "21321", "45df", "23", "333@@#$");
    }


    public void showAddFriendDialog() {
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

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = firebaseDatabase.getReference(Database.USERS);
                final DatabaseReference friendReqRef = firebaseDatabase.getReference(Database.FRIENDS_REQUESTS);

                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child(Database.EMAIL).getValue().equals(email)) {
                                final String userUid = user.getUid();
                                final String friendUid = userSnapshot.getKey();

                                friendReqRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot request : dataSnapshot.getChildren()) {
                                            if (request.getKey().equals(friendUid)) {
                                                infoTextView.setText(R.string.friends_dialog_already_sent);
                                            } else {
                                                infoTextView.setText("");
                                                friendReqRef.child(userUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                                friendReqRef.child(friendUid).child(userUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                                Toast.makeText(FriendsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                                dialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                infoTextView.setText(R.string.friends_dialog_wrong_email);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new FriendsListFragment();
                    ((FriendsListFragment) fragment).setFriendsList(mFriendsList);
                    break;

                case 1:
                    fragment = new FriendsRequestFragment();
                    ((FriendsRequestFragment) fragment).setFriendsList(mFriendsList);
                    break;
            }

            return fragment;
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
