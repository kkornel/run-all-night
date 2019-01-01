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

import com.example.kornel.alphaui.FindOthersActivity;
import com.example.kornel.alphaui.SettingsActivity;
import com.example.kornel.alphaui.ShareYourLocationActivity;
import com.example.kornel.alphaui.friends.FriendsActivity;
import com.example.kornel.alphaui.LoginActivity;
import com.example.kornel.alphaui.R;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment {
    private static final String TAG = "MoreFragment";

    Button mFreeButton;
    Button mShareYourLocationButton;
    Button mFindOtherRunnersButton;
    Button mFriendsButton;
    Button mLogoutButton;


    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        mFreeButton = rootView.findViewById(R.id.freeButton);
        mShareYourLocationButton = rootView.findViewById(R.id.shareYourLocationButton);
        mFindOtherRunnersButton = rootView.findViewById(R.id.findOtherRunnersButton);
        mFriendsButton = rootView.findViewById(R.id.friendsButton);
        mLogoutButton = rootView.findViewById(R.id.logoutButton);

        mFreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
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
