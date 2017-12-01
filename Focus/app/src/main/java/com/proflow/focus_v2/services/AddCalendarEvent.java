package com.proflow.focus_v2.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.proflow.focus_v2.R;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Usman Sohail on 11/27/2017.
 */

public class AddCalendarEvent implements Runnable {

    private Context context;

    public AddCalendarEvent(Context context)
    {
        this.context = context;
    }

    @Override
    public void run() {

        // set priority
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Event googleEvent = new Event()
                        .setSummary("Test")
                        .setDescription("This is a test");

                DateTime startDate = new DateTime("2017-11-21T09:00:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDate)
                        .setTimeZone("America/Los_Angeles");
                googleEvent.setStart(start);

                DateTime endTime = new DateTime("2018-11-21T10:00:00");
                EventDateTime end = new EventDateTime()
                        .setDate(endTime)
                        .setTimeZone("America/Los_Angeles");
                googleEvent.setEnd(end);

                String calendarID = "primary";

                // more calendar stuff
                String [] SCOPES = {CalendarScopes.CALENDAR};

                // get the account name
                SharedPreferences sharedPreferences = context
                        .getSharedPreferences(context.getString(R.string.ACCOUNT_INFO), Context.MODE_PRIVATE);
                String accountName = sharedPreferences.getString(context.getString(R.string.ACCOUNT_NAME), "");         // blank default string
                Log.d("API", "The account name is: " + accountName);

                GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                        context, Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff())
                        .setSelectedAccountName(accountName);

                // transport and json factory
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                Calendar service = new Calendar.Builder(transport, jsonFactory, mCredential).build();

                Log.d("CAL", service.events().toString());
                Log.d("CAL", googleEvent.getSummary().toString());
                Log.d("SHARED PREF", "Account name check in frag" + sharedPreferences.getString(context.getString(R.string.ACCOUNT_NAME), ""));
                Log.d("CAL", mCredential.getSelectedAccountName().toString());



                try {
                    //GoogleSignInAccount googleSignInAccount = new GoogleSignInAccount();

                    googleEvent = service.events().insert(calendarID, googleEvent).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.execute();


    }

}
