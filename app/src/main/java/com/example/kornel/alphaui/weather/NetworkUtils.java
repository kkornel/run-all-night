package com.example.kornel.alphaui.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
