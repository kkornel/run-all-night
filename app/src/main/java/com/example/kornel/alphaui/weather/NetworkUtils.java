package com.example.kornel.alphaui.weather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.kornel.alphaui.R;

public class NetworkUtils {
    private static NetworkUtils sInstance = new NetworkUtils();

    public static NetworkUtils getInstance() {
        return sInstance;
    }

    public static boolean isConnected(final Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void requestInternetConnection(View view) {
        Snackbar.make(
                view,
                R.string.enable_internet,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        view.getContext().startActivity(viewIntent);
                    }
                })
                .show();
    }
}
