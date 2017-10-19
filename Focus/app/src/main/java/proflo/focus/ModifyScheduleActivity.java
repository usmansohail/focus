package proflo.focus;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Vector;

public class ModifyScheduleActivity extends AppCompatActivity {

    boolean nameModified;
    boolean someBlockChanged;

    // when the time picker is first activated, make it start time
    // if this is false, then show the end time picker
    boolean startPickerActive = true;

    int newStartHour;
    int newStartMin;
    int newStopHour;
    int newStopMin;

    Vector<TimeBlock> timeBlocks;

    RelativeLayout errorLayout;
    RelativeLayout doneConfirm;

    Vector<String> profileNames;


    // get the table
    TableLayout table;

    // create the framelayout that displays the info
    LinearLayout timeBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_schedule);

        // get the objects modified by various methods
        errorLayout = (RelativeLayout) findViewById(R.id.schedule_error);
        doneConfirm = (RelativeLayout) findViewById(R.id.schedule_back);

        table = (TableLayout)findViewById(R.id.timeblock_table);
        timeBlock = new LinearLayout(ModifyScheduleActivity.this);

        timeBlocks = new Vector<>();

        // TODO if we are modifying a schedule, then pull the list of timeblocks

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileNames = new Vector<>();

        // fill the layout with possible apps
        fillLayout();
        selectProfilesBlock();

        // assume everything has not yet been modified
        nameModified = false;
        someBlockChanged = false;

        final EditText editText = (EditText)findViewById(R.id.schedule_name);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameModified) {
                    editText.setText("");
                    editText.setTextColor(getResources().getColor(R.color.text));
                    nameModified = true;
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.done:
                //but first prompt the user for confirmation
                if(validate()) {
                    createSchedule();
                    onBackPressed();
                    return true;
                }
                return false;

            case android.R.id.home:

                if(someBlockChanged || nameModified)
                {
                    displayBackConfirmation();
                }
                else
                {
                    onBackPressed();
                }

                return true;

        }

        return false;

    }

    private void createSchedule() {

        // TODO logic for making the schedule
        EditText name = (EditText)findViewById(R.id.schedule_name);
        Global.getInstance().createSchedule(ModifyScheduleActivity.this,name.getText().toString(), timeBlocks, true);


    }

    void fillLayout()
    {
        // get all the timeblocks
        for(TimeBlock timeBlock: timeBlocks)
        {
            displayTimeBlock(timeBlock.getStartHour(), timeBlock.getStartMinute(),"", "", timeBlock.getStopHour(),timeBlock.getDays(), timeBlock.getStopMinute());


        }

        createNewTimeBlock();
    }

    void displayBackConfirmation()
    {
        // display the back message
        doneConfirm.setVisibility(View.VISIBLE);
        TextView text = (TextView)findViewById(R.id.schedule_back_text);
        text.setText("Are you sure you want to discard the current profile?");

        Button yes = (Button)findViewById(R.id.back_icon_schedule);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    void displayErrorMessage(String error)
    {
        // get the error view
        errorLayout.setVisibility(View.VISIBLE);

        // set the  text as well
        TextView text = (TextView)findViewById(R.id.profile_error_text);
        text.setText(error);

        // set the cancel on click stuff
        Button cancel = (Button)findViewById(R.id.cancel_icon);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
            }
        });

    }

    boolean validate()
    {
        // confirm that the name is not empty
        TextView scheduleName = (TextView)findViewById(R.id.schedule_name);

        if(scheduleName.getText().toString().length() == 0 || scheduleName.toString().equals("Schedule Name"))
        {
            displayErrorMessage("Please enter a Schedule name");
            return false;
        }
        else if(timeBlocks.size() == 0)
        {
            // a single app hasn't been selected, send an error
            displayErrorMessage("Please enter appropriate time information");
            return false;
        }
        else {

            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // use the setToolbar method to handle this situation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modification_toolbar, menu);

        return true;
    }


    void selectProfilesBlock()
    {

        Global global = Global.getInstance();

        // get the profiles
        Vector<Profile> profile_objects = global.getAllProfiles(ModifyScheduleActivity.this);
        final String profiles[] = new String[profile_objects.size()];

        for(int i = 0; i < profile_objects.size(); i++)
        {
            profiles[i] = profile_objects.get(i).getName();

        }


        // add the list of profiles
        final boolean [] profilesBoolean = new boolean[profiles.length];

        final AlertDialog.Builder builder = new AlertDialog.Builder(ModifyScheduleActivity.this);
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


        builder.show();


        LinearLayout profile_list= (LinearLayout)findViewById(R.id.list_of_profiles);
        Button edit = new Button(ModifyScheduleActivity.this);
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
    }

    void createNewTimeBlock()
    {
        newStartHour = 00;
        newStartMin = 00;
        newStopHour = 00;
        newStopMin = 00;

        // clear the timeblock just in case
        timeBlock.removeAllViews();


        Vector<String> dayNames = new Vector<>();
        dayNames.add("S");
        dayNames.add("M");
        dayNames.add("T");
        dayNames.add("W");
        dayNames.add("Th");
        dayNames.add("F");
        dayNames.add("Sa");


        timeBlock.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        timeBlock.setPadding(35,35,35,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        timeBlock.setBackground(border);




        // create a table for all the days
        TableLayout dayTable = new TableLayout(ModifyScheduleActivity.this);
        LinearLayout boxes = new LinearLayout(ModifyScheduleActivity.this);
        LinearLayout titles = new LinearLayout(ModifyScheduleActivity.this);

        TextView dayNotTitle = new TextView(ModifyScheduleActivity.this);
        dayNotTitle.setLayoutParams(new LinearLayout.LayoutParams(25,75));
        titles.addView(dayNotTitle);



        // add a time picker for the start
        final TimePicker startTime = new TimePicker(new ContextThemeWrapper(ModifyScheduleActivity.this, R.style.TimePickerMode));
        final TimePicker endTime = new TimePicker(ModifyScheduleActivity.this);



        startTime.setLayoutParams(new LinearLayout.LayoutParams(800, 1000));
        endTime.setLayoutParams(new LinearLayout.LayoutParams(800, 1000));

        // LinearLayout for time
        LinearLayout timeBox = new LinearLayout(ModifyScheduleActivity.this);
        LinearLayout.LayoutParams timeBoxParams = new LinearLayout.LayoutParams(800, 120);
        timeBoxParams.weight = 1.0f;
        timeBoxParams.gravity = Gravity.CENTER;
        timeBox.setPadding(10, 10, 10, 10);

        timeBox.setLayoutParams(timeBoxParams);

        LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);

        final LinearLayout messageBox = new LinearLayout(ModifyScheduleActivity.this);
        LinearLayout.LayoutParams messageBoxParams = new LinearLayout.LayoutParams(900, 80);
        messageBox.setLayoutParams(messageBoxParams);
        final TextView message = new TextView(ModifyScheduleActivity.this);
        message.setTextSize(20);
        Button done = new Button(ModifyScheduleActivity.this);
        done.setLayoutParams(new LinearLayoutCompat.LayoutParams(80, 80));
        done.setBackground(getResources().getDrawable(R.drawable.done_icon));
        Button cancel = new Button(ModifyScheduleActivity.this);
        cancel.setLayoutParams(new LinearLayoutCompat.LayoutParams(80, 80));
        cancel.setBackground(getResources().getDrawable(R.drawable.cancel_icon));
        messageBox.addView(message);
        messageBox.addView(done);
        messageBox.addView(cancel);



        // Text for time
        final TextView timeText = new TextView(ModifyScheduleActivity.this);
        timeText.setText("00:00  -  00:00");

        timeBox.addView(timeText);

        // create an edit button
        Button edit = new Button(ModifyScheduleActivity.this);
        edit.setBackground(getResources().getDrawable(R.drawable.edit_icon));
        edit.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
        timeBox.addView(edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the edit_time block
                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);
                editTime.setVisibility(View.VISIBLE);
                editTime.addView(startTime);
                message.setText("Please select a start time");
                editTime.addView(messageBox);


            }
        });

        timeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the edit_time block
                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);
                editTime.setVisibility(View.VISIBLE);
                editTime.addView(startTime);
                message.setText("Please select a start time");
                editTime.addView(messageBox);


            }
        });

        // get the button

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);

                if(startPickerActive)
                {
                    // change the time picker to end time
                    editTime.removeAllViews();
                    editTime.addView(endTime);
                    startPickerActive = false;
                    message.setText("Please select a end time");
                    editTime.addView(messageBox);



                    // update the time
                    setTime(timeText, startTime, endTime);
                }
                else
                {

                    editTime.removeAllViews();
                    editTime.setVisibility(View.GONE);

                    // if they edit the time again, go to the startPickerActive
                    startPickerActive = true;

                    setTime(timeText, startTime, endTime);
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove everything from the editTime view and make it disappear
                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);

                editTime.removeAllViews();
                editTime.setVisibility(View.GONE);
            }
        });


        startTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTime(timeText, startTime, endTime);
            }
        });


        endTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTime(timeText, startTime, endTime);
            }
        });

        timeBlock.addView(timeBox);

        final Vector<CheckBox> dayStatus = new Vector<>();

        for(int i = 0; i < 7; i++)
        {
            // create a checkbox
            CheckBox checkBox = new CheckBox(ModifyScheduleActivity.this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(75, 75));

            // add to the outer vector

            dayStatus.add(checkBox);

            boxes.addView(checkBox);
            TextView dayTitle = new TextView(ModifyScheduleActivity.this);
            dayTitle.setText(dayNames.get(i));
            dayTitle.setLayoutParams(new LinearLayout.LayoutParams(75,75));
            titles.addView(dayTitle);



        }



        dayTable.addView(boxes);
        dayTable.addView(titles);

        timeBlock.addView(dayTable);



        Button createBlock = new Button(ModifyScheduleActivity.this);
        createBlock.setLayoutParams(new LinearLayoutCompat.LayoutParams(80, 80));
        createBlock.setBackground(getResources().getDrawable(R.drawable.done_icon));
        timeBlock.addView(createBlock);

        createBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // call the validate method
                // validate();
                createTimeBlock(dayStatus);

            }
        });

        timeBlock.setBackgroundColor(getResources().getColor(R.color.soft_background));

        // add the app layout to the table
        table.addView(timeBlock);
    }

    void setTime(TextView text, TimePicker start, TimePicker stop) {
        newStartHour = start.getHour();
        newStartMin = start.getMinute();
        newStopHour = stop.getHour();
        newStopMin = stop.getMinute();

        String startH, startM, stopH, stopM, startP, stopP;

        startH = "" + newStartHour;
        stopH = "" + newStopHour;

        if(newStartHour >= 12)
        {
            startP = "PM";
        }
        else
        {
            startP = "AM";
        }
        if(newStopHour >= 12)
        {
            stopP = "PM";
        }
        else
        {
            stopP = "AM";
        }
        if(newStartHour > 12)
        {
            startH = ""  + (newStartHour - 12);
        }
        if(newStopHour > 12)
        {
            stopH = "" + (newStopHour - 12);
        }


        if(newStopMin == 0)
        {
            stopM = "00";
        }
        else {
            stopM = "" + newStopMin;
        }

        if(newStartMin == 0)
        {
            startM = "00";
        }
        else
        {
            startM = "" + newStartMin;
        }
        if (newStopMin < 10) {
            stopM = "0" + newStopMin;
        }
        if (newStartMin < 10) {
            startM = "0" + newStartMin;
        }
        text.setText(startH + ":" + startM + startP + " - " + stopH + ":" + stopM + stopP);
    }

    void createTimeBlock(Vector<CheckBox> days)
    {
        Vector<Boolean> dayBools = new Vector<>();

        for(CheckBox c: days)
        {
            if(c.isChecked())
            {
                dayBools.add(true);
            }
            else
            {
                dayBools.add(false);
            }
        }



        TimeBlock newBlock = new TimeBlock(newStartHour, newStartMin, newStopHour, newStopMin, dayBools);
        timeBlocks.add(newBlock);

        //TODO update the view
        table.removeView(timeBlock);
        fillLayout();
    }

    void displayTimeBlock(int startHour, int startMinute, String startM, String stopM, int stopHour,
                          final Vector<Boolean> days, int stopMinute) {
        Vector<String> dayNames = new Vector<>();
        dayNames.add("S");
        dayNames.add("M");
        dayNames.add("T");
        dayNames.add("W");
        dayNames.add("Th");
        dayNames.add("F");
        dayNames.add("Sa");

        // get the table
        TableLayout table = (TableLayout) findViewById(R.id.timeblock_table);

        // create the framelayout that displays the info
        LinearLayout timeBlock = new LinearLayout(ModifyScheduleActivity.this);
        timeBlock.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        timeBlock.setPadding(35, 35, 35, 35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        timeBlock.setBackground(border);

        // create  the text to add for the name
        TextView time = new TextView(ModifyScheduleActivity.this);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(250, 80);
        paramsText.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        paramsText.weight = 5;
        time.setLayoutParams(paramsText);
        time.setText(startHour + ":" + startMinute + startM + " - " + stopHour + ":" + stopMinute + stopM);
        time.setTextSize(15);


        // create a table for all the days
        TableLayout dayTable = new TableLayout(ModifyScheduleActivity.this);
        LinearLayout boxes = new LinearLayout(ModifyScheduleActivity.this);
        LinearLayout titles = new LinearLayout(ModifyScheduleActivity.this);

        TextView dayNotTitle = new TextView(ModifyScheduleActivity.this);
        dayNotTitle.setLayoutParams(new LinearLayout.LayoutParams(25, 75));
        titles.addView(dayNotTitle);

        Drawable[] daysIcons = new Drawable[7];


        for (int i = 0; i < 7; i++) {
            // create a checkbox
            ShapeDrawable checkBox = new ShapeDrawable(new OvalShape());
            LinearLayout dayBox = new LinearLayout(ModifyScheduleActivity.this);

            if (days.get(i) == true) {
                checkBox.getPaint().setColor(Color.GRAY);
            } else {
                checkBox.getPaint().setColor(Color.WHITE);
            }


            checkBox.setIntrinsicHeight(70);
            checkBox.setIntrinsicWidth(70);


            // add the checkbox to the box
            dayBox.setBackground(checkBox);
            dayBox.setLayoutParams(new LinearLayoutCompat.LayoutParams(65, 65));
            dayBox.setPadding(5, 5, 5, 5);

            boxes.addView(dayBox);

            // linear layout to padd
            LinearLayout padd = new LinearLayout(ModifyScheduleActivity.this);
            padd.setLayoutParams(new ViewGroup.LayoutParams(12, 5));
            //padd.setBackgroundColor();

            boxes.addView(padd);


            TextView dayTitle = new TextView(ModifyScheduleActivity.this);
            dayTitle.setText(dayNames.get(i));
            dayTitle.setLayoutParams(new LinearLayout.LayoutParams(75, 75));
            titles.addView(dayTitle);


        }


        dayTable.addView(boxes);
        dayTable.addView(titles);

        timeBlock.addView(time);
        timeBlock.addView(dayTable);


        // add the app layout to the table
        table.addView(timeBlock);


    }

}