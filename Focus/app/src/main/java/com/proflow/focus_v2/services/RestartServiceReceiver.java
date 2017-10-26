package com.proflow.focus_v2.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by forre on 10/26/2017.
 */

public class RestartServiceReceiver extends BroadcastReceiver
{

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), AppBlocker.class));

    }

}
