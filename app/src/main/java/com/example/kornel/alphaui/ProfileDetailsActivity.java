package com.example.kornel.alphaui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileDetailsActivity extends AppCompatActivity {
    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private TextView mTotalDistanceTextView;
    private TextView mTotalTimeTextView;
    private TextView mWorkoutsCountTextView;
    private TextView mHmm1TextView;
    private TextView mHmm2TextView;
    private TextView mFirendsCountTextView;


    private Button mEditProfileButton;
    private Button mAddFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        getSupportActionBar().setTitle(R.string.profile_details_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAvatarImageView = findViewById(R.id.avatarImageView);
        mNameTextView = findViewById(R.id.nameTextView);
        mTotalDistanceTextView = findViewById(R.id.totalDistanceTextView);
        mTotalTimeTextView = findViewById(R.id.totalTimeTextView);
        mWorkoutsCountTextView = findViewById(R.id.workoutsCountTextView);
        mHmm1TextView = findViewById(R.id.hmm1TextView);
        mHmm2TextView = findViewById(R.id.hmm2TextView);
        mFirendsCountTextView = findViewById(R.id.friendsCountTextView);

        mEditProfileButton = findViewById(R.id.editProfileButton);
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditProfileClicked();
            }
        });

        mAddFriendButton = findViewById(R.id.addFriendButton);
        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddFriendClicked();
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userUid = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference(Database.USERS);

        userRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getAvatarUrl()).into(mAvatarImageView);
                mNameTextView.setText(user.getFirstName() + " " + user.getSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void onEditProfileClicked() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void onAddFriendClicked() {
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

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child(Database.EMAIL).getValue().equals(email)) {
                                final String userUid = user.getUid();
                                final String friendUid = userSnapshot.getKey();

                                friendReqRef.child(userUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            infoTextView.setText(R.string.friends_dialog_already_sent);
                                        } else {
                                            infoTextView.setText("");
                                            friendReqRef.child(userUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                            friendReqRef.child(friendUid).child(userUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                            Toast.makeText(ProfileDetailsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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

                    }
                });
            }
        });
    }
}
