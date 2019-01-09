package com.example.kornel.alphaui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.kornel.alphaui.utils.User;

import static com.example.kornel.alphaui.sharelocation.FindOthersMapFragment.EXTRA_USER;

public class ViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "ViewProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        User u = (User) getIntent().getExtras().getSerializable(EXTRA_USER);
        Log.d(TAG, "onClickConfirmed: " + u);
    }
}
