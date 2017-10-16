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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class ModifyProfileActivity extends AppCompatActivity {

    boolean nameModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        // set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // fill the layout with possible apps
        fillLayout();

        // assume the name has not yet been modified
        nameModified = false;

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
        switch (item.getItemId())
        {
            case R.id.done:
                //but first prompt the user for confirmation
                if(validate()) {
                    onBackPressed();

                    return true;
                }
            case android.R.id.home:
                onBackPressed();


        }


        return true;
    }


    boolean validate()
    {
        // implement validation here
        return true;
    }


    void fillLayout()
    {
        // logic to fill out all the apps here

        // some tests
        createAppRow("app 1", 1);
        createAppRow("app 2", 1);
        createAppRow("app 3", 1);

    }

    void createAppRow(String appName, int appIndex)
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
        TextView appNameView = new TextView(ModifyProfileActivity.this);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(250, 80);
        paramsText.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        paramsText.weight = 5;
        appNameView.setLayoutParams(paramsText);
        appNameView.setText(appName);
        appNameView.setTextSize(20);




        // create a checkbox
        CheckBox checkBox = new CheckBox(ModifyProfileActivity.this);
        checkBox.setChecked(false);
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)checkBox.getLayoutParams();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
        params.weight = 1;
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        checkBox.setLayoutParams(params);

        // add everything to the linearLayout
        app.addView(appNameView);
        app.addView(checkBox);

        // add the app layout to the table
        table.addView(app);

    }


}
