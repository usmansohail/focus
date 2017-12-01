package com.proflow.focus_v2.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.activities.MainActivity;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.adapters.TimeBlockAdapter;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.helpers.RetrieveTokenTask;
import com.proflow.focus_v2.models.Profile;
import com.proflow.focus_v2.models.Schedule;
import com.proflow.focus_v2.models.TimeBlock;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.client.json.gson.GsonFactory;
import com.proflow.focus_v2.services.AddCalendarEvent;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.api.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.content.ContentValues.TAG;

public class CreateScheduleFragment extends BaseFragment {

    EditText mNameEditText;
    ListView mTimeBlockList;
    AppCompatCheckBox mRepeatWeeklyButton;
    private TimeBlockAdapter mTimeBlockAdapter;

    private boolean mIsNew = false;
    static AsyncTask<Void, Void, Void> task;
    private Schedule mSchedule = null;
    private RecyclerView mProfileRecycler;
    private ProfileAdapter mProfileAdapter;

    public CreateScheduleFragment() {
        // Required empty public constructor
    }

    public static CreateScheduleFragment newInstance() {
        CreateScheduleFragment fragment = new CreateScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setupToolbar() {
        showBackButton(true);
        showConfirmButton(true);
        setShowBottomBar(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null){
            if(getArguments().containsKey(getString(R.string.scheduleKey))) {
                Log.d(TAG, "Attempting to get Schedule by ID");
                int schedID = getArguments().getInt(getString(R.string.scheduleKey));
                mSchedule = Global.getInstance().getScheduleById(schedID);
                Log.d(TAG, "Schedule Stats: Num TimeBlocks: " + mSchedule.getTimeBlocks().size()
                + " RepeatWeekly: " + mSchedule.repeatWeekly() + " Num Profiles: " + mSchedule.getProfiles().size());
            }
            if(getArguments().containsKey(getString(R.string.schedule_is_new))){
                mIsNew = getArguments().getBoolean(getString(R.string.schedule_is_new));
            }
        } else {
            mSchedule = null;
        }


        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_schedule, container, false);
        mNameEditText = layout.findViewById(R.id.schedule_name_edit_text);
        mNameEditText.setText(mSchedule.getName());
        mTimeBlockList = layout.findViewById(R.id.schedule_time_block_list_view);

        mRepeatWeeklyButton = layout.findViewById(R.id.schedule_repeat_weekly_radio);
        if(mSchedule != null){
            mRepeatWeeklyButton.setChecked(mSchedule.repeatWeekly());
        }

        mTimeBlockAdapter = new TimeBlockAdapter(getContext(), mSchedule, mIsNew);
        Button footer = layout.findViewById(R.id.schedule_add_time_block);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = CreateTimeBlockFragment.newInstance();
                Bundle args = new Bundle();

                mSchedule.setName(mNameEditText.getText().toString());
                mSchedule.setRepeatWeekly(mRepeatWeeklyButton.isChecked());
                mSchedule.setProfiles(getSelectedProfiles());
                mSchedule.setTimeBlocks(mTimeBlockAdapter.getTimeBlocks());
                Global.getInstance().modifySchedule(getContext(), mSchedule);

                args.putInt(getString(R.string.scheduleKey), mSchedule.getId());
                args.putBoolean(getString(R.string.schedule_is_new), mIsNew);
                frag.setArguments(args);
                FragmentTransaction ft = ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.Main_Frame, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Log.d(TAG, "TIMEBLOCKADAPTER: HAS: " + mTimeBlockAdapter.getCount());
        mTimeBlockList.setAdapter(mTimeBlockAdapter);
        mProfileRecycler = layout.findViewById(R.id.create_schedule_profile_recycler);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Global.getInstance().getUsername()).child("Profiles");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> temp = new ArrayList<>();
                for(DataSnapshot id : dataSnapshot.getChildren()){
                    Profile profile = new Profile(id);
                    temp.add(profile);
                }

                mProfileAdapter = new ProfileAdapter(temp, getContext(), mSchedule);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                mProfileRecycler.setLayoutManager(mLayoutManager);
                mProfileRecycler.setItemAnimator(new DefaultItemAnimator());
                mProfileRecycler.setAdapter(mProfileAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button mDeleteButton = layout.findViewById(R.id.create_schedule_delete_button);
        if (!mIsNew){
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//                    mDatabase.child(Global.getInstance().getUsername()).child("Schedules").child(Integer.toString(mSchedule.getId())).removeValue();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.Main_Frame, MainActivity.schedulesFragment);

                    Bundle args = new Bundle();
                    args.putInt(getString(R.string.schedule_id_to_delete), mSchedule.getId());

                    MainActivity.schedulesFragment.setArguments(args);
                    ft.commit();
                }
            });
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO implement validation of input -- schedule.
                if(validate()) {
                    //Get values from fragment
                    String scheduleName = mNameEditText.getText().toString();
                    final Vector<TimeBlock> timeBlocks = mTimeBlockAdapter.getTimeBlocks();
                    Boolean repeat = mRepeatWeeklyButton.isChecked();
                    Vector<Profile> checkedProfiles = mProfileAdapter.getCheckedProfiles();

                    for(int i = 0; i < checkedProfiles.size(); i++){
                        Log.d(TAG, "Adding profile \"" + checkedProfiles.get(i).getName() + "\" to schedule");
                    }

                    //Set associated values in sched
                    mSchedule.setProfiles(checkedProfiles);
                    mSchedule.setName(scheduleName);
                    mSchedule.setTimeBlocks(timeBlocks);
                    mSchedule.setRepeatWeekly(repeat);

                    //tell schedule to modify sched if it exists, and if not - add it.
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(Global.getInstance().getUsername()).child("Schedules").child(String.valueOf(mSchedule.getId())).setValue(mSchedule);
                    mDatabase.child(Global.getInstance().getUsername()).child("Schedules").child(String.valueOf(mSchedule.getId())).child("repeatWeekly").setValue(repeat);

                    //TODO: Implement adding to google calendar
                    new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.add_to_calendar_prompt))
                            .setNegativeButton("No thank-you", null)
                            .setPositiveButton("Yes!", new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                // add all the events to google
                                    for(TimeBlock timeBlock: timeBlocks)
                                    {
                                        addEventToGoogle(timeBlock, mSchedule);
                                    }
                                }
                            })
                            .create()
                            .show();
//                    DateTime endTime = new DateTime("2017-11-21T09:00:00-9:00");
//                    EventDateTime end = new EventDateTime()
//                            .setDate(endTime)
//                            .setTimeZone("America/Los_Angeles");
                    /*googleEvent.setEnd(end);

                    String calendarID = "primary";*/
                   // googleEvent = service.events().insert(calendarID, googleEvent).execute();


                    Global.getInstance().modifySchedule(getContext(), mSchedule);
                    Global.getInstance().synchSchedules(getContext());
                    resetNotificationFlags();
                    getActivity().onBackPressed();
                }
            }
        });

        navBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsNew) {
                    Global.getInstance().removeSchedule(getContext(), mSchedule);
                    resetNotificationFlags();
                }
                getActivity().onBackPressed();
            }
        });

        if(getArguments() != null && getArguments().get(getString(R.string.timeBlockKey)) != null){
            TimeBlock newTimeBlock = (TimeBlock) getArguments().get(getString(R.string.timeBlockKey));
            mSchedule.addTimeBlock(newTimeBlock);
        }


        if(!mIsNew){
        }


        return layout;
    }

    private void addEventToGoogle(final TimeBlock timeBlock, Schedule schedule)
    {
        // get the name of the calendar
        final String scheduleName = schedule.getName();
        Boolean repeat = schedule.repeatWeekly();

        // get the current day
        LocalDateTime now = LocalDateTime.now();


        // get the day's of the timeblock
        Vector<String> days = new Vector<>();
        boolean first = true;
        final Vector<DayOfWeek> firstDay = new Vector<>();

        for(TimeBlock.day d: timeBlock.getDays())
        {

            if(d.ordinal() == TimeBlock.day.MONDAY.ordinal())
            {
                days.add("MO");
                firstDay.add(DayOfWeek.MONDAY);
            }
            if(d.ordinal() == TimeBlock.day.TUESDAY.ordinal())
            {
                firstDay.add(DayOfWeek.TUESDAY);
                days.add("TU");
            }
            if(d.ordinal() == TimeBlock.day.WEDNESDAY.ordinal())
            {
                firstDay.add(DayOfWeek.WEDNESDAY);
                days.add("WE");
            }
            if(d.ordinal() == TimeBlock.day.THURSDAY.ordinal())
            {
                firstDay.add(DayOfWeek.THURSDAY);
                days.add("TH");
            }
            if(d.ordinal() == TimeBlock.day.FRIDAY.ordinal())
            {
                firstDay.add(DayOfWeek.FRIDAY);
                days.add("FR");
            }
            if(d.ordinal() == TimeBlock.day.SATURDAY.ordinal())
            {
                firstDay.add(DayOfWeek.SATURDAY);
                days.add("SA");
            }
            if(d.ordinal() == TimeBlock.day.SUNDAY.ordinal())
            {
                firstDay.add(DayOfWeek.SUNDAY);
                days.add("SU");
            }
        }

        // create the recurrence string for this event
        String recurrenceDays = "RRULE:FREQ=WEEKLY;";



        if(days.size() > 0) {

            // add to the recurrence string
            recurrenceDays += "BYDAY=";

            for (int i = 0; i < days.size(); i++) {
                if (i < days.size() - 1) {
                    recurrenceDays += days.elementAt(i) + ',';
                } else {
                    recurrenceDays += days.elementAt(i);
                }
            }
        }

        int count = 0;
        // set the count based on the repeate
        if(schedule.repeatWeekly())
        {
            // for now just set the repeate value to 100
            count = 100;
        }
        else
        {
            // set the count to the nuber of days that were selected
            count = days.size();
        }

        // add the count to the RRCUR string
        recurrenceDays += ";COUNT=" + count;

        // setup the final  recurrenceDays string that can be accessed from inner class
        final String finalReccuranceDays = recurrenceDays;

        final DayOfWeek finalFirstDay = firstDay.elementAt(0);

        String pattern = "T\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d[z|Z]";


        // get the datetime from the timeblock
        Date sDate = Date.from(now.with(TemporalAdjusters.next(finalFirstDay)).atZone(ZoneId.systemDefault()).toInstant());
        Log.d("TIME", sDate.toInstant().toString());
        // use regular expression to remove the time from the now timestamp
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(sDate.toInstant().toString());
        String dateString = m.replaceAll("");
        Log.d("TIME", "Truncated: " + dateString + " without: ");

        TimeZone tz = TimeZone.getDefault();

        final String startString = dateString + "T" + timeBlock.getStartTime().getRFCString() + "-08:00";
        final boolean singleDay = (days.size() == 1);

        Date eDate = sDate;
        final String endString = dateString + "T" + timeBlock.getEndTime().getRFCString() + "-08:00";


        Log.d("TIME", "start time: " + startString);
        Log.d("TIME", "end time: " + endString);
        // create an AddCalendarEvent runnable object
        task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Event event = new Event()
                        .setSummary(scheduleName)
                        .setDescription("This is an auto-generated event created by the Focus! application " +
                                "for the schedule named: " + scheduleName + ".");


                DateTime startDateTime = new DateTime(startString);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/Los_Angeles");
                event.setStart(start);

                DateTime endDateTime = new DateTime(endString);
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("America/Los_Angeles");
                event.setEnd(end);

                Log.d("RRULE", finalReccuranceDays);
                if(!singleDay) {
                    String[] recurrence = new String[]{finalReccuranceDays};
                    event.setRecurrence(Arrays.asList(recurrence));
                }
                EventAttendee[] attendees = new EventAttendee[] {
                };
                event.setAttendees(Arrays.asList(attendees));

                EventReminder[] reminderOverrides = new EventReminder[] {
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);



                // more calendar stuff
                String [] SCOPES = {CalendarScopes.CALENDAR};

                // get the account name
                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences(getString(R.string.ACCOUNT_INFO), Context.MODE_PRIVATE);
                String accountName = sharedPreferences.getString(getString(R.string.ACCOUNT_NAME), "");         // blank default string
                Log.d("API", "The account name is: " + accountName);

                new RetrieveTokenTask().execute(accountName, getActivity());

                GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                        getActivity(), Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff())
                        .setSelectedAccountName(accountName);

                // transport and json factory
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                Calendar service = new Calendar.Builder(transport, jsonFactory, mCredential).build();



                try {
                    String calendarId = "primary";
                    event = service.events().insert(calendarId, event).execute();
                    Log.d("CAL UPDATE:", event.getSummary());
                    Log.d("CAL UPDATE:", event.getHtmlLink());
                }
                catch (UserRecoverableAuthIOException e)
                {
                    Log.d("USER", "caught weird exception");
                    getActivity().startActivityForResult(e.getIntent(), 1256);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


                String pageToken = null;
                String eventSummary = "";
                do {


                    CalendarList calendarList = null;
                    try {
                        calendarList = service.calendarList().list().setPageToken(pageToken).execute();
                        List<CalendarListEntry> items = calendarList.getItems();

                        for(CalendarListEntry cal: items)
                        {
                            Log.d("CALENDAR:", cal.getSummary());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //List<Calendar> calendars = (List<Calendar>) service.calendarList();

                    pageToken = calendarList.getNextPageToken();

                }while (pageToken != null);
                Vector<String> eventStrings = new Vector<String>();

                return null;
            }


        };
        task.execute();}

    private boolean validate() {
        boolean hasName = !mNameEditText.getText().toString().isEmpty();
        boolean hasProfile = mProfileAdapter.getCheckedProfiles().size() > 0;
        boolean hasTimeBlocks = mTimeBlockAdapter.getTimeBlocks().size() > 0;
        boolean uniqueName = true;
        for(Schedule s : Global.getInstance().getSchedules()){
            if(s.getName().compareToIgnoreCase(mNameEditText.getText().toString()) == 0
                    && s.getId() != getArguments().getInt(getString(R.string.scheduleKey))){
                uniqueName = false;
            }
        }

        if(!hasName){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoName), Toast.LENGTH_SHORT).show();
        } else if(!hasProfile){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoProfiles), Toast.LENGTH_SHORT).show();
        } else if(!hasTimeBlocks){
            Toast.makeText(getContext(), getString(R.string.scheduleHasNoTimeBlocks), Toast.LENGTH_SHORT).show();
        } else if(!uniqueName){
            Toast.makeText(getContext(), getString(R.string.scheduleNameIsTaken), Toast.LENGTH_SHORT).show();
        }


        return hasName && hasProfile && hasTimeBlocks && uniqueName;
    }

    public void addTimeBlock(TimeBlock timeBlock) {
        mSchedule.addTimeBlock(timeBlock);
        mTimeBlockAdapter.notifyDataSetChanged();
    }

    public void resetNotificationFlags(){
        Vector<Boolean> blockingProfiles = new Vector<>();
        Vector<Schedule> schedules = Global.getInstance().getSchedules();
        for(int i=0; i<schedules.size(); i++){
            blockingProfiles.add(schedules.get(i).isBlocking());
        }
        Global.getInstance().setScheduleFlags(getContext(), blockingProfiles);
    }

    public Vector<Profile> getSelectedProfiles() {
        return mProfileAdapter.getCheckedProfiles();
    }
}