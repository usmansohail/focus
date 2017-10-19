package proflo.focus;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    //I low key find the activity package pointing kinda clever....

    // global activity variables here
    FrameLayout profileFrame;
    FrameLayout schedulesFrame;
    FrameLayout timerFrame;
    FrameLayout notificationsFrame;



    public static final String PROFILE_STATUS = "proflo.focus.profile_status";
    public static final String SCHEDULE_STATUS = "proflo.focus.schedule_status";
    public static final String TIMER_STATUS = "proflo.focus.timer_status";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ANDROID_MESSAGING = "com.android.messaging";
    private static final String ANDROID_EMAIL = "com.android.email";
    private static final String ANDROID_GESTURE_BUILDER = "com.android.gesture.builder";
    private static final String ANDROID_API_DEMOS = "API Demos";

    // these booleans indicate which framelayout is active
    boolean profileActive;
    boolean scheduleActive;
    boolean timerActive;

    // delete these after
    boolean isOn = false;
    boolean hasStarted = true;


    public enum frameIndex {PROFILE, SCHEDULE, TIMER, NOTIFICATION}

    // keeps track of the index of the active frame
    int activeFrame;

    // keeps track of if the data was changed
    boolean schedulesChanged;
    boolean timersChanged;
    boolean profileChanged;
    boolean notificationsChanged;

    Vector<FrameLayout> layouts;
    Vector<Integer> toolbars;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profiles:
                    setupProfile();
                    setFrameVisible(frameIndex.PROFILE.ordinal());
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_schedule:
                    setupSchedules();
                    setFrameVisible(frameIndex.SCHEDULE.ordinal());
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_timers:
                    setupTimers();
                    setFrameVisible(frameIndex.TIMER.ordinal());
                    invalidateOptionsMenu();
                    return true;
                case R.id.navigation_notifications:
                    setupNotifications();
                    setFrameVisible(frameIndex.NOTIFICATION.ordinal());
                    invalidateOptionsMenu();
                    return true;
            }
            return false;
        }


    };


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        setToolbar(menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // assume the data was changed in order to repopulate stuff
        schedulesChanged = true;
        timersChanged = true;
        profileChanged = true;
        notificationsChanged = true;


        // create the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //set all the framelayouts to invisible
        setupFrames();
        setFrameVisible(frameIndex.PROFILE.ordinal());


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupProfile();

        //dialog to turn on permissions appears if notification service has not yet been enabled
        if(!isNotificationServiceEnabled()){
            buildNotificationPermissionsAlertDialog().show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // this method adds buttons to the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // use the setToolbar method to handle this situation
        setToolbar(menu);
        return true;
    }

    public void setToolbar(Menu menu)
    {
        // create a menuInflator to inflate the menu
        MenuInflater inflator = getMenuInflater();

        // check which layout is active
        for(int i = 0; i < layouts.size(); i++)
        {
            if(activeFrame == i)
            {
                // set the ith toolbar to active
                inflator.inflate(toolbars.get(i), menu);
            }
        }

    }


    // This method gives functionality to each menu button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_profile:
                Intent intentProfile = new Intent(this, ModifyProfileActivity.class);
                Boolean newProfile = true;
                intentProfile.putExtra(PROFILE_STATUS, newProfile);
                startActivity(intentProfile);
                updateAvailableApps();
                return true;

            case R.id.add_schedule:
                Intent intentSchedule = new Intent(this, ModifyScheduleActivity.class);
                Boolean newSchedule = true;
                intentSchedule.putExtra(SCHEDULE_STATUS, newSchedule);
                startActivity(intentSchedule);
                return true;

            case R.id.add_timer:
                Intent intentTimer = new Intent(this, ModifyTimerActivity.class);
                Boolean newTimer = true;
                intentTimer.putExtra(TIMER_STATUS, newTimer);
                startActivity(intentTimer);
                return true;

            case R.id.clear_all_notifications:
                //TODO Logic of removing all notifications


        }
        return true;
    }


    void setupProfile()
    {
        while(!Global.getInstance().loaded){}

        //TODO populate the views for all the profiles in the database
        Vector<Profile> profiles = Global.getInstance().getAllProfiles();
        for(Profile p: profiles){
            createProfile(p.getName(), p.isActive());
        }
    }

    boolean toggleProfile()
    {
        return true;
    }

    void setupNotifications()
    {
        // get all the notifications in the notification vector

        // some dummies for now:
        for(int i = 0; i < 3; i++)
        {
            createNotification("App " + i, getDrawable(R.drawable.cancel_icon));
        }
    }


    void createNotification(String appName, Drawable appIcon)
    {


        // get the table to fill in
        TableLayout tableLayout = (TableLayout)findViewById(R.id.notification_table);
        TableRow tableRow = new TableRow(MainActivity.this);




        // create the framelayout that displays the info
        FrameLayout frameLayout = new FrameLayout(MainActivity.this);
        frameLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        frameLayout.setBottom(4);
        frameLayout.setPadding(35,35,75,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        frameLayout.setBackground(border);

        // create and set the title text
        TextView profileTitle = new TextView(MainActivity.this, null);
        profileTitle.setText(appName);
        profileTitle.setTextSize(25);
        profileTitle.setTypeface(Typeface.DEFAULT_BOLD);
        profileTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);


        // add the text to the parent frame
        frameLayout.addView(profileTitle);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tableLayout.getWidth(), 150);
        params.height = 150;
        params.width = tableLayout.getWidth() - 1;
        params.weight = 10;
        frameLayout.setLayoutParams(params);

        // add the frame to the table
        tableLayout.addView(frameLayout);


    }

    void createProfile(String profileName, final boolean status)
    {
        // button status
        final boolean statusTwo = status;

        // get the table to fill in
        TableLayout tableLayout = (TableLayout)findViewById(R.id.profile_table);
        TableRow tableRow = new TableRow(MainActivity.this);




        // create the framelayout that displays the info
        FrameLayout frameLayout = new FrameLayout(MainActivity.this);
        frameLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        frameLayout.setBottom(4);
        frameLayout.setPadding(35,35,75,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        frameLayout.setBackground(border);

        // create and set the title text
        TextView profileTitle = new TextView(MainActivity.this, null);
        profileTitle.setText(profileName);
        profileTitle.setTextSize(25);
        profileTitle.setTypeface(Typeface.DEFAULT_BOLD);
        profileTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);


        // add the text to the parent frame
        frameLayout.addView(profileTitle);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tableLayout.getWidth(), 150);
        params.height = 150;
        params.width = tableLayout.getWidth() - 1;
        params.weight = 10;
        frameLayout.setLayoutParams(params);

        // add the frame to the table
        tableLayout.addView(frameLayout);


    }

    void setupSchedules()
    {
        // this is where all the schedules in the database should populate the screen

        // sample
        if(schedulesChanged) {
            // actually query from the database/shared preferences
            createSchedule("Sample Schedule", true);
        }

        schedulesChanged = false;
    }

    void createSchedule(String scheduleName, final boolean status)
    {

        // get the table to fill in
        TableLayout tableLayout = (TableLayout)findViewById(R.id.schedules_table);
        //TableRow tableRow = new TableRow(MainActivity.this);

        // create a toggle button with the correct status
        final Switch toggleButton = new Switch(MainActivity.this);
        toggleButton.setChecked(status);

        // set the parameters of the button
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(150, 75);
        buttonParams.gravity = Gravity.RIGHT;
        buttonParams.weight = 10;
        toggleButton.setLayoutParams(buttonParams);
        toggleButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        // handle the situation of the button being pressed
        toggleButton.setOnClickListener(new View.OnClickListener() {

            public boolean onTouch(View v, MotionEvent event) {
                toggleButton.setChecked(toggleProfile());
                return false;
            }

            // keep for now if you decide to switch back to the onClickListener
            @Override
            public void onClick(View v) {
                // set the button to reflect the action
                toggleButton.setChecked(!status);

                // call toggleProfile. this should take in a profile object, and set it's value
                // accordingly


                // should be able to do this:
                toggleButton.setChecked(toggleProfile());

            }
        });


        // create the framelayout that displays the info
        FrameLayout frameLayout = new FrameLayout(MainActivity.this);
        frameLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        frameLayout.setBottom(4);
        frameLayout.setPadding(35,35,75,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        frameLayout.setBackground(border);

        // create and set the title text
        TextView profileTitle = new TextView(MainActivity.this, null);
        profileTitle.setText(scheduleName);
        profileTitle.setTextSize(25);
        profileTitle.setTypeface(Typeface.DEFAULT_BOLD);
        profileTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);


        // add the text to the parent frame
        frameLayout.addView(profileTitle);
        frameLayout.addView(toggleButton);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tableLayout.getWidth(), 150);
        params.height = 150;
        params.width = tableLayout.getWidth() - 1;
        params.weight = 10;
        frameLayout.setLayoutParams(params);

        // add the frame to the table
        //tableRow.addView(frameLayout);
        tableLayout.addView(frameLayout);


    }

    void setupTimers()
    {
        if(timersChanged) {
            // sample timer
            Vector<String> profiles = new Vector<>();
            profiles.add("profile 1");
            profiles.add("profile 2");
            createTimer(3, 45, profiles);
        }
    }

    void createTimer(int hours, int minutes, Vector<String> profiles)
    {
        int buttonSizeConstant = 75;
        int paddingConstant = 75;

        // get  the table layout from the xml
        TableLayout table = (TableLayout) findViewById(R.id.timer_table);

        // create the framelayout that displays the info
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        linearLayout.setBottom(4);
        linearLayout.setPadding(35,35,75,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        linearLayout.setBackground(border);

        // create and set the title text
        TextView time = new TextView(MainActivity.this, null);
        time.setText(hours + " hrs " + minutes + " min");
        time.setTextSize(15);
        time.setTypeface(Typeface.DEFAULT_BOLD);
        time.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        time.setPadding(50, 0, 20, 0);


        TableLayout profileTable = new TableLayout(MainActivity.this);
        profileTable.setPadding(30,0,100,0);

        // add all the profile names to the timer
        for(String profile: profiles)
        {
            TextView profileTitle = new TextView(MainActivity.this, null);
            profileTitle.setText(profile);
            profileTitle.setTextSize(12);
            profileTitle.setGravity(Gravity.LEFT);
            profileTable.addView(profileTitle);
        }
        // add the table to the view
        linearLayout.addView(profileTable);

        // add the play and pause buttons
        final Button playPauseButton = new Button(MainActivity.this);
        if(isOn) {
            playPauseButton.setBackground(getResources().getDrawable(R.drawable.pause_icon));
        }
        else
        {
            playPauseButton.setBackground(getResources().getDrawable(R.drawable.play_icon));
        }
        playPauseButton.setLayoutParams(new FrameLayout.LayoutParams(buttonSizeConstant, buttonSizeConstant));
        playPauseButton.setPadding(75,0,75,0);

        // add the reset button
        final Button resetButton = new Button(MainActivity.this);
        resetButton.setBackground(getResources().getDrawable(R.drawable.reset_icon));
        resetButton.setLayoutParams(new FrameLayout.LayoutParams(buttonSizeConstant, buttonSizeConstant));
        resetButton.setPadding(75,0,75,0);

        // what happens if the play/pause button is clicked
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // setup whatever logic needs to happen on the backend
                if(isOn) {
                    isOn = false;
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.play_icon));
                }
                else
                {
                    isOn = true;
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.pause_icon));
                }

                if(!hasStarted) {
                    hasStarted = true;
                    resetButton.setVisibility(View.VISIBLE);
                }
            }
        });




        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update the variable
                hasStarted = false;

                // remove the button
                resetButton.setVisibility(View.GONE);

                isOn = false;
                playPauseButton.setBackground(getResources().getDrawable(R.drawable.play_icon));
            }
        });

        // add the appropriate button
        linearLayout.addView(playPauseButton);


        // if the timer has started, show the reset button
        if(hasStarted)
        {
            linearLayout.addView(resetButton);
        }


        linearLayout.addView(time);

        // add it  to the table
        table.addView(linearLayout);
    }

    void setupFrames() {
        // instantiate FrameLayouts
        profileFrame = (FrameLayout) findViewById(R.id.profiles);
        schedulesFrame = (FrameLayout) findViewById(R.id.schedules);
        timerFrame = (FrameLayout) findViewById(R.id.timers);
        notificationsFrame = (FrameLayout) findViewById(R.id.notifications);

        // instantiate the vector of layouts
        layouts = new Vector<FrameLayout>();
        toolbars = new Vector<>();

        // add all the layouts to the vector
        layouts.add(profileFrame);
        layouts.add(schedulesFrame);
        layouts.add(timerFrame);
        layouts.add(notificationsFrame);


        // add the toolbar ids to the vector
        toolbars.add(R.menu.profile_toolbar);
        toolbars.add(R.menu.schedule_toolbar);
        toolbars.add(R.menu.timer_toolbar);
        toolbars.add(R.menu.notification_toolbar);


    }

    void setFrameVisible(int frameNum) {
        // get the frame from the int
        FrameLayout frameVisible  = layouts.get(frameNum);

        // set the active frame to the frameNum
        activeFrame = frameNum;

        // set the frame to visible
        frameVisible.setVisibility(View.VISIBLE);

        // iterate through all other frames and make them invisible
        for (FrameLayout frame : layouts) {
            if (frame != frameVisible) {
                frame.setVisibility(View.GONE);
            }
        }
    }

    int getRInt(int num)
    {
        if(num == frameIndex.PROFILE.ordinal())
        {
            return R.id.add_profile;
        }
        else if(num == frameIndex.SCHEDULE.ordinal())
        {
            return R.id.add_schedule;
        }
        else if(num == frameIndex.TIMER.ordinal())
        {
            return R.id.add_timer;
        }
        else
        {
            return R.id.clear_all_notifications;
        }
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
    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }


    private android.app.AlertDialog buildNotificationPermissionsAlertDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.permissions_title);
        alertDialogBuilder.setMessage(R.string.permissions_message);
        alertDialogBuilder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.deny,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //will add functionality later to close app if user chooses no
                    }
                });
        return(alertDialogBuilder.create());
    }

    public void updateAvailableApps(){
        Vector<ApplicationInfo> availableApps = new Vector<ApplicationInfo>();
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES;
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                //Android pre-installed app
                if(appInfo.packageName.equals(ANDROID_EMAIL) || appInfo.packageName.equals(ANDROID_MESSAGING)){
                    availableApps.add(appInfo);
                }
            }
            else {
                //User installed app
                if(!appInfo.packageName.equals(ANDROID_GESTURE_BUILDER) && !appInfo.loadLabel(getPackageManager()).toString().equals(ANDROID_API_DEMOS)){
                    availableApps.add(appInfo);
                }
            }
        }
        Global.getInstance().setAllApps(availableApps);
    }
}
