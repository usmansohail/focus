package com.proflow.focus_v2.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.fragments.ModifyProfileFragment;
import com.proflow.focus_v2.models.FocusTimer;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Forrest on 10/19/2017.
 *
 * This is the adapter that displays the profiles as a list on the ProfilesFragment
 *
 * Think of ProfileViewHolder as an arbitrary individual row within the RecyclerView
 * and, thus, change the OnBindViewHolder if you need to change information within the rows.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> mProfiles = new ArrayList<>();
    private Context mContext;
    Schedule mSchedule = null;
    Vector<Profile> checkedProfiles = new Vector<>();
    boolean mTimer = false;

    //isGlobal is basically just a delimiter to determine whether or not changes in this diplay
    //can edit the globals. If mIsGlobal is false, profileAdapter must be asked which profiles are
    //checked for it to do anything. Furthermore, it must be told which profiles are already checked.
    //Also gets rid of the edit button on the profile.
    private boolean mIsGlobal;

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton editButton;
        public SwitchCompat activeSwitch;
        public CheckBox checkBox;
        Profile thisProfile;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.profile_list_title);
            editButton = (ImageButton) itemView.findViewById(R.id.profile_list_edit_button);
            activeSwitch = (SwitchCompat) itemView.findViewById(R.id.profile_list_switch);
            checkBox = (CheckBox) itemView.findViewById(R.id.profile_list_checkbox);

            if(mSchedule != null || mTimer){
                checkBox.setVisibility(View.VISIBLE);
                activeSwitch.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
            }
        }
    }

    public ProfileAdapter(List<Profile> profiles, Context context){
        mContext = context;
        this.mProfiles = profiles;
    }


    public ProfileAdapter(List<Profile> profiles, Context context, Schedule schedule){
        mContext = context;
        this.mProfiles = profiles;
        if(schedule == null){
            mSchedule = new Schedule("New Schedule", new Vector<TimeBlock>(), false);
        }
        mSchedule = schedule;
    }

    public ProfileAdapter(List<Profile> profiles, Context context, boolean timer){
        mContext = context;
        mTimer = timer;
        mProfiles = profiles;
    }


    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile, parent, false);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, int position) {
        final Profile currentProfile = mProfiles.get(position);
        holder.thisProfile = currentProfile;
        holder.name.setText(currentProfile.getName());

        //Set the edit button to react to the current profile in the viewholder.
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity main = (MainActivity)mContext;
                ModifyProfileFragment mpf = ModifyProfileFragment.newInstance();
                Bundle b = new Bundle();
                b.putInt("ProfileIndex", holder.getAdapterPosition());
                mpf.setArguments(b);
                main.switchContent(mpf);
            }
        });

        //Set the switch to the current indefiniteActive status of the profile.
        if(mSchedule == null && !mTimer) {
            Log.d("ProfileAdapter", "Setting timer to :" +currentProfile.isActive());
            holder.activeSwitch.setChecked(currentProfile.isActive());
            holder.activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        holder.thisProfile.activate(mContext);
                    } else {
                        holder.thisProfile.deactivate(mContext);
                    }
                }
            });
        } else {
            if(mTimer){
                holder.checkBox.setChecked(false);
            } else {
                for(Profile p : mSchedule.getProfiles()){
                    if(p.getId() == currentProfile.getId()){
                        holder.checkBox.setChecked(true);
                        break;
                    }
                }
            }
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        checkedProfiles.add(holder.thisProfile);
                    } else {
                        checkedProfiles.remove(holder.thisProfile);
                    }
                }
            });
        }


    }

    public Vector<Profile> getCheckedProfiles(){
        return checkedProfiles;
    }

    private boolean getIndefiniteActive(Profile p) {
        return p.isActive();
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }
}
