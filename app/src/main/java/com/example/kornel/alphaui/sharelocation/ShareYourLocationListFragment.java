package com.example.kornel.alphaui.sharelocation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.CurrentUserProfile;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ListItemClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Map;
import java.util.Set;

public class ShareYourLocationListFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "ShareYourLocationListFr";

    private TextView mNoDataInfoTextView;
    private ShareYourLocationListAdapter mShareYourLocationListAdapter;
    private RecyclerView mRecyclerView;

    private List<SharedLocationInfo> mYourSharedLocations;

    public ShareYourLocationListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_your_location_list, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mShareYourLocationListAdapter = new ShareYourLocationListAdapter(getContext(), this, mYourSharedLocations);
        mRecyclerView.setAdapter(mShareYourLocationListAdapter);

        queryForYourShares();

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        createDeleteDialog(clickedItemIndex).show();
    }

    private void loadNewData() {
        checkIfEmpty();
        mShareYourLocationListAdapter.loadNewData(mYourSharedLocations);
    }

    public void queryForYourShares() {
        mYourSharedLocations = new ArrayList<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        final DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        DatabaseReference userRef = rootRef.child(Database.USERS);
        final String userUid = user.getUid();

        ValueEventListener sharedLocsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final HashMap<String, Boolean> sharedLocsMap = (HashMap<String, Boolean>) dataSnapshot.getValue();

                if (sharedLocsMap == null) {
                    loadNewData();
                    return;
                }

                final Set<String> workoutTypes = sharedLocsMap.keySet();

                CurrentUserProfile.setSharedLocations(sharedLocsMap);

                for (final String workoutType : workoutTypes) {

                    ValueEventListener sharedLocationInfoListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SharedLocationInfo sli = dataSnapshot.getValue(SharedLocationInfo.class);
                            sli.setWorkoutType(workoutType);

                            mYourSharedLocations.add(sli);

                            if (mYourSharedLocations.size() == sharedLocsMap.size()) {
                                loadNewData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };

                    sharedLocRef.child(workoutType).child(userUid).addListenerForSingleValueEvent(sharedLocationInfoListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        userRef.child(userUid).child(Database.SHARED_LOCATIONS).addListenerForSingleValueEvent(sharedLocsListener);
    }

    private void delete(int clickedItemIndex) {
        SharedLocationInfo sli = mYourSharedLocations.get(clickedItemIndex);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        final DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        DatabaseReference userRef = rootRef.child(Database.USERS);

        String userUid = user.getUid();

        Map<String, Boolean> sharedLocationsMap = CurrentUserProfile.getSharedLocations();
        sharedLocationsMap.remove(sli.getWorkoutType());

        userRef.child(userUid).child(Database.SHARED_LOCATIONS).setValue(sharedLocationsMap);

        sharedLocRef.child(sli.getWorkoutType()).child(userUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                queryForYourShares();
            }
        });
    }

    private void checkIfEmpty() {
        if (mYourSharedLocations == null || mYourSharedLocations.size() == 0) {
            mNoDataInfoTextView.setText(getString(R.string.no_shares));
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private Dialog createDeleteDialog(final int clickedItemIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete(clickedItemIndex);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
