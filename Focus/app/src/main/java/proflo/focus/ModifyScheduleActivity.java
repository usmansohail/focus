package proflo.focus;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        for(int i = 0; i < 10; i++)
        {
            Vector<Integer> days = new Vector<>();
            days.add(1);
            days.add(0);
            days.add(1);
            days.add(1);
            days.add(0);
            days.add(1);
            days.add(0);
            createTimeBlock(8, 40, "am", "am", 10, days, 30);
        }
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

        for(int i = 0; i < 7; i++)
        {
            // create a checkbox
            CheckBox checkBox = new CheckBox(ModifyScheduleActivity.this);

            if(days.get(i) == 1)
            {
                checkBox.setChecked(true);
            }
            else
            {
                checkBox.setChecked(false);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // set it to its original value

                    buttonView.setChecked(!isChecked);

                }
            });

            LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(75, 75);
            checkBox.setLayoutParams(boxParams);
            boxes.addView(checkBox);
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
