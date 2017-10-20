package com.fdunlap.focus_v2.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.fragments.BaseFragment;
import com.fdunlap.focus_v2.fragments.NotificationsFragment;
import com.fdunlap.focus_v2.fragments.ProfilesFragment;
import com.fdunlap.focus_v2.fragments.SchedulesFragment;
import com.fdunlap.focus_v2.fragments.TimersFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //Layouts
    FrameLayout mainFrame;

    //Fragments
    NotificationsFragment notificationsFragment;
    ProfilesFragment profilesFragment;
    SchedulesFragment schedulesFragment;
    TimersFragment timersFragment;

    Vector<Fragment> fragments;

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

        //Currently just switches between 'tabs' Not currently managing new fragments.
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

        if(savedInstanceState != null){
            return;
        } else {
            currentFragment = null;
        }
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
}
