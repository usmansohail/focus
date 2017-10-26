package com.proflow.focus_v2.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.fragments.CreateScheduleFragment;
import com.proflow.focus_v2.models.Schedule;

import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 *
 * Holy crap I never want to make an ExpandableListAdapter ever again...
 */

public class ScheduleAdapter extends BaseAdapter {

    Vector<Schedule> mSchedules;
    Context mContext;

    public ScheduleAdapter(Context context, Vector<Schedule> schedules){
        mContext = context;
        mSchedules = schedules;
    }

    @Override
    public int getCount() {
        return mSchedules.size();
    }

    @Override
    public Object getItem(int i) {
        return mSchedules.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Schedule currentSchedule = mSchedules.get(i);

        if(view == null){
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.list_item_schedule, null);
        }

        TextView name = view.findViewById(R.id.schedule_name_text_view);
        name.setText(currentSchedule.getName());

        final ImageButton showMoreButton = view.findViewById(R.id.schedule_more_button);
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity main = (MainActivity)mContext;
                        FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();

                        Bundle args = new Bundle();
                        args.putInt(mContext.getString(R.string.scheduleKey), mSchedules.get(i).getId());
                        args.putBoolean(mContext.getString(R.string.schedule_is_new), false);

                        CreateScheduleFragment frag = CreateScheduleFragment.newInstance();
                        frag.setArguments(args);

                        ft.replace(R.id.Main_Frame, frag);
                        ft.addToBackStack(null);

                        ft.commit();
                    }
                });
            }
        });

        SwitchCompat activeSwitch = view.findViewById(R.id.schedule_active_switch);

        activeSwitch.setChecked(currentSchedule.isActive());
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    currentSchedule.start(mContext);
                } else {
                    currentSchedule.stop(mContext);
                }
            }
        });

        return view;
    }
}
