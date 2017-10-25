package com.proflow.focus_v2.activities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.comparators.PackageInfoComparator;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.fragments.BaseFragment;
import com.proflow.focus_v2.fragments.NotificationsFragment;
import com.proflow.focus_v2.fragments.ProfilesFragment;
import com.proflow.focus_v2.fragments.SchedulesFragment;
import com.proflow.focus_v2.fragments.TimersFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    //Layouts
    FrameLayout mainFrame;

    //Fragments
    NotificationsFragment notificationsFragment;
    ProfilesFragment profilesFragment;
    SchedulesFragment schedulesFragment;
    TimersFragment timersFragment;

    public static Vector<PackageInfo> packageList1;

    Vector<Fragment> fragments;

    //These are the packages that are TECHNICALLY System apps but that should show up in the list
    //of apps that can be blocked.
    private String systemPackageExceptionsArray[] = {
            "com.google.android.youtube",
            "com.google.android.calendar",
            "com.google.android.apps.messaging",
            "com.android.contacts",
            "com.google.android.music",
            "com.android.chrome",
            "com.google.android.apps.photos",
            "com.android.phone",
            "com.google.android.gm",
            "com.google.android.apps.babel",
            "com.google.android.talk",
            "com.android.vending",
            "de.androidpit.app",
            "com.google.android.videos"
    };

    private ArrayList<String> systemPackageExceptions = new ArrayList<>(Arrays.asList(systemPackageExceptionsArray));

    enum fragmentType{
        NOTIFICATIONS,
        PROFILES,
        SCHEDULES,
        TIMERS
    }
    fragmentType currentFragment;

    //BottomBar
    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up globals from IDs
            //mainFrame = Fragment frame
        mainFrame = (FrameLayout) findViewById(R.id.Main_Frame);

        if(fragments == null){
            fragments = new Vector<>();
        }

        //Check to setup fragments
        if (notificationsFragment == null){
            notificationsFragment = NotificationsFragment.newInstance();
            fragments.add(notificationsFragment);
        }
        if (profilesFragment == null){
            profilesFragment = ProfilesFragment.newInstance();
            fragments.add(profilesFragment);
        }
        if (schedulesFragment == null){
            schedulesFragment = SchedulesFragment.newInstance();
            fragments.add(schedulesFragment);
        }
        if (timersFragment == null){
            timersFragment = TimersFragment.newInstance();
            fragments.add(timersFragment);
        }

            //bottomBar + bottombar listener setup
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
            //Bottombar title setup (b/c localization)
        bottomBar.getTabWithId(R.id.tab_notifications).setTitle(getString(R.string.notifications_bottom_bar_tab_title));
        bottomBar.getTabWithId(R.id.tab_profiles).setTitle(getString(R.string.profiles_bottom_bar_tab_title));
        bottomBar.getTabWithId(R.id.tab_schedules).setTitle(getString(R.string.schedules_bottom_bar_tab_title));
        bottomBar.getTabWithId(R.id.tab_timers).setTitle(getString(R.string.timer_bottom_bar_tab_title));

        //Just switches between tabs.
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch(tabId){
                    case(R.id.tab_notifications):
                        if(currentFragment != fragmentType.NOTIFICATIONS){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.Main_Frame, notificationsFragment);
                            transaction.commit();
                            currentFragment = fragmentType.NOTIFICATIONS;
                        }
                        break;
                    case(R.id.tab_profiles):
                        if(currentFragment != fragmentType.PROFILES){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.Main_Frame, profilesFragment);
                            transaction.commit();
                            currentFragment = fragmentType.PROFILES;
                        }
                        break;
                    case(R.id.tab_schedules):
                        if(currentFragment != fragmentType.SCHEDULES){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.Main_Frame, schedulesFragment);
                            transaction.commit();
                            currentFragment = fragmentType.SCHEDULES;
                        }
                        break;
                    case(R.id.tab_timers):
                        if(currentFragment != fragmentType.TIMERS){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.Main_Frame, timersFragment);
                            transaction.commit();
                            currentFragment = fragmentType.TIMERS;
                        }
                        break;
                }
            }
        });

        populateGlobalAppsList();

        if(savedInstanceState != null){
            return;
        } else {
            currentFragment = null;
        }
    }

    private void populateGlobalAppsList() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        packageList1 = new Vector<>();
        for (PackageInfo pi : packageList) {
            String label = pi.applicationInfo.loadLabel(getPackageManager()).toString();
            if ((!isSystemPackage(pi) ||  systemPackageExceptions.contains(pi.packageName) )
                    && (!label.equals("") && !label.isEmpty())
                    && !pi.applicationInfo.loadLabel(getPackageManager()).toString().contains(".")) {
                Log.d(TAG, "Added " + pi.applicationInfo.loadLabel(getPackageManager()) + " to allApps vector.");
                packageList1.add(pi);
            } else {
                Log.e(TAG, pi.packageName + " was not added as it is a systemPackage.");
            }
        }

        Collections.sort(packageList1, new PackageInfoComparator(getApplicationContext()));



        for(PackageInfo pi : packageList1){
            Log.d(TAG, "App: " + pi.applicationInfo.loadLabel(getPackageManager()));
        }

        Global.getInstance().setAllApps(this, packageList1);

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) || pkgInfo.packageName.contains("focus"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.Main_Frame);

        if(frag == notificationsFragment || frag == profilesFragment || frag == schedulesFragment
                || frag == timersFragment){
            ((BaseFragment) frag).resetToolbar();
        }

    }

    public void switchContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.Main_Frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
