package com.proflow.focus_v2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.models.Profile;
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

    TimeBlock.day mDay = TimeBlock.day.MONDAY;

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
        startAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartAM = true;
                if(mStartHour > 11) mStartHour -= 12;
            }
        });
        startPM = layout.findViewById(R.id.create_time_block_start_time_pm);
        startPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartAM = false;
                if(mStartHour < 12) mStartHour += 12;
            }
        });
        endAM = layout.findViewById(R.id.create_time_block_end_time_am);
        endAM.setChecked(true);
        endAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndAM = true;
                if(mEndHour > 11) mEndHour -= 12;
            }
        });
        endPM = layout.findViewById(R.id.create_time_block_end_time_pm);
        endPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndAM = false;
                if(mEndHour < 12) mEndHour += 12;
            }
        });

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

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement confirmation in CreateTimeBlockFragment
                if(validate()){
                    
                }
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return layout;
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
