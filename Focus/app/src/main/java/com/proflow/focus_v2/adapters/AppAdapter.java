package com.proflow.focus_v2.adapters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by forre on 10/20/2017.
 *
 * Think about how recycler adapters work... Also deal with the ordering. Yay.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private static final String TAG = "AppAdapter";
    private final Vector<PackageInfo> mApps;
    private Context mContext;
    private Vector<String> selectedApps = new Vector<>();

    public class AppViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "AppViewHolder";

        TextView appTitle;
        ImageView appIcon;
        public AppCompatCheckBox appCheckbox;
        public String thisPackage;

        AppViewHolder(View itemView) {
            super(itemView);

            appTitle = itemView.findViewById(R.id.app_list_title);
            appIcon = itemView.findViewById(R.id.app_list_icon);
            appCheckbox = itemView.findViewById(R.id.app_list_checkbox);
            appCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        boolean alreadyContains = false;
                        for(String s : selectedApps){
                            if(s.compareToIgnoreCase(thisPackage) == 0){
                                alreadyContains = true;
                                break;
                            }
                        }
                        if(!alreadyContains){
                            selectedApps.add(thisPackage);
                        }
                    } else {
                        for(int i = 0; i < selectedApps.size(); i++){
                            if(selectedApps.get(i).compareToIgnoreCase(thisPackage) == 0){
                                selectedApps.remove(i);
                            }
                        }
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appCheckbox.toggle();
                }
            });

        }
    }

    public AppAdapter(Vector<PackageInfo> apps, Context context){

        mContext = context.getApplicationContext();

        this.mApps = apps;
    }

    public AppAdapter(Profile p, Vector<PackageInfo> apps, Context context){

        mContext = context.getApplicationContext();

        this.mApps = apps;
        for(PackageInfo pi : apps){
            Log.e("Apps in app adapter: ", pi.packageName);
        }
        //Vector<PackageInfo> selectedAppPackages = p.getApps();
        Vector<String> selectedAppPackages = p.getApps();

        //Log.d(TAG, "SelectedAppPackages isEmpty():" + selectedAppPackages.isEmpty());

        for(String pi : selectedAppPackages){
            Log.e("Selected app: ", pi);
            if(pi != null) {
//                Log.d(TAG, "PI name: " + pi.applicationInfo.name);
                selectedApps.add(pi);
            }
        }
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false);

        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AppViewHolder holder, int position) {
        PackageInfo app = mApps.get(position);

        PackageManager pm = mContext.getApplicationContext().getPackageManager();

        if(app.applicationInfo.loadLabel(pm) != null && holder != null && holder.appTitle != null) {
            holder.thisPackage = app.packageName;
            holder.appTitle.setText(app.applicationInfo.loadLabel(pm));
            holder.appIcon.setImageDrawable(app.applicationInfo.loadIcon(pm));
            holder.appCheckbox.setChecked(getAppStatus(app.packageName));
            Log.d(TAG, app.packageName);
        }

        if(holder.appTitle.getText().toString().equals(mContext.getString(R.string.app_name))){
            Log.d(TAG, "Holder has default app name. Package: " + app.packageName);
            holder.itemView.setVisibility(View.GONE);
        }

        Log.d(TAG, "-----");
        for(String pName : selectedApps){
            Log.d(TAG, pName);
        }
        Log.d(TAG, "-----");

    }

    private boolean getAppStatus(String piName) {

        for(String pName : selectedApps){
            Log.e("SelectedApp: ", pName);
            Log.e("Checked box name: ", piName);
            if(pName.compareTo(piName) == 0){
                return true;
            }
        }
        return false;
    }

    public Vector<String> getSelectedApps() {

        Vector<String> sApps = new Vector<>();
        for(String pName : selectedApps){
            sApps.add(pName);
        }
        return sApps;
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }
}
