/**
 * Copyright (C) Data4All
 */
package io.github.data4all.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class to request the current network state.
 * 
 * @author tbrose
 */
public final class NetworkState {
    /**
     * Private sole constructor so no instances of this class can exists.
     */
    private NetworkState() {
    }

    /**
     * Provides the current network state of the given context.
     * 
     * @param context
     *            The context to request the network state from
     * @return {@code true} if the device is connected to a network<br/>
     *         {@code false} otherwise
     * @throws IllegalArgumentException
     *             if the given context is {@code null}
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        } else {
            final ConnectivityManager connectivityManager =
                    (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetworkInfo =
                    connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}
