package proflo.focus;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnClickListener;

import java.util.Vector;

public class ModifyTimerActivity extends AppCompatActivity {

    boolean somethingChanged;

    RelativeLayout errorLayout;
    RelativeLayout doneConfirm;

    Vector<String> profileNames;
    int numHours;
    int numMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_timer);

        // get the objects we will need throughout
        errorLayout =  (RelativeLayout)findViewById(R.id.profile_error);
        doneConfirm = (RelativeLayout)findViewById(R.id.profile_back);

        profileNames = new Vector<>();

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // add the list of profiles
        final String profiles[] = new String[] {"red", "green", "blue", "black"};     // change this line
        final boolean [] profilesBoolean = new boolean[profiles.length];

        final AlertDialog.Builder builder = new AlertDialog.Builder(ModifyTimerActivity.this);
        DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                profilesBoolean[which] = isChecked;
            }
        };


        builder.setMultiChoiceItems(profiles, profilesBoolean, listener);
        builder.setTitle("Select all Profiles");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){


            @Override
            public void onClick(DialogInterface dialog, int which) {
                // fill the correct textView with the list of profiles
                TextView list = (TextView)findViewById(R.id.list_text);
                String listText = "";
                for(int i = 0; i < profiles.length; i++)
                {
                    if(profilesBoolean[i])
                    {
                        listText = listText + profiles[i] + '\n';
                        profileNames.add(profiles[i]);
                    }
                    else
                    {
                        for(int j = 0; j < profileNames.size(); j++)
                        {
                            if(profileNames.get(j).toString().equalsIgnoreCase(profiles[i].toString()))
                            {
                                profileNames.remove(j);
                            }
                        }
                    }

                }

                list.setText(listText);
            }
        });

        somethingChanged = false;
        builder.show();


        LinearLayout profile_list= (LinearLayout)findViewById(R.id.list_of_profiles);
        Button edit = new Button(ModifyTimerActivity.this);
        edit.setBackground(getResources().getDrawable(R.drawable.edit_icon));
        edit.setLayoutParams(new LinearLayoutCompat.LayoutParams(80, 80));
        profile_list.addView(edit);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        profile_list.setBackground(border);
        profile_list.setPadding(10,10,10,10);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });


        // setup the time pickers
        NumberPicker hourPicker = (NumberPicker) findViewById(R.id.num_hours);
        NumberPicker minPicker = (NumberPicker)findViewById(R.id.num_min);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);
        minPicker.setMinValue(0);
        minPicker.setMaxValue(60);

        hourPicker.setEnabled(true);
        minPicker.setEnabled(true);
        hourPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.isInEditMode();
            }
        }); minPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.isInEditMode();
            }
        });

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numHours = newVal;
            }
        });

        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numMinutes = newVal;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // use the setToolbar method to handle this situation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modification_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.done:
                //but first prompt the user for confirmation
                if(validate()) {

                    // actually create the timer
                    createTimer();

                    onBackPressed();
                    return true;
                }
                return false;

            case android.R.id.home:

                if(somethingChanged)
                {
                    displayDoneConfirmation();
                }
                else
                {
                    onBackPressed();
                }

                return true;

        }

        return false;

    }

    boolean validate()
    {
        return true;
    }


    void createTimer()
    {
        // numHours and numMin have the number of hours we want


        // get list of profiles
        // profileNames has this

    }

    void displayDoneConfirmation()
    {
        // display the back message
        doneConfirm.setVisibility(View.VISIBLE);
        TextView text = (TextView)findViewById(R.id.profile_back_text);
        text.setText("Are you sure you want to discard the current profile?");

        Button yes = (Button)findViewById(R.id.back_icon_profile);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void displayErrorMessage(String errorMessage)
    {
        // get the error view
        errorLayout.setVisibility(View.VISIBLE);

        // set the  text as well
        TextView text = (TextView)findViewById(R.id.profile_error_text);
        text.setText(errorMessage);

        // set the cancel on click stuff
        Button cancel = (Button)findViewById(R.id.cancel_icon);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
            }
        });


    }


}
