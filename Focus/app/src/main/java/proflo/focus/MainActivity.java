package proflo.focus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;


    // global activity variables here
    FrameLayout profileFrame;
    FrameLayout schedulesFrame;
    FrameLayout timerFrame;
    FrameLayout notificationsFrame;

    public static final String PROFILE_STATUS = "proflo.focus.profile_status";
    public static final String SCHEDULE_STATUS = "proflo.focus.schedule_status";
    public static final String TIMER_STATUS = "proflo.focus.timer_status";


    // these booleans indicate which framelayout is active
    boolean profileActive;
    boolean scheduleActive;
    boolean timerActive;

    public enum frameIndex {PROFILE, TIMER, SCHEDULE, NOTIFICATION}

    int activeFrame;

    Vector<FrameLayout> layouts;

    private ActionMode.Callback mActionCallback = new ActionMode.Callback(){


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.main_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            for(int i = 0; i < layouts.size(); i++)
            {
                if(i == activeFrame)
                {
                    // this is the menu item we want
                    menu.findItem(getRInt(i)).setVisible(true);

                    MenuItem item = (MenuItem)menu.findItem(getRInt(i));

                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                else
                {
                    // this is not the correct menu item for this frame
                    menu.findItem(getRInt(i)).setVisible(false);
                }
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add_profile:
                    Intent intentProfile = new Intent(getApplicationContext(), ModifyProfileActivity.class);
                    Boolean newProfile = true;
                    intentProfile.putExtra(PROFILE_STATUS, newProfile);
                    startActivity(intentProfile);
                    return true;

                case R.id.add_schedule:
                    Intent intentSchedule = new Intent(getApplicationContext(), ModifyScheduleActivity.class);
                    Boolean newSchedule = true;
                    intentSchedule.putExtra(SCHEDULE_STATUS, newSchedule);
                    startActivity(intentSchedule);
                    return true;

                case R.id.add_timer:
                    Intent intentTimer = new Intent(getApplicationContext(), ModifyScheduleActivity.class);
                    Boolean newTimer = true;
                    intentTimer.putExtra(TIMER_STATUS, newTimer);
                    startActivity(intentTimer);
                    return true;

                case R.id.clear_all_notifications:
                    //Intent intentClearAllNotifications = new Intent(this, )
                    // don't know what to do yet, since this doesn't need to launch a new activity
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profiles:
                    setFrameVisible(frameIndex.PROFILE.ordinal());
                    invalidateOptionsMenu();
                    MainActivity.this.startSupportActionMode(mActionCallback);
                    mTextMessage.setText(R.string.title_profiles);
                    return true;
                case R.id.navigation_schedule:
                    setFrameVisible(frameIndex.SCHEDULE.ordinal());
                    invalidateOptionsMenu();
                    MainActivity.this.startSupportActionMode(mActionCallback);
                    mTextMessage.setText(R.string.title_schedules);
                    return true;
                case R.id.navigation_timers:
                    setFrameVisible(frameIndex.TIMER.ordinal());
                    invalidateOptionsMenu();
                    MainActivity.this.startSupportActionMode(mActionCallback);
                    mTextMessage.setText(R.string.title_timers);
                    return true;
                case R.id.navigation_notifications:
                    setFrameVisible(frameIndex.NOTIFICATION.ordinal());
                    mTextMessage.setText(R.string.title_notifications);
                    invalidateOptionsMenu();
                    MainActivity.this.startSupportActionMode( mActionCallback);
                    return true;
            }
            return false;
        }


    };

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        for(int i = 0; i < layouts.size(); i++)
        {
            if(i == activeFrame)
            {
                // this is the menu item we want
                menu.findItem(getRInt(i)).setVisible(true);

                MenuItem item = (MenuItem)menu.findItem(getRInt(i));

                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            else
            {
                // this is not the correct menu item for this frame
                menu.findItem(getRInt(i)).setVisible(false);
            }
        }
        return true;
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        // create the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        */

        //set all the framelayouts to invisible
        setupFrames();
        setFrameVisible(frameIndex.PROFILE.ordinal());


        mTextMessage = (TextView) findViewById(R.id.profile_header);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        // start the menu that depends on the view
        this.startSupportActionMode(mActionCallback);


    }

    /*
    // this method adds buttons to the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        return true;
    }
    */

    /*
    // This method gives functionality to each menu button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_profile:
                Intent intentProfile = new Intent(this, ModifyProfileActivity.class);
                Boolean newProfile = true;
                intentProfile.putExtra(PROFILE_STATUS, newProfile);
                startActivity(intentProfile);
                return true;

            case R.id.add_schedule:
                Intent intentSchedule = new Intent(this, ModifyScheduleActivity.class);
                Boolean newSchedule = true;
                intentSchedule.putExtra(SCHEDULE_STATUS, newSchedule);
                startActivity(intentSchedule);
                return true;

            case R.id.add_timer:
                Intent intentTimer = new Intent(this, ModifyScheduleActivity.class);
                Boolean newTimer = true;
                intentTimer.putExtra(TIMER_STATUS, newTimer);
                startActivity(intentTimer);
                return true;

            case R.id.clear_all_notifications:
                //Intent intentClearAllNotifications = new Intent(this, )
                // don't know what to do yet, since this doesn't need to launch a new activity
        }
        return true;
    }
    */

    void setupFrames() {
        // instantiate FrameLayouts
        profileFrame = (FrameLayout) findViewById(R.id.profiles);
        schedulesFrame = (FrameLayout) findViewById(R.id.schedules);
        timerFrame = (FrameLayout) findViewById(R.id.timers);
        notificationsFrame = (FrameLayout) findViewById(R.id.notifications);

        // instantiate the vector of layouts
        layouts = new Vector<FrameLayout>();

        // add all the layouts to the vector
        layouts.add(profileFrame);
        layouts.add(schedulesFrame);
        layouts.add(timerFrame);
        layouts.add(notificationsFrame);

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


}
