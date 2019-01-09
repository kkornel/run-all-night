package com.example.kornel.alphaui.friends;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.profile.ViewProfileActivity;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.friends.FriendsActivity.OnDeleteFriendDialog;
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

import static com.example.kornel.alphaui.sharelocation.FindOthersMapFragment.EXTRA_USER;

public class FriendsListFragment extends Fragment implements ListItemClickListener, OnDeleteFriendDialog {
    private static final String TAG = "FriendsListFragment";

    private TextView mNoDataInfoTextView;
    private FriendsListAdapter mFriendsListAdapter;
    private RecyclerView mRecyclerView;

    private List<User> mFriendsProfileList;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUsersRef;
    private DatabaseReference mUserFriendsRef;

    private ValueEventListener mFriendsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            final HashMap<String, Boolean> friends = (HashMap) dataSnapshot.getValue();

            if (friends == null) {
                loadNewData(new ArrayList<User>());
            } else {
                final List<User> friendsList = new ArrayList<>();
                for (final String key : friends.keySet()) {
                    mUsersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.setUserUid(key);
                            friendsList.add(user);

                            if (friendsList.size() == friends.size()) {
                                loadNewData(friendsList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            throw databaseError.toException();
        }
    };


    public FriendsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFriendsListAdapter = new FriendsListAdapter(this, mFriendsProfileList);
        mRecyclerView.setAdapter(mFriendsListAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();
        mUsersRef = rootRef.child(Database.USERS);
        mUserFriendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);

        readFriendsList();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUserFriendsRef.removeEventListener(mFriendsListener);
    }

    @Override
    public void onListItemClick(final int clickedItemIndex) {
        final String friendUid = mFriendsProfileList.get(clickedItemIndex).getUserUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Choose")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog removeFriendDialog = createDialog(
                                getString(R.string.remove_friend),
                                getString(R.string.remove),
                                getString(R.string.cancel),
                                friendUid,
                                FriendsListFragment.this);

                        removeFriendDialog.show();
                    }
                })
                .setNegativeButton("Profile", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getContext(), ViewProfileActivity.class);
                        i.putExtra(EXTRA_USER, mFriendsProfileList.get(clickedItemIndex));

                        startActivity(i);
                    }
                });

        Dialog friendDialog = builder.create();
        friendDialog.show();

        Dialog removeFriendDialog = createDialog(
                getString(R.string.remove_friend),
                getString(R.string.remove),
                getString(R.string.cancel),
                friendUid,
                this);

        // removeFriendDialog.show();
    }

    public void setFriendsList(List<User> friendsProfileList) {
        mFriendsProfileList = friendsProfileList;
    }

    public void loadNewData(List<User> friendsProfileList) {
        setFriendsList(friendsProfileList);
        checkIfListIsEmpty();
        mFriendsListAdapter.loadNewData(mFriendsProfileList);
    }

    private void checkIfListIsEmpty() {
        if (mFriendsProfileList.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_friends));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void readFriendsList() {
        mUserFriendsRef.addValueEventListener(mFriendsListener);
    }

    @Override
    public void deleteFriend(String friendUid) {
        mUsersRef.child(mUserUid).child(Database.FRIENDS).child(friendUid).removeValue();
        mUsersRef.child(friendUid).child(Database.FRIENDS).child(mUserUid).removeValue();
    }

    private Dialog createDialog(String title, String posBtn, String negBtn, final String friendUid, final OnDeleteFriendDialog callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title)
                .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.deleteFriend(friendUid);
                    }
                })
                .setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
