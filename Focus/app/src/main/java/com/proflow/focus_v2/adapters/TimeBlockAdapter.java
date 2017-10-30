package com.proflow.focus_v2.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.fragments.CreateScheduleFragment;
import com.proflow.focus_v2.fragments.CreateTimeBlockFragment;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

import java.util.Vector;
import java.util.zip.Inflater;

/**
 * Created by forre on 10/23/2017.
 */

public class TimeBlockAdapter extends BaseAdapter {

    private static final String TAG = "TimeBlockAdapter";

    Context mContext;
    Schedule mSchedule;

    public TimeBlockAdapter(Context context, Schedule schedule){
        mContext = context;
        mSchedule = schedule;
        Log.d(TAG, "STATS: NumTimeBlocks: " + mSchedule.getTimeBlocks().size());
    }

    @Override
    public int getCount() {
        return mSchedule.getTimeBlocks().size();
    }

    @Override
    public Object getItem(int i) {
        return mSchedule.getTimeBlocks().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final TimeBlock timeBlock = mSchedule.getTimeBlocks().get(i);

        Log.d(TAG, "TimeBlock " + i +
                " stats: StartTime: " + timeBlock.getStartTime().hour + ":" + timeBlock.getStartTime().minute
                + " EndTime: " + timeBlock.getEndTime().hour + ":" + timeBlock.getEndTime().minute);

        if(view == null){
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.list_item_schedule_info, null);
        }

        TextView mStartTimeTextView = view.findViewById(R.id.schedule_info_start_text_view);
        TextView mEndTimeTextView = view.findViewById(R.id.schedule_info_end_text_view);
        ImageButton mEditButton = view.findViewById(R.id.schedule_info_edit_button);
        ImageButton mDeleteButton = view.findViewById(R.id.schedule_info_delete_button);

        mStartTimeTextView.setText(timeBlock.getStartString());
        mEndTimeTextView.setText(timeBlock.getEndString());

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = CreateTimeBlockFragment.newInstance();
                Bundle args = new Bundle();
                Global.getInstance().modifySchedule(mContext, mSchedule);

                args.putInt(mContext.getString(R.string.scheduleKey), mSchedule.getId());
                args.putInt(mContext.getString(R.string.timeBlockIndex), i);
                frag.setArguments(args);
                FragmentTransaction ft = ((MainActivity)mContext).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSchedule.removeTimeBlock(timeBlock);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public Vector<TimeBlock> getTimeBlocks(){
        return mSchedule.getTimeBlocks();
    }
}
