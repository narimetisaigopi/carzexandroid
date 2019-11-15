package com.carzex;

import android.content.Context;
import android.net.ConnectivityManager;

public class Utility {

    Context context;

    public boolean TESTING = false;


    public Utility(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
