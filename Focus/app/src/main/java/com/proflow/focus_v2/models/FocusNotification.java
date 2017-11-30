package com.proflow.focus_v2.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.data.Global;

/**
 * Created by forre on 10/26/2017.
 */

public class FocusNotification{

    private String mPackageName;
    private String mName;
    private String mDescription;
    private int id;

    public FocusNotification(String packageName, String name, String description) {
        mPackageName = packageName;
        mName = name;
        mDescription = description;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Timers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int uniqueId = -1;
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    uniqueId = id.child("id").getValue(Integer.class);
                }
                uniqueId++;
                id = uniqueId;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //setId(Global.getInstance().getUniqueNotificationId());
    }

    public FocusNotification(DataSnapshot snapshot){
        mPackageName = snapshot.child("packagename").getValue(String.class);
        mName = snapshot.child("name").getValue(String.class);
        mDescription = snapshot.child("description").getValue(String.class);
        id = snapshot.child("id").getValue(Integer.class);
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
        this.id = id;
    }

    public int getId(){ return id; }
}