package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.LoginActivity;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.proflow.focus_v2.models.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class SchedulesFragment extends BaseFragment {

    private WeekView mWeekView;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    public static SchedulesFragment newInstance() {
        SchedulesFragment fragment = new SchedulesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showAddButton(true);
    }

    //Global views
    ListView scheduleListView;
    ImageButton addScheduleButton;
    ImageButton expandMenuButton;

    //RecyclerView Adapter
//    ScheduleAdapter mAdapter;

    //Schedule List
//    Vector<Schedule> schedules;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_schedules, container, false);
        expandMenuButton = largerMenuButton;

        if(getArguments() != null){
            int scheduleToDeleteID = getArguments().getInt(getString(R.string.schedule_id_to_delete));
            Schedule scheduleToDelete = Global.getInstance().getScheduleById(scheduleToDeleteID);
            Global.getInstance().removeSchedule(getContext(), scheduleToDelete);
            resetNotificationFlags();
            this.setArguments(null);
        }

        resetToolbar();

        //instantiate global views
//        scheduleListView = layout.findViewById(R.id.schedule_list_view);
        addScheduleButton = addItemButton;

        /*
            Deprecated SchedulesListView
         */

//        Log.d("SchedulesFragment", "starting global getSchedules");

//        Log.d("SchedulesFragment", "Creating new Schedule Adapter");
//        mAdapter = new ScheduleAdapter(getActivity());
        // attach the adapter to the expandable list view
//        scheduleListView.setAdapter(mAdapter);

        weekViewSetup(layout);
        Log.d("SchedulesFragment", "Creating new Schedule Adapter");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Schedules");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<Schedule> temp = new Vector<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Schedule schedule = new Schedule(id);
                    temp.add(schedule);
                }
                //When data is changed, it should just be added to the Globals. TBD
//                mAdapter = new ScheduleAdapter(getActivity(), temp);
                // attach the adapter to the expandable list view
//                scheduleListView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.getInstance().synchAll(getContext());
                Fragment frag = CreateScheduleFragment.newInstance();
                Bundle args = new Bundle();
                Schedule sched =
                        new Schedule("", new Vector<TimeBlock>(), false);
                Global.getInstance().addSchedule(getContext(), sched);
                Global.getInstance().synchSchedules(getContext());
                Log.d("SchedulesFragment", "Adding scheduleID: " + sched.getId() + " to args");
                args.putInt(getString(R.string.scheduleKey), (int) sched.getId());
                args.putBoolean(getString(R.string.schedule_is_new), true);
                frag.setArguments(args);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });



        //TODO
        expandMenuButton.setVisibility(View.VISIBLE);
        expandMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO figure out what would even need to go into a menu, and then add that here
                //Recommend looking for a dropdown library?
                PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.container_menu));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.options_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Toast toast = Toast.makeText(getContext(), "clicked button", Toast.LENGTH_SHORT);
                        toast.show();
                        switch (menuItem.getItemId())
                        {
                            case R.id.logout:
                                Intent logout = new Intent(getContext(), LoginActivity.class);
                                startActivity(logout);



                                return true;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


                return layout;
    }

    private void weekViewSetup(View layout) {
        mWeekView = layout.findViewById(R.id.weekView);

// Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                Bundle args = new Bundle();
                args.putInt(getContext().getString(R.string.scheduleKey), (int) event.getId());
                args.putBoolean(getContext().getString(R.string.schedule_is_new), false);

                CreateScheduleFragment frag = CreateScheduleFragment.newInstance();
                frag.setArguments(args);

                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);

                ft.commit();
            }
        });

// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            public static final String TAG = "WeekViewSetup";

            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

                Log.d(TAG, "onMonthChange");
                Vector<Schedule> allSchedules = Global.getInstance().getSchedules(getContext());

                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                // Calendar Based Method(s)
                //Attempt 1
//                for(Schedule s: allSchedules){
//                    for(TimeBlock tb : s.getTimeBlocks()){
//                        time start = tb.getStartTime();
//                        time end = tb.getEndTime();
//                        for(TimeBlock.day d : tb.getDays()){
//                            Calendar startCalendar = Calendar.getInstance();
//                            startCalendar.set(Calendar.YEAR, newYear);
//                            startCalendar.set(Calendar.MONTH, newMonth - 1);
//                            startCalendar.set(Calendar.DAY_OF_WEEK, TimeBlock.day.toInteger(d) + 1);
//                            startCalendar.set(Calendar.HOUR, start.hour);
//                            startCalendar.set(Calendar.MINUTE, start.minute);
//                            startCalendar.roll(Calendar.WEEK_OF_YEAR, -1*startCalendar.get(Calendar.WEEK_OF_MONTH));
//                            Calendar endCalendar = (Calendar) startCalendar.clone();
//                            endCalendar.set(Calendar.HOUR, end.hour);
//                            endCalendar.set(Calendar.MINUTE, end.minute);
//
//                            int numWeeks = startCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
//                            int originalMonth = startCalendar.get(Calendar.MONTH);
//
//                            Log.d(TAG, "START VAL: " + startCalendar);
//
//                            for(int i = 0; i < numWeeks; i++){
////                                if(startCalendar.get(Calendar.MONTH) == originalMonth) {
//                                    WeekViewEvent event = new WeekViewEvent(s.getId(), s.getName(), (Calendar) startCalendar.clone(), (Calendar) endCalendar.clone());
//                                    if (s.isActive())
//                                        event.setColor(getContext().getColor(R.color.ActivatedSwitch));
//                                    else
//                                        event.setColor(getContext().getColor(R.color.primaryColor));
//                                    events.add(event);
////                                }
//                                Log.d(TAG, "START AFTER " + i + " ITERATIONS: " + startCalendar);
//                                startCalendar.roll(Calendar.WEEK_OF_YEAR, 1);
//                                endCalendar.roll(Calendar.WEEK_OF_YEAR, 1);
//                            }
//                        }
//                    }
//                }

                for(Schedule s : allSchedules){
                    for(TimeBlock tb : s.getTimeBlocks()){
                        Vector<Integer> days = new Vector<>();
                        for(TimeBlock.day d : tb.getDays()){
                            days.add(TimeBlock.day.toInteger(d) + 1);
                        }
                        Calendar startCal = Calendar.getInstance();
                        startCal.set(Calendar.MONTH, newMonth-1);
                        startCal.set(Calendar.YEAR, newYear);
                        startCal.set(Calendar.DAY_OF_MONTH, 1);
                        if(tb.getStartTime().hour < 12){
                            startCal.set(Calendar.AM_PM, 0);
                            startCal.set(Calendar.HOUR, tb.getStartTime().hour);
                        } else {
                            startCal.set(Calendar.AM_PM, 1);
                            startCal.set(Calendar.HOUR, tb.getStartTime().hour-12);
                        }
                        startCal.set(Calendar.MINUTE, tb.getStartTime().minute);
                        int maxDays = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        Log.d(TAG, "Max days in month: " + maxDays);
                        for(int i = 0; i < startCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                            if(days.contains(startCal.get(Calendar.DAY_OF_WEEK))){

                                Log.d(TAG, "START VAL: " + startCal);
                                Calendar endCal = (Calendar) startCal.clone();
                                if(tb.getEndTime().hour < 12){
                                    endCal.set(Calendar.AM_PM, 0);
                                    endCal.set(Calendar.HOUR, tb.getEndTime().hour);
                                } else {
                                    endCal.set(Calendar.AM_PM, 1);
                                    endCal.set(Calendar.HOUR, tb.getEndTime().hour-12);
                                }
                                endCal.set(Calendar.MINUTE, tb.getEndTime().minute);
                                WeekViewEvent event = new WeekViewEvent(s.getId(), s.getName(), (Calendar) startCal.clone(),(Calendar)  endCal.clone());
                                if (s.isActive())
                                    event.setColor(getContext().getColor(R.color.ActivatedSwitch));
                                else
                                    event.setColor(getContext().getColor(R.color.primaryColor));
                                events.add(event);
                            }
                            startCal.roll(Calendar.DAY_OF_MONTH, 1);
                        }
                    }
                }

//                for(Schedule s : Global.getInstance().getSchedules(getContext())){
//                    Log.d(TAG, "Got schedule: " + s.getName());
//                    for(TimeBlock tb : s.getTimeBlocks()){
//                        Calendar currTime = Calendar.getInstance();
//                        for(TimeBlock.day day : tb.getDays()){
//                            int dayOfWeekInt = TimeBlock.day.toInteger(day);
//
//                            for(int i = 0; i < 4; i++){
//                                events.add(
//                                        new WeekViewEvent(s.getId(), s.getName(),
//                                        currTime.get(Calendar.YEAR), newMonth, dayOfWeekInt, tb.getStartTime().hour, tb.getStartTime().minute,
//                                        currTime.get(Calendar.YEAR), newMonth, dayOfWeekInt, tb.getEndTime().hour, tb.getEndTime().minute));
//                            }
//                        }
//                    }
//                }

                return events;
            }
        });

// Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
                if(event != null){
                    Schedule current = Global.getInstance().getScheduleById((int) event.getId());
                    if(current.isActive()){
                        current.setActive(false);
                        event.setColor(getContext().getColor(R.color.primaryColor));
                    } else {
                        current.setActive(true);
                        event.setColor(getContext().getColor(R.color.ActivatedSwitch));
                    }
                    mWeekView.notifyDatasetChanged();
                }
            }
        });

        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                switch(date.get(Calendar.DAY_OF_WEEK)){
                    case 1:
                        return "SUNDAY";
                    case 2:
                        return "MONDAY";
                    case 3:
                        return "TUESDAY";
                    case 4:
                        return "WEDNESDAY";
                    case 5:
                        return "THURSDAY";
                    case 6:
                        return "FRIDAY";
                    case 7:
                        return "SATURDAY";
                }
                return "HOW?";
            }

            @Override
            public String interpretTime(int hour) {
                if(hour == 0){
                    return "12:00AM";
                } else if(hour < 12){
                    return "" + hour + ":00AM";
                } else if(hour == 12) {
                    return "12:00PM";
                } else {
                    return "" + (hour-12) + ":00PM";
                }
            }
        });
    }

    public void resetNotificationFlags(){
        Vector<Boolean> blockingProfiles = new Vector<>();
        Vector<Schedule> schedules = Global.getInstance().getSchedules();
        for(int i=0; i<schedules.size(); i++){
            blockingProfiles.add(schedules.get(i).isBlocking());
        }
        Global.getInstance().setScheduleFlags(getContext(), blockingProfiles);
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(!prefs.contains(getString(R.string.firstScheduleKey))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstScheduleKey), false).apply();
        }
    }

    Animation mEnterAnimation, mExitAnimation;

    private void runProfileTutorial() {
        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        runOverlay_ContinueMethod();
    }

    private void runOverlay_ContinueMethod(){
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Schedules")
                        .setDescription("Schedules are a set of times, and apps, that tell Focus! " +
                                "when to block apps for you")
                        .setGravity(Gravity.NO_GRAVITY)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mWeekView);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("Just like Profiles, click here to create a schedule.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(addScheduleButton);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                .setDescription("When you create a Schedule, it will show up in its designated spot" +
                        " in the calendar. Tap it if you want to edit, or delete it, and log hold it" +
                        " to turn it on or off.")

                        .setGravity(Gravity.NO_GRAVITY)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mWeekView);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();


        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }
}