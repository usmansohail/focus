package com.proflow.focus_v2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 */

public class TimerAdapter extends BaseAdapter {

    Context mContext;
    Vector<FocusTimer> mTimers;

    public TimerAdapter(Context context, Vector<FocusTimer> timers){
        mContext = context;
        Timer timer = new Timer(true);
        this.mTimers = timers;
        //Forces the text views to update. Woo.
    }

    @Override
    public int getCount() {
        return Global.getInstance().getTimers(mContext).size();
    }

    @Override
    public Object getItem(int i) {
        return Global.getInstance().getTimers(mContext).get(i);
    }

    @Override
    public long getItemId(int i) {
        //TODO update this for our friends in the cloud.
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("Global size", "" + getCount());
        Log.e("Global i value", "" + i);
        Log.e("mTimers size", "" + mTimers.size());
        Log.e("mTimers i value", "" + i);
        final FocusTimer currentTimer = (FocusTimer) getItem(i);

        if(view == null){
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.list_item_timer, null);
        }

        TextView timeRemainingTextView = view.findViewById(R.id.timer_list_time);
        timeRemainingTextView.setText(currentTimer.getRemainingTimeString());

        final ImageButton playPauseButton = view.findViewById(R.id.timer_list_play_pause);


        if(currentTimer.isPaused()){
            playPauseButton.setBackground(mContext.getDrawable(R.drawable.ic_play_dark));
        } else {
            playPauseButton.setBackground(mContext.getDrawable(R.drawable.ic_pause_dark));
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimer.togglePause();

                if(currentTimer.isPaused()){
                    playPauseButton.setBackground(mContext.getDrawable(R.drawable.ic_play_dark));
                } else {
                    playPauseButton.setBackground(mContext.getDrawable(R.drawable.ic_pause_dark));
                }
            }
        });

        final ImageButton deleteButton = view.findViewById(R.id.timer_list_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("User1").child("Timer").child(String.valueOf(currentTimer.getId())).removeValue();
                Global.getInstance().removeTimer(mContext, currentTimer);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
