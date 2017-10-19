package proflo.focus;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Vector;

public class ModifyProfileActivity extends AppCompatActivity {

    private static final String TAG = "ModifyProfileActivity";
    boolean nameModified;
    boolean someAppChecked;

    RelativeLayout errorLayout;
    RelativeLayout doneConfirm;

    Vector<ApplicationInfo> blocked = new Vector<ApplicationInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        // get the objects we will need throughout
        errorLayout =  (RelativeLayout)findViewById(R.id.profile_error);
        doneConfirm = (RelativeLayout)findViewById(R.id.profile_back);

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // fill the layout with possible apps
        fillLayout();

        // assume everything has not yet been modified
        nameModified = false;
        someAppChecked = false;

        final EditText editText = (EditText)findViewById(R.id.profile_name);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        // use the setToolbar method to handle this situation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modification_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        Log.d(TAG, "Entering onOptionsItemSelected.");
        switch (item.getItemId())
        {
            case R.id.done:
                //but first prompt the user for confirmation
                Log.d(TAG, "Entering validate.");
                if(validate()) {
                    TableLayout table = (TableLayout)findViewById(R.id.app_table);
                    Log.d(TAG, "Table has: " + table.getChildCount() + " children.");
                    for(int i = 0; i < table.getChildCount(); i++){
                        View view = table.getChildAt(i);
                        if(view instanceof LinearLayout){
                            LinearLayout app = (LinearLayout) table.getChildAt(i);
                            for(int j = 0; j < app.getChildCount(); j++) {
                                View element = app.getChildAt(j);
                                if (element instanceof CheckBox) {
                                    CheckBox checkBox = (CheckBox) element;
                                    if (checkBox.isChecked()) {
                                        Log.d(TAG, "Adding " + Global.getInstance().getAllApps(this).get(i).packageName + " to blocked list.");
                                        blocked.add(Global.getInstance().getAllApps(this).get(i));
                                    }
                                }
                            }
                        }
                    }

                    Vector<ApplicationInfo> temp = Global.getInstance().getActiveApps(this);
                    for(int i = 0; i < blocked.size(); i++){
                        Log.d(TAG, "Adding " + blocked.get(i).packageName + " to temp.");
                        temp.add(blocked.get(i));
                    }
                    Global.getInstance().setActiveApps(this, temp);

                    TextView profileName = (TextView)findViewById(R.id.profile_name);
                    Log.d(TAG, "Profile name set to be: " + profileName.getText().toString());
                    Profile profile = new Profile(profileName.getText().toString(), blocked, false);
                    Global.getInstance().addProfile(this, profile);
                    onBackPressed();
                    return true;
                }
                return false;

            case android.R.id.home:

                if(someAppChecked || nameModified)
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
        // confirm that the name is not empty
        TextView profileName = (TextView)findViewById(R.id.profile_name);

        if(profileName.getText().toString().length() == 0 || profileName.toString().equals("Profile Name"))
        {
            Log.d(TAG, "Enter Display Name message.");
            displayErrorMessage("Please enter a profile name");
            return false;
        }
        else if(!someAppChecked)
        {
            // a single app hasn't been selected, send an error
            Log.d(TAG, "No apps are checked.");
            displayErrorMessage("Please select at least one app for this profile");
            return false;
        }
        else {

            Log.d(TAG, "Successful validation.");
            return true;
        }
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



    void fillLayout()
    {
        // logic to fill out all the apps here

        // some tests
        Vector<ApplicationInfo> apps = Global.getInstance().getAllApps(this);

        for(int i = 0; i < apps.size(); i++)
        {
            createAppRow(apps.get(i), 0);
        }

    }

    void createAppRow(ApplicationInfo appInfo, int appIndex)
    {
        // get the table
        TableLayout table = (TableLayout)findViewById(R.id.app_table);

        // create the framelayout that displays the info
        LinearLayout app = new LinearLayout(ModifyProfileActivity.this);
        app.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        app.setPadding(35,35,35,35);

        // add the border
        GradientDrawable border = new GradientDrawable();
        border.setColor(getResources().getColor(R.color.colorSecondary));
        border.setStroke(3, getResources().getColor(R.color.border));
        app.setBackground(border);

        // create  the text to add for the name
        ImageView appIconView = new ImageView(ModifyProfileActivity.this);
        LinearLayout.LayoutParams paramView = new LinearLayout.LayoutParams(10, 80);
        paramView.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        paramView.weight = 6;
        appIconView.setLayoutParams(paramView);
        appIconView.setImageDrawable(appInfo.loadIcon(getPackageManager()));

        // create  the text to add for the name
        TextView appNameView = new TextView(ModifyProfileActivity.this);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(250, 80);
        paramsText.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        paramsText.weight = 5;
        appNameView.setLayoutParams(paramsText);
        appNameView.setText(appInfo.loadLabel(getPackageManager()).toString());
        appNameView.setTextSize(20);

        // create a checkbox
        CheckBox checkBox = new CheckBox(ModifyProfileActivity.this);
        checkBox.setChecked(false);

        // set a listener
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // logic of storing this data
                someAppChecked = true;

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
        params.weight = 1;
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        checkBox.setLayoutParams(params);

        // add everything to the linearLayout
        app.addView(appIconView);
        app.addView(appNameView);
        app.addView(checkBox);

        // add the app layout to the table
        table.addView(app);

    }


}
