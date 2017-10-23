package com.fdunlap.focus_v2.adapters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by forre on 10/20/2017.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private static final String TAG = "AppAdapter";
    private final List<PackageInfo> mApps;
    private Context mContext;
    private ArrayList<String> selectedApps = new ArrayList<>();;

    public class AppViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "AppViewHolder";

        TextView appTitle;
        ImageView appIcon;
        public AppCompatCheckBox appCheckbox;
        public PackageInfo thisPackage;

        AppViewHolder(View itemView) {
            super(itemView);
            appTitle = itemView.findViewById(R.id.app_list_title);
            appIcon = itemView.findViewById(R.id.app_list_icon);
            appCheckbox = itemView.findViewById(R.id.app_list_checkbox);
            appCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    String currentAppTitle = appTitle.getText().toString();
                    Log.d(TAG, "CheckedChanged, State: " + b);
                    if(b){
                        Log.d(TAG, "Added app: " + currentAppTitle);
                        selectedApps.add(currentAppTitle);
                    } else {
                        Log.d(TAG, "Attempting to remove: " + currentAppTitle);
                        if(selectedApps.contains(currentAppTitle)){
                            Log.d(TAG, "Removed: " + currentAppTitle);
                            selectedApps.remove(currentAppTitle);
                        }
                    }
                }
            });
        }
    }

    public AppAdapter(List<PackageInfo> apps, Context context){

        mContext = context;

        this.mApps = apps;
    }

    public AppAdapter(Profile p, List<PackageInfo> apps, Context context){

        mContext = context;

        this.mApps = apps;
        Vector<PackageInfo> selectedAppPackages = p.getApps();

        Log.d(TAG, "SelectedAppPackages isEmpty():" + selectedAppPackages.isEmpty());

        for(PackageInfo pi : selectedAppPackages){
            Log.d(TAG, "PI name: " + pi.applicationInfo.name);
            selectedApps.add(pi.applicationInfo.loadLabel(mContext.getApplicationContext().getPackageManager()).toString());
        }
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false);

        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        PackageInfo app = mApps.get(position);

        if(app.applicationInfo.name != null && holder != null && holder.appTitle != null) {
            holder.appTitle.setText(app.applicationInfo.loadLabel(mContext.getPackageManager()));
            holder.appIcon.setImageDrawable(app.applicationInfo.loadIcon(mContext.getPackageManager()));
            holder.appCheckbox.setChecked(getAppStatus(holder.appTitle.getText().toString()));
            holder.thisPackage = app;
        }
    }

    private boolean getAppStatus(String name) {
        return selectedApps.contains(name);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }
}
