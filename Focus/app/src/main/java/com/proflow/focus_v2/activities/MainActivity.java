package com.proflow.focus_v2.activities;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.Manifest;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.comparators.PackageInfoComparator;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.fragments.BaseFragment;
import com.proflow.focus_v2.fragments.CreateScheduleFragment;
import com.proflow.focus_v2.fragments.CreateTimeBlockFragment;
import com.proflow.focus_v2.fragments.NotificationsFragment;
import com.proflow.focus_v2.fragments.ProfilesFragment;
import com.proflow.focus_v2.fragments.SchedulesFragment;
import com.proflow.focus_v2.fragments.TimersFragment;
import com.proflow.focus_v2.helpers.RetrieveTokenTask;
import com.proflow.focus_v2.models.FocusNotification;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.proflow.focus_v2.models.time;
import com.proflow.focus_v2.services.AppBlocker;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1019;
    private static final int SIGN_IN_FOR_CALENDAR = 42069 ;
    private final String TAG = "MainActivity";
    boolean debug = false;

    //Layouts
    FrameLayout mainFrame;

    //Fragments
    public static NotificationsFragment notificationsFragment;
    public static ProfilesFragment profilesFragment;
    public static SchedulesFragment schedulesFragment;
    public static TimersFragment timersFragment;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

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

        checkPermissions();
        getCalendarPermissions();
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
                    switch(tabId) {
                        case (R.id.tab_notifications):
                            if (currentFragment != fragmentType.NOTIFICATIONS) {
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.Main_Frame, notificationsFragment);
                                transaction.commit();
                                currentFragment = fragmentType.NOTIFICATIONS;
                            }
                            break;
                        case (R.id.tab_profiles):
                            if (currentFragment != fragmentType.PROFILES) {
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.Main_Frame, profilesFragment);
                                transaction.commit();
                                currentFragment = fragmentType.PROFILES;
                            }
                            break;
                        case (R.id.tab_schedules):
                            Log.d(TAG, "Switching to schedules tab...");
                            if (currentFragment != fragmentType.SCHEDULES) {
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.Main_Frame, schedulesFragment);
                                transaction.commit();
                                currentFragment = fragmentType.SCHEDULES;
                            }
                            break;
                        case (R.id.tab_timers):
                            if (currentFragment != fragmentType.TIMERS) {
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
        Global.getInstance().clearAll();
        populateData();


        if(savedInstanceState == null){
            currentFragment = null;
        }

        if(!AppBlocker.running){
            startService(new Intent(this, AppBlocker.class));
            Log.d("SERVICE IND:", "the appBlocker wasn't running");
        }

        if(AppBlocker.blocked){
            BlockedApplicationAlert().show();
            AppBlocker.blocked = false;
            Log.d("SERVICE IND:", "the appBlocker was blocked");
        }


        // initially go to the profiles fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Main_Frame, profilesFragment);
        transaction.commit();
        currentFragment = fragmentType.PROFILES;
        Log.d("TAG", "The profiles fragment was just put in the main activity");



    }

    private void populateGlobalAppsList() {
        if(debug) {
            Global.getInstance().clearPreferences(getApplicationContext());
        }

        PackageManager packageManager = getPackageManager();

        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        packageList1 = new Vector<>();
        for (PackageInfo pi : packageList) {
            String label = pi.applicationInfo.loadLabel(getPackageManager()).toString();
            if ((!isSystemPackage(pi) ||  systemPackageExceptions.contains(pi.packageName) )
                    && (!label.equals("") && !label.isEmpty())
                    && !pi.applicationInfo.loadLabel(getPackageManager()).toString().contains(".")) {
                packageList1.add(pi);
            }
        }

        Collections.sort(packageList1, new PackageInfoComparator(getApplicationContext()));

        Global.getInstance().setAllApps(getApplicationContext(), new Vector<PackageInfo>());
        Global.getInstance().setAllApps(this, packageList1);
        Global.getInstance().synchApps(getApplicationContext());

        //FOR DEBUGGING - note done after apps.
//        if(debug) {
//            populateFakeData();
//        }
    }

    private void populateData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<Profile> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Profile profile = new Profile(id);
                    temp.add(profile);
                }
                Global.getInstance().setProfiles(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference not = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        not.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<FocusNotification> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    FocusNotification notification = new FocusNotification(id);
                    temp.add(notification);
                }
                Global.getInstance().setNotifications(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference sch = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Schedules");
        sch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<Schedule> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Schedule schedule = new Schedule(id);
                    temp.add(schedule);
                }
                Global.getInstance().setSchedules(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference tim = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Timers");
        tim.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<FocusTimer> temp = new Vector<FocusTimer>();
                for(DataSnapshot id : dataSnapshot.getChildren()) {
                    FocusTimer timer = new FocusTimer(id);
                    Log.e("Adding timer!", "Adding timers: " + timer.getId());
                    temp.add(timer);
                }
                Global.getInstance().setTimers(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateFakeData() {
        /*Random rand = new Random();

        //Random profiles!
        for(int i = 0; i < 10; i++){
            Vector<String> apps = new Vector<>();
            Vector<PackageInfo> allApps = Global.getInstance().getAllApps(getApplicationContext());


            int numActiveApps = rand.nextInt(15);
            Vector<Integer> selected = new Vector<>();
            for(int j = 0; j < numActiveApps; j++){
                int appIndex = rand.nextInt(allApps.size());
                while(selected.contains(appIndex)){
                    appIndex = rand.nextInt(allApps.size());
                }
                apps.add(allApps.get(appIndex).packageName);
                selected.add(appIndex);
            }

            Profile p = new Profile("Profile " + i, apps, i);
            Global.getInstance().addProfile(getApplicationContext(), p);
        }

        //Random schedules!
        for(int i = 0; i < 10; i++){
            String sName = "Schedule " + i;

            int numTimeBlocks = rand.nextInt(5) + 5;
            Vector<TimeBlock> tbVec = new Vector<>();

            for(int j = 0; j < numTimeBlocks; j++) {
                int startHour = rand.nextInt(12);
                int startMinute = rand.nextInt(60);
                int endHour = rand.nextInt(12) + 12;
                int endMinute = rand.nextInt(60);

                time start = new time(startHour, startMinute);
                time end = new time(endHour, endMinute);
                TimeBlock.day day = TimeBlock.day.fromInteger(rand.nextInt(7));


                tbVec.add(new TimeBlock(start, end, day));
            }

            //now add a random assortment of a few profiles
            Vector<Profile> pVec = new Vector<>();
            int numProfiles = rand.nextInt(5);
            Vector<Integer> selected = new Vector<>();
            for(int j = 0; j < numProfiles; j++){
                int profileIndex = rand.nextInt(10);
                while(selected.contains(profileIndex)){
                    profileIndex = rand.nextInt(5);
                }
                pVec.add(Global.getInstance().getProfileByID(getApplicationContext(), profileIndex));
            }
            boolean mRepeat = false;
            if(rand.nextInt() % 2 == 0){
                mRepeat = true;
            }

            Global.getInstance().addSchedule(getApplicationContext(), new Schedule(sName, tbVec, pVec, mRepeat, i));
        }*/

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) || pkgInfo.packageName.contains("focus"));
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.Main_Frame);

        if(frag instanceof CreateTimeBlockFragment){
            int schedID = ((CreateTimeBlockFragment)frag).getScheduleId();

            Bundle args = new Bundle();
            args.putInt(getString(R.string.scheduleKey), schedID);

            getSupportFragmentManager().popBackStack();

            CreateScheduleFragment switchInto = CreateScheduleFragment.newInstance();
            switchInto.setArguments(args);

            bottomBar.setVisibility(View.VISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.Main_Frame, switchInto);
            ft.commit();
        } else {

            super.onBackPressed();

            if (frag == notificationsFragment || frag == profilesFragment || frag == schedulesFragment
                    || frag == timersFragment) {
                ((BaseFragment) frag).resetToolbar();
            }
        }
    }

    public void switchContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.Main_Frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void getCalendarPermissions()
    {
        // get permissions for google API
        final int REQUEST_CODE = 101;

        final String[] permissions = {android.Manifest.permission.WRITE_CALENDAR,
                android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.INTERNET,
                android.Manifest.permission.GET_ACCOUNTS};

        String[] permissionNames = {"Write to Calendar", "Read from Calendar", "Use the internet ",
                "Get Google Accounts"};

        String prompt = "You need to grant all of the following permissions to use this app: " ;
        for(int i = 0; i < permissions.length; i++)
        {
            Log.d("PERMISSION", permissionNames[i]);
            prompt = prompt + permissionNames[i];
            if(i < permissions.length - 1)
            {
                prompt = prompt + ", ";
            }
        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_CALENDAR) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_CALENDAR) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.GET_ACCOUNTS) !=
                        PackageManager.PERMISSION_GRANTED ) {
            new AlertDialog.Builder(this)
                    .setMessage(prompt)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(permissions, REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();

        }
    }

    public boolean checkPermissions(){



        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            // ask for permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }

        //dialog to turn on permissions appears if notification service has not yet been enabled
        if(!isUsageAccessEnabled(getApplicationContext())){
            buildUsageAccessPermissionsAlertDialog().show();
        }
        if(!isNotificationServiceEnabled() && isUsageAccessEnabled(getApplicationContext())) {
            buildNotificationPermissionsAlertDialog().show();
        }
        if(!isNotificationServiceEnabled() || !isUsageAccessEnabled(getApplicationContext())){
            return false;
        }
        return true;
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isUsageAccessEnabled(Context context) {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private android.app.AlertDialog buildNotificationPermissionsAlertDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_permissions_title);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.deny,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        return(alertDialogBuilder.create());
    }

    private android.app.AlertDialog buildUsageAccessPermissionsAlertDialog(){
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.usage_permissions_title);
        alertDialogBuilder.setMessage(R.string.usage_access_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        dialog.dismiss();
                        if(!isNotificationServiceEnabled()) {
                            buildNotificationPermissionsAlertDialog().show();
                        }
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.deny,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        return(alertDialogBuilder.create());
    }

    private android.app.AlertDialog BlockedApplicationAlert(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Blocked Application");
        alertDialogBuilder.setMessage("Focus! You are trying to access a distracting application that has been blocked! ");
        alertDialogBuilder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return(alertDialogBuilder.create());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // get the account
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.ACCOUNT_INFO), Context.MODE_PRIVATE);
        String accountName = sharedPreferences.getString(getString(R.string.ACCOUNT_NAME), "");

        if(requestCode == SIGN_IN_FOR_CALENDAR && resultCode == RESULT_OK)
        {
            new RetrieveTokenTask().execute(accountName, this);
        }
    }

}
