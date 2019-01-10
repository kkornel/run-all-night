package com.example.kornel.alphaui.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kornel.alphaui.sharelocation.FindOthersActivity;
import com.example.kornel.alphaui.SettingsActivity;
import com.example.kornel.alphaui.sharelocation.ShareYourLocationActivity;
import com.example.kornel.alphaui.friends.FriendsActivity;
import com.example.kornel.alphaui.login.LoginActivity;
import com.example.kornel.alphaui.R;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment {
    private Button mShareYourLocationButton;
    private Button mFindOtherRunnersButton;
    private Button mFriendsButton;
    private Button mLogoutButton;

    public MoreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        mShareYourLocationButton = rootView.findViewById(R.id.shareYourLocationButton);
        mFindOtherRunnersButton = rootView.findViewById(R.id.findOtherRunnersButton);
        mFriendsButton = rootView.findViewById(R.id.friendsButton);
        mLogoutButton = rootView.findViewById(R.id.logoutButton);

        mShareYourLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShareYourLocationActivity.class);
                startActivity(intent);
            }
        });

        mFindOtherRunnersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindOthersActivity.class);
                startActivity(intent);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
                Intent i = new Intent(getContext(), SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
