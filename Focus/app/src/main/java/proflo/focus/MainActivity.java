package proflo.focus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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



    Vector<FrameLayout> layouts;
    Vector<Integer> layoutIds;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profiles:
                    setFrameVisible(profileFrame);
                    mTextMessage.setText(R.string.title_profiles);
                    return true;
                case R.id.navigation_schedule:
                    setFrameVisible(schedulesFrame);
                    mTextMessage.setText(R.string.title_schedules);
                    return true;
                case R.id.navigation_timers:
                    setFrameVisible(timerFrame);
                    mTextMessage.setText(R.string.title_timers);
                    return true;
                case R.id.navigation_notifications:
                    setFrameVisible(notificationsFrame);
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

        
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();



        //set all the framelayouts to invisible
        setupFrames();
        setFrameVisible(profileFrame);




        mTextMessage = (TextView) findViewById(R.id.profile_header);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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

    void setupFrames()
    {
        profileFrame = (FrameLayout)findViewById(R.id.profiles);
        schedulesFrame = (FrameLayout)findViewById(R.id.schedules);
        timerFrame = (FrameLayout)findViewById(R.id.timers);
        notificationsFrame = (FrameLayout)findViewById(R.id.notifications);

        // instantiate the vector of layouts
        layouts = new Vector<FrameLayout>();
        layoutIds = new Vector<Integer>();

        // add all the layouts to the vector
        layouts.add(profileFrame);
        layouts.add(schedulesFrame);
        layouts.add(timerFrame);
        layouts.add(notificationsFrame);

        layoutIds.add(R.id.profiles);
        layoutIds.add(R.id.schedules);
        layoutIds.add(R.id.timers);
        layoutIds.add(R.id.notifications);


    }

    void setFrameVisible(FrameLayout frameVisible)
    {
        // set the frame to visible
        frameVisible.setVisibility(View.VISIBLE);

        // iterate through all id's and make them invisible
        for(FrameLayout frame: layouts)
        {
            if(frame != frameVisible)
            {
                frame.setVisibility(View.GONE);
            }
        }
    }

}
