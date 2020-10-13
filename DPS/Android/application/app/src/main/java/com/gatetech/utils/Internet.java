package com.gatetech.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Internet {

    public enum ESTATUS {

        URL_AVAILABLE ("url_available"),
        DISABLED_NETWORK ("disabled_network"),
        URL_NOT_AVAILABLE ("url_not_available");

        private final String stringValue;
        ESTATUS(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }


    static public ESTATUS isNetworkAvailable (Activity activity,String url) throws IOException {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            networkInfo.getState();
         return ESTATUS.URL_AVAILABLE;


        } else {
            return ESTATUS.DISABLED_NETWORK;
        }

    }

}
