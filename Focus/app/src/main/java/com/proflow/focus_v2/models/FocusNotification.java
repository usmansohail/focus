package com.proflow.focus_v2.models;

/**
 * Created by forre on 10/26/2017.
 */

public class FocusNotification{

    private String mPackageName;
    private String mName;
    private String mDescription;


    public FocusNotification(String packageName, String name, String description) {
        mPackageName = packageName;
        mName = name;
        mDescription = description;
    }


    public String getDescription(){
        return mDescription;
    }

    public String getName(){
        return mName;
    }

    public String getPackageName() {
        return mPackageName;
    }
}