package com.proflow.focus_v2.helpers;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;

/**
 * Created by Usman Sohail on 11/27/2017.
 *
 * This is totally not a necessary class
 */

public class RetrieveTokenTask extends AsyncTask<Object, Void, String> {

    private final int SIGN_IN = 1256;

    @Override
    protected String doInBackground(Object... objects) {
        // get the context
        Context context = (Context)objects[1];
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        String token = account.getIdToken();

        return token;
    }

    @Override
    protected void onPostExecute(String s)
    {

    }

}
