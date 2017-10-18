package proflo.focus;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Vector;

public class ModifyScheduleActivity extends AppCompatActivity {

    boolean nameModified;
    boolean someBlockChanged;

    RelativeLayout errorLayout;
    RelativeLayout doneConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_schedule);

        // get the objects modified by various methods
        errorLayout = (RelativeLayout) findViewById(R.id.schedule_error);
        doneConfirm = (RelativeLayout) findViewById(R.id.schedule_back);

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // fill the layout with possible apps
        fillLayout();

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

    void fillLayout()
    {
        Vector<Integer> days = new Vector<>();
        days.add(1);
        days.add(0);
        days.add(1);
        days.add(1);
        days.add(0);
        days.add(1);
        days.add(0);

        for(int i = 0; i < 2; i++)
        {
            createTimeBlock(8, 40, "am", "am", 10, days, 30);
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
        TextView profileName = (TextView)findViewById(R.id.profile_name);

        if(profileName.getText().toString().length() == 0 || profileName.toString().equals("Profile Name"))
        {
            displayErrorMessage("Please enter a profile name");
            return false;
        }
        else if(!someBlockChanged)
        {
            // a single app hasn't been selected, send an error
            displayErrorMessage("Please select at least one app for this profile");
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


    void createNewTimeBlock()
    {
        int startHour = 00;
        int startMinute = 00;
        int endHour = 00;
        int endMinute = 00;

        // when the time picker is first activated, make it start time
            // if this is false, then show the end time picker
        boolean startPickerActive = true;


        Vector<String> dayNames = new Vector<>();
        dayNames.add("S");
        dayNames.add("M");
        dayNames.add("T");
        dayNames.add("W");
        dayNames.add("Th");
        dayNames.add("F");
        dayNames.add("Sa");

        // get the table
        TableLayout table = (TableLayout)findViewById(R.id.timeblock_table);

        // create the framelayout that displays the info
        LinearLayout timeBlock = new LinearLayout(ModifyScheduleActivity.this);
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
        timeBox.setLayoutParams(new LinearLayout.LayoutParams(300, 80));

        // Text for time
        TextView timeText = new TextView(ModifyScheduleActivity.this);
        timeText.setText(startHour + ":" + startMinute + " - " + endHour + ":" + endMinute);

        timeBox.addView(timeText);

        timeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the edit_time block
                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);
                editTime.setVisibility(View.VISIBLE);
                editTime.addView(startTime);

                // change the text
                TextView message = (TextView)findViewById(R.id.set_time_text);
               // message.setText("Please select a start time");

                // get the button
                Button startFinish = (Button)findViewById(R.id.done_time);
                startFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout editTime = (LinearLayout) findViewById(R.id.edit_time);

                        // switch  the time picker
                        editTime.removeView(startTime);
                        editTime.addView(endTime);

                        TextView message = (TextView)findViewById(R.id.set_time_text);
                       // message.setText("Please select a end time");

                        // get the button
                        final Button endFinish = (Button)findViewById(R.id.done_time);
                        endFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);
                                editTime.setVisibility(View.GONE);
                                editTime.removeView(endTime);

                            }
                        });

                        // get the button
                        Button startCancel = (Button)findViewById(R.id.cancel_time);
                        startCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // switch  the time picker
                                LinearLayout editTime = (LinearLayout) findViewById(R.id.edit_time);
                                editTime.setVisibility(View.GONE);
                                editTime.removeView(endTime);

                            }
                        });


                    }
                });

                // get the button
                Button startCancel = (Button)findViewById(R.id.cancel_time);
                startCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // switch  the time picker
                        LinearLayout editTime = (LinearLayout)findViewById(R.id.edit_time);
                        editTime.setVisibility(View.GONE);
                        editTime.removeView(startTime);

                    }
                });

            }
        });


        timeBlock.addView(timeBox);


        for(int i = 0; i < 7; i++)
        {
            // create a checkbox
            CheckBox checkBox = new CheckBox(ModifyScheduleActivity.this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(75, 75));

            boxes.addView(checkBox);
            TextView dayTitle = new TextView(ModifyScheduleActivity.this);
            dayTitle.setText(dayNames.get(i));
            dayTitle.setLayoutParams(new LinearLayout.LayoutParams(75,75));
            titles.addView(dayTitle);



        }



        dayTable.addView(boxes);
        dayTable.addView(titles);

        timeBlock.addView(dayTable);



        // add the app layout to the table
        table.addView(timeBlock);
    }

    void createTimeBlock(int startHour, int startMinute, String startM, String stopM, int stopHour,
                         final Vector<Integer> days, int stopMinute)
    {
        Vector<String> dayNames = new Vector<>();
        dayNames.add("S");
        dayNames.add("M");
        dayNames.add("T");
        dayNames.add("W");
        dayNames.add("Th");
        dayNames.add("F");
        dayNames.add("Sa");

        // get the table
        TableLayout table = (TableLayout)findViewById(R.id.timeblock_table);

        // create the framelayout that displays the info
        LinearLayout timeBlock = new LinearLayout(ModifyScheduleActivity.this);
        timeBlock.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        timeBlock.setPadding(35,35,35,35);

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
        dayNotTitle.setLayoutParams(new LinearLayout.LayoutParams(25,75));
        titles.addView(dayNotTitle);

        Drawable[] daysIcons = new Drawable[7];



        for(int i = 0; i < 7; i++)
        {
            // create a checkbox
            ShapeDrawable checkBox = new ShapeDrawable(new OvalShape());
            LinearLayout dayBox = new LinearLayout(ModifyScheduleActivity.this);

            if(days.get(i) == 1)
            {
                checkBox.getPaint().setColor(Color.GRAY);
            }
            else
            {
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
            dayTitle.setLayoutParams(new LinearLayout.LayoutParams(75,75));
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
