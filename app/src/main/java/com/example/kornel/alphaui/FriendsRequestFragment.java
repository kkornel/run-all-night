package com.example.kornel.alphaui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.FriendRequest;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FriendsRequestFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "FeedFriendsFragment";

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

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: " + mFriendsRequestList.get(clickedItemIndex));

        String requestType = mFriendsRequestList.get(clickedItemIndex).getRequestType();
        String friendUid = mFriendsRequestList.get(clickedItemIndex).getFriendUid();

        Dialog dialog = null;

        if (requestType.equals(Database.FRIENDS_REQUESTS_SENT)) {
            dialog = createCancelRequestDialog(
                    "Czy na pewno chcesz anulować zaproszenie?",
                    "Anuluj",
                    "Zamknij",
                    friendUid);
        } else {
            dialog = createCancelRequestDialog(
                    "Nowe zaproszenie",
                    "Akceptuj",
                    "Odrzucić",
                    friendUid);
        }
        dialog.show();
    }

    public Dialog createCancelRequestDialog(String title, String posBtn, String negBtn, final String friendUid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title)
                .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String userUid = user.getUid();
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference requestsRef = firebaseDatabase.getReference(Database.FRIENDS_REQUESTS);
                        requestsRef.child(userUid).child(friendUid).removeValue();
                        requestsRef.child(friendUid).child(userUid).removeValue();
                    }
                })
                .setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }

}