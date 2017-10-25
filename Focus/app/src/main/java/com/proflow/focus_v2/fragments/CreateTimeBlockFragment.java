package com.proflow.focus_v2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.proflow.focus_v2.models.time;

import java.util.Vector;

/**
 * Created by forre on 10/23/2017.
 */

public class CreateTimeBlockFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CreateTimeBlockFragment";
    ImageButton mMondayButton;
    ImageButton mTuesdayButton;
    ImageButton mWednesdayButton;
    ImageButton mThursdayButton;
    ImageButton mFridayButton;
    ImageButton mSaturdayButton;
    ImageButton mSundayButton;

    Vector<ImageButton> mDayButtons = new Vector<>();

    RadioButton startAM;
    RadioButton startPM;
    RadioButton endAM;
    RadioButton endPM;

    Spinner startHour;
    Spinner startMinute;
    Spinner endHour;
    Spinner endMinute;

    ArrayAdapter<CharSequence> startHourAdapter;
    ArrayAdapter<CharSequence> startMinuteAdapter;
    ArrayAdapter<CharSequence> endHourAdapter;
    ArrayAdapter<CharSequence> endMinuteAdapter;

    int mStartHour = 12;
    int mStartMinute = 0;
    boolean mStartAM = true;

    int mEndHour = 12;
    int mEndMinute = 0;
    boolean mEndAM = true;

    RadioGroup mStartRadioGroup;
    RadioGroup mEndRadioGroup;

    Schedule mSchedule = null;

    TimeBlock.day mDay = TimeBlock.day.MONDAY;

    TimeBlock mTimeBlock = null;

    public CreateTimeBlockFragment() {
        // Required empty public constructor
    }

    public static CreateTimeBlockFragment newInstance() {
        return new CreateTimeBlockFragment();
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
            if (getArguments().containsKey(getString(R.string.timeBlockKey))) {
                //Get timeblock and initiate values from it.
            }
            if(getArguments().containsKey(getString(R.string.scheduleKey))){
                int lookingForId = getArguments().getInt(getString(R.string.scheduleKey));

                mSchedule = Global.getInstance().getScheduleById(lookingForId);
            }
            if(getArguments().containsKey(getString(R.string.timeBlockIndex))){
                mTimeBlock = mSchedule.getTimeBlocks().get(
                        getArguments().getInt(getString(R.string.timeBlockIndex))
                );
            }
        }

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_time_block, container, false);

        //Literally the next 60+ lines are instantiation of spinners and objects...

        mMondayButton = layout.findViewById(R.id.monday_button);
        mMondayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });
        mMondayButton.setSelected(true);

        mTuesdayButton = layout.findViewById(R.id.tuesday_button);
        mTuesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mWednesdayButton = layout.findViewById(R.id.wednesday_button);
        mWednesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mThursdayButton = layout.findViewById(R.id.thursday_button);
        mThursdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mFridayButton = layout.findViewById(R.id.friday_button);
        mFridayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mSaturdayButton = layout.findViewById(R.id.saturday_button);
        mSaturdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mSundayButton = layout.findViewById(R.id.sunday_button);
        mSundayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayClicked(view);
            }
        });

        mDayButtons.add(mMondayButton);
        mDayButtons.add(mTuesdayButton);
        mDayButtons.add(mWednesdayButton);
        mDayButtons.add(mThursdayButton);
        mDayButtons.add(mFridayButton);
        mDayButtons.add(mSaturdayButton);
        mDayButtons.add(mSundayButton);

        startAM = layout.findViewById(R.id.create_time_block_start_time_am);
        startAM.setChecked(true);
        startPM = layout.findViewById(R.id.create_time_block_start_time_pm);
        endAM = layout.findViewById(R.id.create_time_block_end_time_am);
        endAM.setChecked(true);
        endPM = layout.findViewById(R.id.create_time_block_end_time_pm);

        startHour = layout.findViewById(R.id.create_time_block_start_time_hour_spinner);
        startMinute  = layout.findViewById(R.id.create_time_block_start_time_minute_spinner);
        endHour  = layout.findViewById(R.id.create_time_block_end_time_hour_spinner);
        endMinute  = layout.findViewById(R.id.create_time_block_end_time_minute_spinner);

        startHourAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.hour_times,
                        android.R.layout.simple_spinner_item);
        startHourAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        startMinuteAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        startMinuteAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        endHourAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.hour_times,
                        android.R.layout.simple_spinner_item);
        endHourAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        endMinuteAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        endMinuteAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        startHour.setAdapter(startHourAdapter);
        startHour.setOnItemSelectedListener(this);

        startMinute.setAdapter(startMinuteAdapter);
        startMinute.setOnItemSelectedListener(this);

        endHour.setAdapter(endHourAdapter);
        endHour.setOnItemSelectedListener(this);

        endMinute.setAdapter(endMinuteAdapter);
        endMinute.setOnItemSelectedListener(this);

        mStartRadioGroup = layout.findViewById(R.id.time_block_start_time_radio_group);
        mEndRadioGroup = layout.findViewById(R.id.time_block_end_time_radio_group);

        if(mTimeBlock != null){
            setupValues();
        }


        startAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartAM = true;
                if(mStartHour > 11) mStartHour -= 12;
            }
        });
        startPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartAM = false;
                if(mStartHour < 12) mStartHour += 12;
            }
        });
        endAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndAM = true;
                if(mEndHour > 11) mEndHour -= 12;
            }
        });
        endPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndAM = false;
                if(mEndHour < 12) mEndHour += 12;
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement confirmation in CreateTimeBlockFragment
                if(validate()) {
                    time start = new time(mStartHour, mStartMinute);
                    time end = new time(mEndHour, mEndMinute);

                    if (mTimeBlock == null){
                    mSchedule.addTimeBlock(new TimeBlock(start, end, mDay));
                    Global.getInstance().modifySchedule(getContext(), mSchedule);
                    } else {
                        int tbIndex = getArguments().getInt(getString(R.string.timeBlockIndex));
                        mSchedule.getTimeBlocks().get(tbIndex).setStartTime(start);
                        mSchedule.getTimeBlocks().get(tbIndex).setEndTime(end);
                        mSchedule.getTimeBlocks().get(tbIndex).setDays(mDay);
                        Global.getInstance().modifySchedule(getContext(), mSchedule);
                    }
                    Bundle args = new Bundle();
                    args.putInt(getString(R.string.scheduleKey), mSchedule.getId());

                    Fragment frag = CreateScheduleFragment.newInstance();
                    frag.setArguments(args);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.Main_Frame, frag);
                    ft.commit();
                }
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO intercept backPressed and point.
                getActivity().onBackPressed();
            }
        });



        return layout;
    }

    private void setupValues() {
        mDay = mTimeBlock.getDay();
        switch(mDay){
            case MONDAY:
                mMondayButton.setSelected(true);
                mMondayButton.callOnClick();
                break;
            case TUESDAY:
                mTuesdayButton.setSelected(true);
                mTuesdayButton.callOnClick();
                break;
            case WEDNESDAY:
                mWednesdayButton.setSelected(true);
                mWednesdayButton.callOnClick();
                break;
            case THURSDAY:
                mThursdayButton.setSelected(true);
                mThursdayButton.callOnClick();
                break;
            case FRIDAY:
                mFridayButton.setSelected(true);
                mFridayButton.callOnClick();
                break;
            case SATURDAY:
                mSaturdayButton.setSelected(true);
                mSaturdayButton.callOnClick();
                break;
            case SUNDAY:
                mSundayButton.setSelected(true);
                mSundayButton.callOnClick();
                break;
        }

        time startTime = mTimeBlock.getStartTime();
        time endTime = mTimeBlock.getEndTime();

        mStartHour = startTime.hour;
        mStartMinute = startTime.minute;
        mEndHour = endTime.hour;
        mEndMinute = endTime.minute;

        startHour.setSelection(startTime.hour % 12);
        startMinute.setSelection(startTime.minute);

        if(startTime.hour > 11){
            startAM.setChecked(false);
            startPM.setChecked(true);
        }


        int endHourIndex;
        if(endTime.hour == 12){
            endHourIndex = 0;
        } else {
            endHourIndex = (endTime.hour - 12);
        }

        endHour.setSelection(endHourIndex);
        endMinute.setSelection(endTime.minute);

        if(endTime.hour > 11){
            endAM.setChecked(false);
            endPM.setChecked(true);
        }

    }

    private boolean validate() {
        time start = new time(mStartHour, mStartMinute);
        time end = new time(mEndHour, mEndMinute);

        if(!start.isBefore(end)){
            Toast.makeText(getContext(), "INVALID: start time is after end", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        if(parent.getId() == startHour.getId()){
            mStartHour = Integer.parseInt(startHour.getSelectedItem().toString());

            if(mStartHour == 12) mStartHour = 0;

            if(!mStartAM){
                mStartHour += 12;
            }
            Log.d(TAG, "Set start hour to: " + mStartHour);
        } else if(parent.getId() == startMinute.getId()){
            mStartMinute = Integer.parseInt(startMinute.getSelectedItem().toString());
            Log.d(TAG, "Set start minute to: " + mStartMinute);
        } else if(parent.getId() == endHour.getId()){
            mEndHour = Integer.parseInt(endHour.getSelectedItem().toString());

            if(mEndHour == 12) mEndHour = 0;

            if(!mEndAM){
                mEndHour += 12;
            }
            Log.d(TAG, "Set end hour to: " + mEndHour);
        } else if(parent.getId() == endMinute.getId()){
            mEndMinute = Integer.parseInt(endMinute.getSelectedItem().toString());
            Log.d(TAG, "Set end minute to: " + mEndMinute);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onDayClicked(View v){
        for(ImageButton b : mDayButtons){
            if(b.getId() != v.getId()){
                b.setBackground(getContext().getDrawable(R.drawable.circle_button));
            }
        }
        ((ImageButton)v).setBackground(getContext().getDrawable(R.drawable.circle_button_active));
        if(v.getId() == mMondayButton.getId()){
            mDay = TimeBlock.day.MONDAY;
        } else if(v.getId() == mTuesdayButton.getId()){
            mDay = TimeBlock.day.TUESDAY;
        } else if(v.getId() == mWednesdayButton.getId()){
            mDay = TimeBlock.day.WEDNESDAY;
        } else if(v.getId() == mThursdayButton.getId()){
            mDay = TimeBlock.day.THURSDAY;
        } else if(v.getId() == mFridayButton.getId()){
            mDay = TimeBlock.day.FRIDAY;
        } else if(v.getId() == mSaturdayButton.getId()){
            mDay = TimeBlock.day.SATURDAY;
        } else if(v.getId() == mSundayButton.getId()){
            mDay = TimeBlock.day.SUNDAY;
        }
    }
}
