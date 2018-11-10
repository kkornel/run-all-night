package com.example.kornel.alphaui.mainactivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.FriendsActivity;
import com.example.kornel.alphaui.LoginActivity;
import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {

    private static final String TAG = "MoreFragment";

    Button mFreeButton;
    Button mFriendsButton;
    Button mLogoutButton;


    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =inflater.inflate(R.layout.fragment_more, container, false);


        mFreeButton = rootView.findViewById(R.id.freeButton);
        mFriendsButton = rootView.findViewById(R.id.friendsButton);
        mLogoutButton = rootView.findViewById(R.id.logoutButton);

        mFreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
        
        return rootView;
    }

    // private void addFriend() {
    //     final String email = mEmail.getText().toString();
    //
    //     FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //     final DatabaseReference userRef = firebaseDatabase.getReference(Database.USERS);
    //     final DatabaseReference firendReqRef = firebaseDatabase.getReference("friendsRequests");
    //
    //     FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //     final FirebaseUser user = firebaseAuth.getCurrentUser();
    //
    //     userRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //             for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
    //                 Log.d(TAG, "onDataChange: " + userSnapshot.child("email").toString());
    //                 if (userSnapshot.child("email").getValue().equals(email)) {
    //                     String friendUid = userSnapshot.getKey();
    //                     Log.d(TAG, "onDataChange: " + friendUid);
    //
    //                     firendReqRef.child(user.getUid()).child(friendUid).setValue("sent");
    //                     firendReqRef.child(friendUid).child(user.getUid()).setValue("received");
    //
    //                     Toast.makeText(getContext(), "Wysłano zaproszenie do grona znajomych", Toast.LENGTH_LONG).show();
    //                     mInfoTextView.setText("");
    //                 } else {
    //                     mInfoTextView.setText("Nie ma użytkownika o takim adresie email");
    //                 }
    //             }
    //         }
    //
    //         @Override
    //         public void onCancelled(@NonNull DatabaseError databaseError) {
    //
    //         }
    //     });
    // }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }
}
