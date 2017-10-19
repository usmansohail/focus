package com.fdunlap.focus_v2.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fdunlap.focus_v2.R;
import com.fdunlap.focus_v2.activities.MainActivity;
import com.roughike.bottombar.BottomBar;

public abstract class BaseFragment extends android.support.v4.app.Fragment {

    //Activity Toolbar
    Toolbar toolbar;

    //Toolbar Icons
    ImageButton addItemButton;
    ImageButton navBackButton;
    ImageButton clearNotificationsButton;
    ImageButton largerMenuButton;
    ImageButton confirmButton;

    //Activity BottomBar
    BottomBar bottomBar;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        MainActivity callback;

        super.onCreate(savedInstanceState);
        try{
            callback = (MainActivity) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must be MainActivity to extend BaseFragment");
        }

        //Toolbars themselves
        toolbar = (Toolbar) callback.findViewById(R.id.toolbar);
        bottomBar = (BottomBar) callback.findViewById(R.id.bottomBar);

        //Toolbar Icons
        addItemButton = (ImageButton) callback.findViewById(R.id.toolbar_add_item);
        navBackButton = (ImageButton) callback.findViewById(R.id.toolbar_nav_back_arrow);
        clearNotificationsButton = (ImageButton) callback.findViewById(R.id.toolbar_clear_notifications);
        largerMenuButton = (ImageButton) callback.findViewById(R.id.toolbar_more_options);
        confirmButton = (ImageButton) callback.findViewById(R.id.toolbar_confirm);

        clearToolbarIcons();

        //Because default
        setShowBottomBar(true);


        setupToolbar();
    }

    //Helper function used in each class to setup toolbar. All it does is show or hide icons
    abstract void setupToolbar();

    public void clearToolbarIcons(){
        addItemButton.setVisibility(View.GONE);
        navBackButton.setVisibility(View.GONE);
        clearNotificationsButton.setVisibility(View.GONE);
        largerMenuButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.GONE);
    }

    public void showBackButton(boolean show){
        if(show){
            navBackButton.setVisibility(View.VISIBLE);
        } else {
            navBackButton.setVisibility(View.GONE);
        }
    }

    public void showAddButton(boolean show){
        if(show){
            addItemButton.setVisibility(View.VISIBLE);
        } else {
            addItemButton.setVisibility(View.GONE);
        }
    }

    public void showClearButton(boolean show){
        if(show){
            clearNotificationsButton.setVisibility(View.VISIBLE);
        } else {
            clearNotificationsButton.setVisibility(View.GONE);
        }
    }

    public void showMenuButton(boolean show){
        if(show){
            largerMenuButton.setVisibility(View.VISIBLE);
        } else {
            largerMenuButton.setVisibility(View.GONE);
        }
    }

    public void showConfirmButton(boolean show){
        if(show){
            confirmButton.setVisibility(View.VISIBLE);
        } else {
            confirmButton.setVisibility(View.GONE);
        }
    }

    public void resetToolbar(){
        clearToolbarIcons();
        setShowBottomBar(true);
        setupToolbar();
    }

    public void setShowBottomBar(boolean show){
        if(show){
            bottomBar.setVisibility(View.VISIBLE);
        } else {
            bottomBar.setVisibility(View.GONE);
        }
    }

}
