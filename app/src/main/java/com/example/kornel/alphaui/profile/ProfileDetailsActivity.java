package com.example.kornel.alphaui.profile;

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

import com.example.kornel.alphaui.R;
import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.User;
import com.example.kornel.alphaui.utils.Utils;
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
    private TextView mFriendsCountTextView;


    private Button mEditProfileButton;
    private Button mAddFriendButton;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUserRef;
    private DatabaseReference mFriendReqRef;

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
        mTotalTimeTextView = findViewById(R.id.totalTimeTextView2);
        mWorkoutsCountTextView = findViewById(R.id.workoutsCountTextView);
        mHmm1TextView = findViewById(R.id.hmm1TextView);
        mHmm2TextView = findViewById(R.id.hmm2TextView);
        mFriendsCountTextView = findViewById(R.id.friendsCountTextView2);

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
        mUser = firebaseAuth.getCurrentUser();
        mUserUid = mUser.getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mFriendReqRef = firebaseDatabase.getReference(Database.FRIENDS_REQUESTS);
        mUserRef = firebaseDatabase.getReference(Database.USERS);

        mUserRef.child(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.ic_person_black_64dp)
                        .error(R.drawable.ic_error_red_64dp)
                        .into(mAvatarImageView);
                mNameTextView.setText(user.getFirstName() + " " + user.getSurname());
                mTotalDistanceTextView.setText(Utils.distanceMetersToKm(user.getTotalDistance()) + "km");
                mTotalTimeTextView.setText(Utils.getDurationHoursString(user.getTotalDuration()) + "h");
                mWorkoutsCountTextView.setText(user.getTotalWorkouts() + "");
                if (user.getFriends() == null) {
                    mFriendsCountTextView.setText(0 + "");
                } else {
                    mFriendsCountTextView.setText(user.getFriends().size() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
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

                mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child(Database.EMAIL).getValue().equals(email)) {
                                mUserUid = mUser.getUid();
                                final String friendUid = userSnapshot.getKey();

                                if (mUserUid.equals(friendUid)) {
                                    infoTextView.setText(getString(R.string.can_not_send_invitation_to_yrself));
                                } else {

                                    mUserRef.child(mUserUid).child(Database.FRIENDS).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                infoTextView.setText(getString(R.string.user_is_already_a_friends));
                                            } else {
                                                mFriendReqRef.child(mUserUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() != null) {
                                                            infoTextView.setText(R.string.friends_dialog_already_sent);
                                                        } else {
                                                            infoTextView.setText("");
                                                            mFriendReqRef.child(mUserUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                                            mFriendReqRef.child(friendUid).child(mUserUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                                            Toast.makeText(ProfileDetailsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                                            dialog.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        throw databaseError.toException();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            throw databaseError.toException();
                                        }
                                    });
                                }
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
                        throw databaseError.toException();
                    }
                });
            }
        });
    }
}
