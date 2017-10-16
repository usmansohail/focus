package proflo.focus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.Vector;
import java.util.zip.Inflater;

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
    Vector<Integer> toolbars;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profiles:
                    setFrameVisible(frameIndex.PROFILE.ordinal());
                    invalidateOptionsMenu();
                    mTextMessage.setText(R.string.title_profiles);
                    return true;
                case R.id.navigation_schedule:
                    setFrameVisible(frameIndex.SCHEDULE.ordinal());
                    invalidateOptionsMenu();
                    mTextMessage.setText(R.string.title_schedules);
                    return true;
                case R.id.navigation_timers:
                    setFrameVisible(frameIndex.TIMER.ordinal());
                    invalidateOptionsMenu();
                    mTextMessage.setText(R.string.title_timers);
                    return true;
                case R.id.navigation_notifications:
                    setFrameVisible(frameIndex.NOTIFICATION.ordinal());
                    mTextMessage.setText(R.string.title_notifications);
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



        // create the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //set all the framelayouts to invisible
        setupFrames();
        setFrameVisible(frameIndex.PROFILE.ordinal());


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupProfile();

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


    void setupProfile()
    {
        // create the views for all the profles in the database

        // to be filled in
        createProfile("Test Profile", true);
        createProfile("Another Test", false);
    }

    void createProfile(String profileName, boolean status)
    {
        // get the table to fill in
        TableLayout tableLayout = (TableLayout)findViewById(R.id.profile_table);
        TableRow tableRow = new TableRow(MainActivity.this);


        // create a toggle button with the correct status
        ToggleButton toggleButton = new ToggleButton(MainActivity.this);
        toggleButton.setChecked(status);

        // set the parameters of the button
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(150, 75);
        buttonParams.gravity = Gravity.RIGHT;
        buttonParams.weight = 10;
        toggleButton.setLayoutParams(buttonParams);
        toggleButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        // handle the situation of the button being pressed
        //toggleButton.


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
        frameLayout.addView(toggleButton);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tableLayout.getWidth(), 150);
        params.height = 150;
        params.width = tableLayout.getWidth() - 1;
        params.weight = 10;
        frameLayout.setLayoutParams(params);

        // add the frame to the table
        tableLayout.addView(frameLayout);


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


}
