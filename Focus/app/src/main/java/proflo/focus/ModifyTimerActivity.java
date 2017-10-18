package proflo.focus;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ModifyTimerActivity extends AppCompatActivity {

    boolean somethingChanged;

    RelativeLayout errorLayout;
    RelativeLayout doneConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_timer);

        // get the objects we will need throughout
        errorLayout =  (RelativeLayout)findViewById(R.id.profile_error);
        doneConfirm = (RelativeLayout)findViewById(R.id.profile_back);

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // add the list of profiles
        CharSequence profiles[] = new CharSequence[] {"red", "green", "blue", "black"};     // change this line

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select all Profiles");
        builder.setItems(profiles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
            }
        });
        builder.show();

        somethingChanged = false;


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
        //  get the number of hours
        NumberPicker hours = (NumberPicker)findViewById(R.id.num_hours);
        NumberPicker min = (NumberPicker)findViewById(R.id.num_min);

        int numHours = hours.getValue();
        int numMin = min.getValue();

        // get list of profiles


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
