package com.proflow.focus_v2.activities;

import android.app.Application;
import android.content.Context;

/**
 * Created by alexinaboudreaux on 10/29/17.
 */

public class ContextActivity extends Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ContextActivity.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ContextActivity.context;
    }
}
