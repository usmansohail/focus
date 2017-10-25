package com.proflow.focus_v2.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.adapters.TimeBlockAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

import java.util.Vector;

public class CreateScheduleFragment extends BaseFragment {

    EditText mNameEditText;
    ListView mTimeBlockList;
    RadioButton mRepeatWeeklyButton;
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
        if(getArguments() != null && getArguments().containsKey(getString(R.string.scheduleKey))){
            int schedIndex = getArguments().getInt(getString(R.string.scheduleKey));
            mSchedule = Global.getInstance().getSchedules(getContext()).get(schedIndex);
        } else {
            mSchedule = null;
        }


        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_schedule, container, false);
        mNameEditText = layout.findViewById(R.id.schedule_name_edit_text);
        if(mSchedule != null){
            mNameEditText.setText(mSchedule.getName());
        }
        mTimeBlockList = layout.findViewById(R.id.schedule_time_block_list_view);

        mRepeatWeeklyButton = layout.findViewById(R.id.schedule_repeat_weekly_radio);
        if(mSchedule != null){
            mRepeatWeeklyButton.setChecked(mSchedule.repeatWeekly());
        }
        mRepeatWeeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepeatWeeklyButton.toggle();
            }
        });

        //This is done down here so we can create a new schedule only if needed.
        //Furthermore, it prevents the ENTIRELY POSSIBLE case of someone naming a schedule "NewSchedule"
        if(mSchedule == null){
            mSchedule = new Schedule("NewSchedule", new Vector<TimeBlock>(), false);
            mIsNew = true;
        }

        mTimeBlockAdapter = new TimeBlockAdapter(getContext(), mSchedule);
        mTimeBlockList.setAdapter(mTimeBlockAdapter);
        View footer = inflater.inflate(R.layout.view_time_block_footer, container, false);
        footer.findViewById(R.id.footer_add_time_block_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, CreateTimeBlockFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mTimeBlockList.addFooterView(footer);
        mProfileAdapter = new ProfileAdapter(Global.getInstance().getAllProfiles(getContext()), getContext(), mSchedule);
        mProfileRecycler = layout.findViewById(R.id.create_schedule_profile_recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mProfileRecycler.setLayoutManager(mLayoutManager);
        mProfileRecycler.setItemAnimator(new DefaultItemAnimator());
        mProfileRecycler.setAdapter(mProfileAdapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO implement validation of input -- schedule.

                //Get values from fragment
                String scheduleName = mNameEditText.getText().toString();
                Vector<TimeBlock> timeBlocks = mTimeBlockAdapter.getTimeBlocks();
                Boolean repeat = mRepeatWeeklyButton.isSelected();
                Vector<Profile> checkedProfiles = mProfileAdapter.getCheckedProfiles();

                //Set associated values in sched
                mSchedule.setProfiles(checkedProfiles);
                mSchedule.setName(scheduleName);
                mSchedule.setTimeBlocks(timeBlocks);
                mSchedule.setRepeatWeekly(repeat);

                //tell schedule to modify sched if it exists, and if not - add it.
                if(mIsNew){
                    Global.getInstance().addSchedule(getContext(), mSchedule);
                } else {
                    Global.getInstance().modifySchedule(getContext(), mSchedule);
                }

                getActivity().onBackPressed();
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        if(getArguments() != null && getArguments().get(getString(R.string.timeBlockKey)) != null){
            TimeBlock newTimeBlock = (TimeBlock) getArguments().get(getString(R.string.timeBlockKey));
            mSchedule.addTimeBlock(newTimeBlock);
        }


        return layout;
    }

    public void addTimeBlock(TimeBlock timeBlock) {
        mSchedule.addTimeBlock(timeBlock);
        mTimeBlockAdapter.notifyDataSetChanged();
    }
}