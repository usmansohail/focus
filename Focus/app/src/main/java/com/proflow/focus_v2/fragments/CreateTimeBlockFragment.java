package com.proflow.focus_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

import java.util.ArrayList;
import java.util.Vector;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

import static com.proflow.focus_v2.models.TimeBlock.day.FRIDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.MONDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.SATURDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.SUNDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.THURSDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.TUESDAY;
import static com.proflow.focus_v2.models.TimeBlock.day.WEDNESDAY;

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

    boolean afterSetup = false;

    RadioGroup mStartRadioGroup;
    RadioGroup mEndRadioGroup;

    boolean mIsNew = false;

    Schedule mSchedule = null;

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
            if(getArguments().containsKey(getString(R.string.scheduleKey))){
                int lookingForId = getArguments().getInt(getString(R.string.scheduleKey));

                mSchedule = Global.getInstance().getScheduleById(lookingForId, getContext());
            }
            if(getArguments().containsKey(getString(R.string.timeBlockIndex))){
                mTimeBlock = mSchedule.getTimeBlocks().get(
                        getArguments().getInt(getString(R.string.timeBlockIndex))
                );
            }
            if(getArguments().containsKey(getString(R.string.schedule_is_new))){
                mIsNew = getArguments().getBoolean(getString(R.string.schedule_is_new));
            }
        }

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_time_block, container, false);

        //Literally the next 60+ lines are instantiation of spinners and objects...


        //Should have set these in XML... Oops....
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

        startMinute.setAdapter(startMinuteAdapter);

        endHour.setAdapter(endHourAdapter);

        endMinute.setAdapter(endMinuteAdapter);

        mStartRadioGroup = layout.findViewById(R.id.time_block_start_time_radio_group);
        mEndRadioGroup = layout.findViewById(R.id.time_block_end_time_radio_group);

        if(mTimeBlock == null){
            mTimeBlock = new TimeBlock(new time(0,0), new time(0,0), new Vector<TimeBlock.day>());
        }
        setupValues();
        afterSetup = true;

        startAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimeBlock.getStartTime().hour > 11) mTimeBlock.getStartTime().hour -= 12;
            }
        });
        startPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimeBlock.getStartTime().hour < 12) mTimeBlock.getStartTime().hour += 12;
            }
        });
        endAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimeBlock.getEndTime().hour > 11) mTimeBlock.getEndTime().hour -= 12;
            }
        });
        endPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimeBlock.getEndTime().hour < 12) mTimeBlock.getEndTime().hour += 12;
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement confirmation in CreateTimeBlockFragment
                if(validate()) {

                    if(!getArguments().containsKey(getString(R.string.timeBlockIndex))){
                        mSchedule.addTimeBlock(mTimeBlock);
                    } else {
                        int tbIndex = getArguments().getInt(getString(R.string.timeBlockIndex), 0);
                        mSchedule.getTimeBlocks().get(tbIndex).setStartTime(mTimeBlock.getStartTime());
                        mSchedule.getTimeBlocks().get(tbIndex).setEndTime(mTimeBlock.getEndTime());
                        mSchedule.getTimeBlocks().get(tbIndex).setDays(mTimeBlock.getDays());
                    }

                    Global.getInstance().modifySchedule(getContext(), mSchedule);

                    Bundle args = new Bundle();
                    args.putInt(getString(R.string.scheduleKey), mSchedule.getId());
                    args.putBoolean(getString(R.string.schedule_is_new), mIsNew);

                    Fragment frag = CreateScheduleFragment.newInstance();
                    frag.setArguments(args);

                    getActivity().getSupportFragmentManager().popBackStack();

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.Main_Frame, frag);
                    ft.commit();
                }
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(getString(R.string.scheduleKey), mSchedule.getId());
                args.putBoolean(getString(R.string.schedule_is_new), mIsNew);

                Fragment frag = CreateScheduleFragment.newInstance();
                frag.setArguments(args);

                getActivity().getSupportFragmentManager().popBackStack();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.commit();
            }
        });



        return layout;
    }

    private void setupValues() {
        //Then, if the time block has the day, select it.
        if(mTimeBlock.hasDay(MONDAY)) {
            setButton(mMondayButton, true);
        }
        if(mTimeBlock.hasDay(TUESDAY)) {
            setButton(mTuesdayButton, true);
        }
        if(mTimeBlock.hasDay(WEDNESDAY)){
            setButton(mWednesdayButton, true);
        }
        if(mTimeBlock.hasDay(THURSDAY)){
            setButton(mThursdayButton, true);
        }
        if(mTimeBlock.hasDay(FRIDAY)){
            setButton(mFridayButton, true);
        }
        if(mTimeBlock.hasDay(SATURDAY)){
            setButton(mSaturdayButton, true);
        }
        if(mTimeBlock.hasDay(SUNDAY)){
            setButton(mSundayButton, true);
        }


        time startTime = mTimeBlock.getStartTime();
        time endTime = mTimeBlock.getEndTime();

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
            endHourIndex = endTime.hour % 12;
        }

        endHour.setSelection(endHourIndex);
        endMinute.setSelection(endTime.minute);

        if(endTime.hour > 11){
            endAM.setChecked(false);
            endPM.setChecked(true);
        }


        startHour.setOnItemSelectedListener(this);
        startMinute.setOnItemSelectedListener(this);
        endHour.setOnItemSelectedListener(this);
        endMinute.setOnItemSelectedListener(this);

    }

    private boolean validate() {

        time startTime = parseStartTime();
        time endTime = parseEndTime();



        mTimeBlock.setStartTime(startTime);
        mTimeBlock.setEndTime(endTime);

        if(!startTime.isBefore(endTime)){
            Toast.makeText(getContext(), "INVALID: start time is after end", Toast.LENGTH_SHORT).show();
        }
        return startTime.isBefore(endTime) && !mTimeBlock.getDays().isEmpty();
    }

    private time parseStartTime() {
        int sHour = startHour.getSelectedItemPosition();
        if(startPM.isChecked()){
            sHour += 12;
        }
        int sMinute = startMinute.getSelectedItemPosition();
        return new time(sHour, sMinute);
    }

    private time parseEndTime() {
        int eHour = endHour.getSelectedItemPosition();
        if(endPM.isChecked()){
            eHour += 12;
        }
        int eMinute = endMinute.getSelectedItemPosition();
        return new time(eHour, eMinute);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        if(afterSetup) {
            if (parent.getId() == startHour.getId()) {
                int start = startHour.getSelectedItemPosition();

                if (startPM.isChecked()) {
                    start += 12;
                }

                mTimeBlock.getStartTime().hour = start;
                Log.d(TAG, "Set start hour to: " + start);
            } else if (parent.getId() == startMinute.getId()) {
                mTimeBlock.getStartTime().minute = startMinute.getSelectedItemPosition();
                Log.d(TAG, "Set start minute to: " + mTimeBlock.getStartTime().minute);
            } else if (parent.getId() == endHour.getId()) {
                int end = endHour.getSelectedItemPosition();

                if (endPM.isChecked()) {
                    end += 12;
                }

                mTimeBlock.setEndTime(new time(end, mTimeBlock.getEndTime().hour));
                Log.d(TAG, "Set end hour to: " + end);
            } else if (parent.getId() == endMinute.getId()) {
                mTimeBlock.getEndTime().minute = endMinute.getSelectedItemPosition();
                Log.d(TAG, "Set end minute to: " + mTimeBlock.getEndTime().minute);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onDayClicked(View v){
        final int vId = v.getId();
        TimeBlock.day day;

        if(vId == mMondayButton.getId()){
            day = MONDAY;
        } else if(vId == mTuesdayButton.getId()){
            day = TUESDAY;
        } else if(vId == mWednesdayButton.getId()){
            day = WEDNESDAY;
        } else if(vId == mThursdayButton.getId()){
            day = THURSDAY;
        } else if(vId == mFridayButton.getId()){
            day = FRIDAY;
        } else if(vId == mSaturdayButton.getId()){
            day = SATURDAY;
        } else {
            day = SUNDAY;
        }

        if(mTimeBlock.hasDay(day)){
            mTimeBlock.removeDay(day);
            setButton((ImageButton)v, false);
        } else {
            mTimeBlock.addDay(day);
            setButton((ImageButton) v, true);
        }
    }

    private void setButton(ImageButton v, boolean active) {
        if(active){
            v.setBackground(getContext().getDrawable(R.drawable.circle_button_active));
        } else {
            v.setBackground(getContext().getDrawable(R.drawable.circle_button));
        }
    }

    public int getScheduleId(){
        return mSchedule.getId();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(!prefs.contains(getString(R.string.firstCreateTimeBlockKey))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstCreateTimeBlockKey), false).apply();
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
                        .setDescription("Toggle these dates to select the days on which this timeblock should be active.")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mThursdayButton);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("Set the start time of the timeblock here...")
                        .setGravity(Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mStartRadioGroup);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("...and the end time here.")
                        .setGravity(Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mEndRadioGroup);

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
