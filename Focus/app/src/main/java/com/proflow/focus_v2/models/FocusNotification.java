package com.proflow.focus_v2.models;

import com.google.firebase.database.DataSnapshot;
import com.proflow.focus_v2.data.Global;

/**
 * Created by forre on 10/26/2017.
 */

public class FocusNotification{

    private String mPackageName;
    private String mName;
    private String mDescription;
    private int mId;

    public FocusNotification(String packageName, String name, String description) {
        mPackageName = packageName;
        mName = name;
        mDescription = description;
        setId(Global.getInstance().getUniqueNotificationId());
    }

    public FocusNotification(DataSnapshot snapshot){
        mPackageName = snapshot.child("packagename").getValue(String.class);
        mName = snapshot.child("name").getValue(String.class);
        mDescription = snapshot.child("description").getValue(String.class);
        mId = snapshot.child("id").getValue(Integer.class);
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

    public void setId(int id){
        mId = id;
    }

    public int getId(){ return mId; }
}