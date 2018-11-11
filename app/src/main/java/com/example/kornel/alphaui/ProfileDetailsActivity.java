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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kornel.alphaui.utils.Database;
import com.example.kornel.alphaui.utils.ProfileInfoValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileDetailsActivity extends AppCompatActivity {
    private Button mEditProfileButton;
    private Button mAddFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        getSupportActionBar().setTitle("Edytuj");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if (userSnapshot.child(Database.EMAIL).getValue().equals(email)) {
                                final String userUid = user.getUid();
                                final String friendUid = userSnapshot.getKey();

                                friendReqRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot request : dataSnapshot.getChildren()) {
                                            if (request.getKey().equals(friendUid)) {
                                                infoTextView.setText(R.string.friends_dialog_already_sent);
                                            } else {
                                                infoTextView.setText("");
                                                friendReqRef.child(userUid).child(friendUid).setValue(Database.FRIENDS_REQUESTS_SENT);
                                                friendReqRef.child(friendUid).child(userUid).setValue(Database.FRIENDS_REQUESTS_RECEIVED);
                                                Toast.makeText(ProfileDetailsActivity.this, R.string.friends_dialog_invitation_sent, Toast.LENGTH_SHORT).show();

                                                dialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                infoTextView.setText(R.string.friends_dialog_wrong_email);
                            }
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
