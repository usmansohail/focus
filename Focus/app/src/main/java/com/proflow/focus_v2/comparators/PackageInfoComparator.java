package com.proflow.focus_v2.comparators;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import java.util.Comparator;

/**
 * Created by forre on 10/22/2017.
 */

public class PackageInfoComparator implements Comparator<PackageInfo> {

    private final String TAG = "PackageInfoComparator";
    private final Context mContext;

    public PackageInfoComparator(Context context) {
        mContext = context;
    }

    @Override
    public int compare(PackageInfo pi1, PackageInfo pi2) {

        String pi1Label = pi1.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
        String pi2Label = pi2.applicationInfo.loadLabel(mContext.getPackageManager()).toString();

        int toReturn = pi1Label.compareToIgnoreCase(pi2Label);

        Log.d(TAG, "Return val: " + toReturn + " given A: " + pi1Label + " B: " + pi2Label);
        return toReturn;
    }
}
