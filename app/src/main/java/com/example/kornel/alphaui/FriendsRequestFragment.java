package com.example.kornel.alphaui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.utils.OnDialogShow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsRequestFragment extends Fragment
        implements ListItemClickListener, OnDialogShow {
    private static final String TAG = "FriendsRequestFragment";

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRequestsRef;

    private FriendsRequestAdapter mFriendsRequestAdapter;
    private RecyclerView mRecyclerView;

    private List<FriendRequest> mFriendsRequestList;

    public FriendsRequestFragment() {
    }

    public void setFriendsList(List<FriendRequest> friendsRequestList) {
        mFriendsRequestList = friendsRequestList;
    }

    public void loadNewData(List<FriendRequest> friendsRequestList) {
        mFriendsRequestList = friendsRequestList;
        mFriendsRequestAdapter.loadNewData(mFriendsRequestList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFriendsRequestAdapter = new FriendsRequestAdapter(this, mFriendsRequestList);
        mRecyclerView.setAdapter(mFriendsRequestAdapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = firebaseDatabase.getReference(Database.USERS);
        mRequestsRef = firebaseDatabase.getReference(Database.FRIENDS_REQUESTS);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: " + mFriendsRequestList.get(clickedItemIndex));

        String requestType = mFriendsRequestList.get(clickedItemIndex).getRequestType();
        String friendUid = mFriendsRequestList.get(clickedItemIndex).getFriendUid();

        Dialog dialog = null;

        if (requestType.equals(Database.FRIENDS_REQUESTS_SENT)) {
            dialog = createRequestDialog(
                    Database.FRIENDS_REQUESTS_SENT,
                    "Czy na pewno chcesz anulować zaproszenie?",
                    "Anuluj",
                    "Zamknij",
                    friendUid,
                    this);
        } else {
            dialog = createRequestDialog(
                    Database.FRIENDS_REQUESTS_RECEIVED,
                    "Nowe zaproszenie",
                    "Akceptuj",
                    "Odrzucić",
                    friendUid,
                    this);
        }
        dialog.show();
    }

    @Override
    public void acceptInvite(final String friendUid) {
        mUsersRef.child(mUserUid).child(Database.FRIENDS).addListenerForSingleValueEvent(new ValueEventListener() {
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

            }
        });

        mUsersRef.child(friendUid).child(Database.FRIENDS).addListenerForSingleValueEvent(new ValueEventListener() {
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

            }
        });

        cancelInvite(friendUid);
    }

    @Override
    public void cancelInvite(String friendUid) {
        mRequestsRef.child(mUserUid).child(friendUid).removeValue();
        mRequestsRef.child(friendUid).child(mUserUid).removeValue();
    }

    public Dialog createRequestDialog(final String type, String title, String posBtn, String negBtn, final String friendUid, final OnDialogShow callback) {
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