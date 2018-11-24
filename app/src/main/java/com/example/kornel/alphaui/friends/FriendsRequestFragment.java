package com.example.kornel.alphaui.friends;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.friends.FriendsActivity.OnInviteDialogShow;
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

public class FriendsRequestFragment extends Fragment
        implements ListItemClickListener, OnInviteDialogShow {
    private static final String TAG = "FriendsRequestFragment";

    private TextView mNoDataInfoTextView;
    private FriendsRequestAdapter mFriendsRequestAdapter;
    private RecyclerView mRecyclerView;

    private List<FriendRequest> mFriendsRequestList;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRequestsRef;
    private DatabaseReference mUserRequestsRef;

    private ValueEventListener mRequestListener = new ValueEventListener() {
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
                loadNewData(new ArrayList<FriendRequest>());
            }

            final List<FriendRequest> updatedFriendsRequestList = new ArrayList<>();
            for (final FriendRequest friendRequest : friendsRequestList) {
                String friendUid = friendRequest.getFriendUid();

                DatabaseReference friendProfileRef = mUsersRef.child(friendUid);
                ValueEventListener friendsProfilesListener = new ValueEventListener() {
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
                            loadNewData(updatedFriendsRequestList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                        throw databaseError.toException();
                    }
                };
                friendProfileRef.addListenerForSingleValueEvent(friendsProfilesListener);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            throw databaseError.toException();
        }
    };

    public FriendsRequestFragment() {

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

        mFriendsRequestAdapter = new FriendsRequestAdapter(this, mFriendsRequestList);
        mRecyclerView.setAdapter(mFriendsRequestAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: ");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        mUsersRef = rootRef.child(Database.USERS);
        mRequestsRef = rootRef.child(Database.FRIENDS_REQUESTS);
        mUserRequestsRef = rootRef.child(Database.FRIENDS_REQUESTS).child(mUserUid);

        readFriendsRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUserRequestsRef.removeEventListener(mRequestListener);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        String requestType = mFriendsRequestList.get(clickedItemIndex).getRequestType();
        String friendUid = mFriendsRequestList.get(clickedItemIndex).getFriendUid();

        Dialog dialog = null;

        if (requestType.equals(Database.FRIENDS_REQUESTS_SENT)) {
            dialog = createDialog(
                    Database.FRIENDS_REQUESTS_SENT,
                    getString(R.string.remove_friend_confirmation),
                    getString(R.string.cancel),
                    getString(R.string.close),
                    friendUid,
                    this);
        } else {
            dialog = createDialog(
                    Database.FRIENDS_REQUESTS_RECEIVED,
                    getString(R.string.new_invitation),
                    getString(R.string.accept),
                    getString(R.string.discard),
                    friendUid,
                    this);
        }
        dialog.show();
    }

    public void setFriendsList(List<FriendRequest> friendsRequestList) {
        mFriendsRequestList = friendsRequestList;
    }

    public void loadNewData(List<FriendRequest> friendsRequestList) {
        setFriendsList(friendsRequestList);
        checkIfListIsEmpty();
        mFriendsRequestAdapter.loadNewData(mFriendsRequestList);
    }

    private void checkIfListIsEmpty() {
        if (mFriendsRequestList.size() == 0 ) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_new_invitations));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void readFriendsRequests() {
        mUserRequestsRef.addValueEventListener(mRequestListener);
    }

    @Override
    public void acceptInvite(final String friendUid) {
        DatabaseReference userFriendsRef = mUsersRef.child(mUserUid).child(Database.FRIENDS);
        ValueEventListener userFriendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> friends = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (friends == null) {
                    friends = new HashMap<>();
                }
                friends.put(friendUid, true);
                mUsersRef.child(mUserUid).child(Database.FRIENDS).setValue(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userFriendsRef.addListenerForSingleValueEvent(userFriendsListener);

        DatabaseReference friendUserRef = mUsersRef.child(friendUid).child(Database.FRIENDS);
        ValueEventListener friendUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> friends = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (friends == null) {
                    friends = new HashMap<>();
                }
                friends.put(mUserUid, true);
                mUsersRef.child(friendUid).child(Database.FRIENDS).setValue(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        friendUserRef.addListenerForSingleValueEvent(friendUserListener);

        cancelInvite(friendUid);
    }

    @Override
    public void cancelInvite(String friendUid) {
        mRequestsRef.child(mUserUid).child(friendUid).removeValue();
        mRequestsRef.child(friendUid).child(mUserUid).removeValue();
    }

    private Dialog createDialog(final String type, String title, String posBtn, String negBtn, final String friendUid, final OnInviteDialogShow callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title)
                .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (type.equals(Database.FRIENDS_REQUESTS_SENT)) {
                            callback.cancelInvite(friendUid);
                        } else {
                            callback.acceptInvite(friendUid);
                        }
                    }
                })
                .setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (type.equals(Database.FRIENDS_REQUESTS_RECEIVED)) {
                            callback.cancelInvite(friendUid);
                        }
                    }
                });
        return builder.create();
    }
}