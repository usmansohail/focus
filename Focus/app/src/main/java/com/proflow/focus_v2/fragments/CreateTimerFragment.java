package com.proflow.focus_v2.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class CreateTimerFragment extends BaseFragment {

    Spinner mHourSpinner;
    Spinner mMinuteSpinner;
    Spinner mSecondSpinner;

    ArrayAdapter<CharSequence> mHourSpinnerAdapter;
    ArrayAdapter<CharSequence> mMinuteSpinnerAdapter;
    ArrayAdapter<CharSequence> mSecondSpinnerAdapter;

    RecyclerView mProfileRecycler;
    ProfileAdapter mProfileAdapter;


    public CreateTimerFragment() {
        // Required empty public constructor
    }

    public static CreateTimerFragment newInstance() {
        CreateTimerFragment fragment = new CreateTimerFragment();
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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_timer, container, false);

        mHourSpinner = layout.findViewById(R.id.create_timer_hour_spinner);
        mMinuteSpinner = layout.findViewById(R.id.create_timer_minute_spinner);
        mSecondSpinner = layout.findViewById(R.id.create_timer_second_spinner);

        mHourSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.timer_hours,
                        android.R.layout.simple_spinner_item);
        mHourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mMinuteSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        mMinuteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mSecondSpinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.minute_times,
                        android.R.layout.simple_spinner_item);
        mSecondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mHourSpinner.setAdapter(mHourSpinnerAdapter);
        mMinuteSpinner.setAdapter(mMinuteSpinnerAdapter);
        mSecondSpinner.setAdapter(mSecondSpinnerAdapter);

        mProfileRecycler = layout.findViewById(R.id.create_timer_profile_recycler);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> temp = new ArrayList<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Profile profile = new Profile(id);
                    temp.add(profile);
                }

                mProfileAdapter = new ProfileAdapter(temp, getContext(), true);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                mProfileRecycler.setLayoutManager(mLayoutManager);
                mProfileRecycler.setItemAnimator(new DefaultItemAnimator());
                mProfileRecycler.setAdapter(mProfileAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    FocusTimer ft = new FocusTimer("", getDuration(), mProfileAdapter.getCheckedProfiles());
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(Global.getInstance().getUsername()).child("Timers").child(String.valueOf(ft.getId())).setValue(ft);
                    Global.getInstance().addTimer(getContext(), ft);
                    getActivity().onBackPressed();
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

    private Long getDuration() {
        return (long)
                (mHourSpinner.getSelectedItemPosition() * 3600000) +
                (mMinuteSpinner.getSelectedItemPosition() * 60000) +
                (mSecondSpinner.getSelectedItemPosition() * 1000);
    }

    private boolean validate() {

        if(mProfileAdapter.getCheckedProfiles().isEmpty()){
            Toast.makeText(getContext(), getString(R.string.timerNoSelectedProfiles), Toast.LENGTH_SHORT).show();
        } else if(!allZero()){
            Toast.makeText(getContext(), getString(R.string.timerAllValuesZero), Toast.LENGTH_SHORT).show();
        }

        return (!mProfileAdapter.getCheckedProfiles().isEmpty() && allZero());
    }

    private boolean allZero() {
        return getDuration() >= 10*1000;
    }

    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(!prefs.contains(getString(R.string.firstCreateTimer))){
            //Then this is the first run of Profiles fragment. run through profile tutorial.
            runProfileTutorial();
            prefs.edit().putBoolean(getString(R.string.firstCreateTimer), false).apply();
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
                        .setDescription("Set the time to block here, and select the profiles to " +
                                "block below.")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(mMinuteSpinner);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setDescription("When you're finished, tap here.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
                        .setBackgroundColor(Color.parseColor("#333333"))
                )
                .playLater(confirmButton);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2)
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
