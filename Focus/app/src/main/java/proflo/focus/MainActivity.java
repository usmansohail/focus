package proflo.focus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //set all the framelayouts to invisible
        setupFrames();
        setFrameVisible(profileFrame);




        mTextMessage = (TextView) findViewById(R.id.profile_header);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
