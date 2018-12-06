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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ShareYourLocationListFragment extends Fragment implements ListItemClickListener {
    private TextView mNoDataInfoTextView;
    private ShareYourLocationListAdapter mShareYourLocationListAdapter;
    private RecyclerView mRecyclerView;

    private List<SharedLocationInfo> mYourSharedLocations;

    public ShareYourLocationListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_your_location_list, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mShareYourLocationListAdapter = new ShareYourLocationListAdapter(getContext(), this, mYourSharedLocations);
        mRecyclerView.setAdapter(mShareYourLocationListAdapter);

        mNoDataInfoTextView.setText("Wyszukaj aby...");
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        queryForYourShares();

        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
        createDeleteDialog(clickedItemIndex).show();

    }

    private Dialog createDeleteDialog(final int clickedItemIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Czy na pewno chcesz usunąć?")
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

    private void delete(int clickedItemIndex) {
        SharedLocationInfo sli = mYourSharedLocations.get(clickedItemIndex);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        final DatabaseReference sharedLocRef = rootRef.child(Database.SHARED_LOCATIONS);
        DatabaseReference userRef = rootRef.child(Database.USERS);

        String userUid = user.getUid();

        sharedLocRef.child(sli.getWorkoutType()).child(userUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                queryForYourShares();
            }
        });
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


        ValueEventListener sharedLocationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot workoutTypeSnapshot : dataSnapshot.getChildren()) {
                    final String workoutType = workoutTypeSnapshot.getKey();

                    ValueEventListener userSharesListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SharedLocationInfo sharedLocationInfo = dataSnapshot.getValue(SharedLocationInfo.class);
                            sharedLocationInfo.setWorkoutType(workoutType);
                            mYourSharedLocations.add(sharedLocationInfo);

                            mShareYourLocationListAdapter.loadNewData(mYourSharedLocations);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                            throw databaseError.toException();
                        }
                    };

                    sharedLocRef.child(workoutType).child(userUid).addListenerForSingleValueEvent(userSharesListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                throw databaseError.toException();
            }
        };
        sharedLocRef.addListenerForSingleValueEvent(sharedLocationsListener);
    }

}
