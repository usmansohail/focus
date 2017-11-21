package com.proflow.focus_v2.adapters;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.models.FocusNotification;

import java.util.Vector;

/**
 * Created by forre on 10/19/2017.
 */

public class NotificationAdapter extends BaseAdapter {

    private Context mContext;
    private Vector<FocusNotification> mNotifications = new Vector<FocusNotification>();

    public NotificationAdapter(Context context, Vector<FocusNotification> notifications){
        mNotifications = notifications;
        mContext = context;

    }

    @Override
    public int getCount() {
        return Global.getInstance().getFocusNotifications(mContext).size();
    }

    @Override
    public FocusNotification getItem(int i) {
        return Global.getInstance().getFocusNotifications(mContext).get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("Notification", "Notification: " + i + " and has name: " + getItem(i).getName());
        final FocusNotification currentNote = getItem(i);

        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(currentNote.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(view == null){
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.list_item_notification, null);
        }
        ImageView iconView = view.findViewById(R.id.notification_list_icon);

        TextView titleView = view.findViewById(R.id.notification_list_title);
        TextView descrView = view.findViewById(R.id.notification_list_description);

        ImageButton deleteButton = view.findViewById(R.id.notification_list_delete);
        if(pi != null) {
            iconView.setImageDrawable(pi.applicationInfo.loadIcon(pm));
            titleView.setText(currentNote.getName());
            descrView.setText(currentNote.getDescription());
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(Global.getInstance().getUsername()).child("Notification").child(String.valueOf(currentNote.getId())).removeValue();
                Global.getInstance().removeFocusNotification(mContext, currentNote);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void clearAll() {
        for(int i = 0; i < getCount(); i ++){
            FocusNotification currentNote = getItem(0);
            Global.getInstance().removeFocusNotification(mContext, currentNote);
            notifyDataSetChanged();
        }
    }
}
