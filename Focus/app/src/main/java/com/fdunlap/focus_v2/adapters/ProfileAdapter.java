package com.fdunlap.focus_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.activities.MainActivity;
import com.fdunlap.focus_v2.fragments.ModifyProfileFragment;
import com.fdunlap.focus_v2.models.Profile;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton editButton;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.profile_list_title);
            editButton = (ImageButton) itemView.findViewById(R.id.profile_list_edit_button);
        }
    }

    public ProfileAdapter(List<Profile> profiles, Context context){
        mContext = context;
        this.mProfiles = profiles;
    }


    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile, parent, false);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, final int position) {
        final Profile currentProfile = mProfiles.get(position);
        holder.name.setText(currentProfile.getName());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity main = (MainActivity)mContext;
                ModifyProfileFragment mpf = ModifyProfileFragment.newInstance();
                Bundle b = new Bundle();
                b.putInt("ProfileIndex", position);
                mpf.setArguments(b);
                main.switchContent(mpf);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }
}
