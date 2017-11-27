package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.adapters.TimeBlockAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.google.api.client.*;
import com.google.api.services.calendar.model.Calendar;

import java.util.Vector;

import static android.content.ContentValues.TAG;

public class CreateScheduleFragment extends BaseFragment {

    EditText mNameEditText;
    ListView mTimeBlockList;
    AppCompatCheckBox mRepeatWeeklyButton;
    private TimeBlockAdapter mTimeBlockAdapter;

    private boolean mIsNew = false;

    private Schedule mSchedule = null;

    private RecyclerView mProfileRecycler;
    private ProfileAdapter mProfileAdapter;

    public CreateScheduleFragment() {
        // Required empty public constructor
    }

    public static CreateScheduleFragment newInstance() {
        CreateScheduleFragment fragment = new CreateScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showBackButton(true);
        showConfirmButton(true);
        setShowBottomBar(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null){
            if(getArguments().containsKey(getString(R.string.scheduleKey))) {
                Log.d(TAG, "Attempting to get Schedule by ID");
                int schedID = getArguments().getInt(getString(R.string.scheduleKey));
                mSchedule = Global.getInstance().getScheduleById(schedID);
                Log.d(TAG, "Schedule Stats: Num TimeBlocks: " + mSchedule.getTimeBlocks().size()
                + " RepeatWeekly: " + mSchedule.repeatWeekly() + " Num Profiles: " + mSchedule.getProfiles().size());
            }
            if(getArguments().containsKey(getString(R.string.schedule_is_new))){
                mIsNew = getArguments().getBoolean(getString(R.string.schedule_is_new));
            }
        } else {
            mSchedule = null;
        }


        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_schedule, container, false);
        mNameEditText = layout.findViewById(R.id.schedule_name_edit_text);
        mNameEditText.setText(mSchedule.getName());
        mTimeBlockList = layout.findViewById(R.id.schedule_time_block_list_view);

        mRepeatWeeklyButton = layout.findViewById(R.id.schedule_repeat_weekly_radio);
        if(mSchedule != null){
            mRepeatWeeklyButton.setChecked(mSchedule.repeatWeekly());
        }

        mTimeBlockAdapter = new TimeBlockAdapter(getContext(), mSchedule, mIsNew);
        Button footer = layout.findViewById(R.id.schedule_add_time_block);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = CreateTimeBlockFragment.newInstance();
                Bundle args = new Bundle();

                mSchedule.setName(mNameEditText.getText().toString());
                mSchedule.setRepeatWeekly(mRepeatWeeklyButton.isChecked());
                mSchedule.setProfiles(getSelectedProfiles());
                mSchedule.setTimeBlocks(mTimeBlockAdapter.getTimeBlocks());
                Global.getInstance().modifySchedule(getContext(), mSchedule);

                args.putInt(getString(R.string.scheduleKey), mSchedule.getId());
                args.putBoolean(getString(R.string.schedule_is_new), mIsNew);
                frag.setArguments(args);
                FragmentTransaction ft = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Log.d(TAG, "TIMEBLOCKADAPTER: HAS: " + mTimeBlockAdapter.getCount());
        mTimeBlockList.setAdapter(mTimeBlockAdapter);


        mProfileAdapter = new ProfileAdapter(Global.getInstance().getAllProfiles(getContext()), getContext(), mSchedule);
        mProfileRecycler = layout.findViewById(R.id.create_schedule_profile_recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mProfileRecycler.setLayoutManager(mLayoutManager);
        mProfileRecycler.setItemAnimator(new DefaultItemAnimator());
        mProfileRecycler.setAdapter(mProfileAdapter);

        Button mDeleteButton = layout.findViewById(R.id.create_schedule_delete_button);
        if (!mIsNew){
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//                    mDatabase.child("User1").child("Schedules").child(Integer.toString(mSchedule.getId())).removeValue();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.Main_Frame, MainActivity.schedulesFragment);

                    Bundle args = new Bundle();
                    args.putInt(getString(R.string.schedule_id_to_delete), mSchedule.getId());

                    MainActivity.schedulesFragment.setArguments(args);
                    ft.commit();
                }
            });
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO implement validation of input -- schedule.
                if(validate()) {
                    //Get values from fragment
                    String scheduleName = mNameEditText.getText().toString();
                    Vector<TimeBlock> timeBlocks = mTimeBlockAdapter.getTimeBlocks();
                    Boolean repeat = mRepeatWeeklyButton.isChecked();
                    Vector<Profile> checkedProfiles = mProfileAdapter.getCheckedProfiles();

                    for(int i = 0; i < checkedProfiles.size(); i++){
                        Log.d(TAG, "Adding profile \"" + checkedProfiles.get(i).getName() + "\" to schedule");
                    }

                    //Set associated values in sched
                    mSchedule.setProfiles(checkedProfiles);
                    mSchedule.setName(scheduleName);
                    mSchedule.setTimeBlocks(timeBlocks);
                    mSchedule.setRepeatWeekly(repeat);

                    //tell schedule to modify sched if it exists, and if not - add it.
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("User1").child("Schedules").child(String.valueOf(mSchedule.getId())).setValue(mSchedule);
                    mDatabase.child("User1").child("Schedules").child(String.valueOf(mSchedule.getId())).child("repeatWeekly").setValue(repeat);

                    //TODO: Implement adding to google calendar
                    Event googleEvent = new Event()
                            .setSummary("Test")
                            .setDescription("This is a test");

                    DateTime startDate = new DateTime("2017-11-21T09:00:00");
                    EventDateTime start = new EventDateTime()
                            .setDateTime(startDate)
                            .setTimeZone("America/Los_Angeles");
                    googleEvent.setStart(start);

                    DateTime endTime = new DateTime("2018-11-21T10:00:00");
                    EventDateTime end = new EventDateTime()
                            .setDate(endTime)
                            .setTimeZone("America/Los_Angeles");
                    googleEvent.setEnd(end);

                    String calendarID = "primary";
                   //googleEvent = service.events().insert(calendarID, googleEvent).execute();


                    Global.getInstance().modifySchedule(getContext(), mSchedule);
                    Global.getInstance().synchAll(getContext());
                    resetNotificationFlags();
                    getActivity().onBackPressed();
                }
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsNew) {
                    Global.getInstance().removeSchedule(getContext(), mSchedule);
                    resetNotificationFlags();
                }
                getActivity().onBackPressed();
            }
        });

        if(getArguments() != null && getArguments().get(getString(R.string.timeBlockKey)) != null){
            TimeBlock newTimeBlock = (TimeBlock) getArguments().get(getString(R.string.timeBlockKey));
            mSchedule.addTimeBlock(newTimeBlock);
        }


        if(!mIsNew){
        }


        return layout;
    }

    private boolean validate() {
        boolean hasName = !mNameEditText.getText().toString().isEmpty();
        boolean hasProfile = mProfileAdapter.getCheckedProfiles().size() > 0;
        boolean hasTimeBlocks = mTimeBlockAdapter.getTimeBlocks().size() > 0;
        boolean uniqueName = true;
        for(Schedule s : Global.getInstance().getSchedules()){
            if(s.getName().compareToIgnoreCase(mNameEditText.getText().toString()) == 0
                    && s.getId() != getArguments().getInt(getString(R.string.scheduleKey))){
                uniqueName = false;
            }
        }

        if(!hasName){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoName), Toast.LENGTH_SHORT).show();
        } else if(!hasProfile){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoProfiles), Toast.LENGTH_SHORT).show();
        } else if(!hasTimeBlocks){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoTimeBlocks), Toast.LENGTH_SHORT).show();
        } else if(!uniqueName){
            Toast.makeText(getContext(), getString(R.string.scheduleNameIsTaken), Toast.LENGTH_SHORT).show();
        }


        return hasName && hasProfile && hasTimeBlocks && uniqueName;
    }

    public void addTimeBlock(TimeBlock timeBlock) {
        mSchedule.addTimeBlock(timeBlock);
        mTimeBlockAdapter.notifyDataSetChanged();
    }

    public void resetNotificationFlags(){
        Vector<Boolean> blockingProfiles = new Vector<>();
        Vector<Schedule> schedules = Global.getInstance().getSchedules();
        for(int i=0; i<schedules.size(); i++){
            blockingProfiles.add(schedules.get(i).isBlocking());
        }
        Global.getInstance().setScheduleFlags(getContext(), blockingProfiles);
    }

    public Vector<Profile> getSelectedProfiles() {
        return mProfileAdapter.getCheckedProfiles();
    }
}