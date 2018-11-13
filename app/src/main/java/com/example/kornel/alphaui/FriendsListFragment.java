package com.example.kornel.alphaui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.example.kornel.alphaui.FriendsActivity.OnDeleteFriendDialog;
import com.example.kornel.alphaui.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FriendsListFragment extends Fragment
        implements ListItemClickListener, OnDeleteFriendDialog {
    private static final String TAG = "FriendsListFragment";

    private TextView mNoDataInfoTextView;
    private FriendsListAdapter mFriendsListAdapter;
    private RecyclerView mRecyclerView;

    private List<User> mFriendsProfileList;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUsersRef;

    public FriendsListFragment() {
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
        if (mFriendsProfileList.size() == 0 ) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText(getString(R.string.no_friends));
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);
        checkIfListIsEmpty();

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mFriendsListAdapter = new FriendsListAdapter(this, mFriendsProfileList);
        mRecyclerView.setAdapter(mFriendsListAdapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = firebaseDatabase.getReference(Database.USERS);

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");

        String friendUid = mFriendsProfileList.get(clickedItemIndex).getUserUid();

        Dialog removeFriendDialog = createDialog(
                "Usuąć znajmowgo?",
                "Usuń",
                "Anuluj",
                friendUid,
                this);

        removeFriendDialog.show();
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
