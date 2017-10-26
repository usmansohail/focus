package com.proflow.focus_v2.adapters;

import android.app.Notification;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.proflow.focus_v2.data.Global;

/**
 * Created by forre on 10/19/2017.
 */

public class NotificationAdapter extends BaseAdapter {

    private Context mContext;

    public NotificationAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return Global.getInstance().getNotifications(mContext).size();
    }

    @Override
    public Notification getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
