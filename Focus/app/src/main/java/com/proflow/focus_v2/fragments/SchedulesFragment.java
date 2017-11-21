package com.proflow.focus_v2.fragments;


import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ScheduleAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class SchedulesFragment extends BaseFragment {
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


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User1").child("Schedules");
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
        WeekView mWeekView = layout.findViewById(R.id.weekView);

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

                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                // Calendar Based Method
                for(Schedule s : Global.getInstance().getSchedules(getContext())){
                    Log.d(TAG, "Got schedule: " + s.getName());
                    for(TimeBlock tb : s.getTimeBlocks()){
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.MONTH, newMonth - 1);
                        startTime.set(Calendar.YEAR, newYear);
                        startTime.set(Calendar.HOUR_OF_DAY, tb.getStartTime().hour);
                        startTime.set(Calendar.MINUTE, tb.getStartTime().minute);
                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.HOUR, tb.getEndTime().hour);
                        endTime.set(Calendar.MINUTE, tb.getEndTime().minute);
                        for(TimeBlock.day day : tb.getDays()){
                            Log.d(TAG, "Got day of Week" + day.toString());
                            int dayOfWeekInt = TimeBlock.day.toInteger(day);
                            for(int i = 0; i < 4; i++){
                                startTime.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInt + (i*dayOfWeekInt));
                                endTime.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInt + (i*dayOfWeekInt));
                                WeekViewEvent event = new WeekViewEvent(s.getId(), s.getName(), (Calendar) startTime.clone(), (Calendar) endTime.clone());
                                event.setColor(getResources().getColor(R.color.ActivatedSwitch));
                                events.add(event);
                            }
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
}