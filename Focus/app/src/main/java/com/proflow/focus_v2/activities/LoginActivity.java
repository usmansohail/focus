package com.proflow.focus_v2.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proflow.focus_v2.R;
import com.proflow.focus_v2.data.Global;
import com.proflow.focus_v2.adapters.ProfileAdapter;
import com.proflow.focus_v2.fragments.CalendarDelete;
import com.proflow.focus_v2.models.Profile;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    //private GoogleSignInClient mGoogleSignInClient;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mStatusTextView;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    // used to only launch intent once
    boolean intentStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        // indicate that an intent has not been started
        intentStarted = false;

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]


        // create client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // check if google already signed in    // TODO
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)


                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]



        if(account != null)
        {
            mGoogleSignInClient.signOut();
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mStatusTextView = findViewById(R.id.status);
        mStatusTextView.setText(R.string.sign_in);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 9001);



            }
        });
        findViewById(R.id.sign_out_button).setOnClickListener(new OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View view) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
        // [START_EXCLUDE]
        //updateUI(false);
        // [END_EXCLUDE]
                    }
            });}
        });
        findViewById(R.id.disconnect_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                    }
                });



        //updateUI
        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        // setup the sign up stuff
        final ScrollView signIn = findViewById(R.id.login_form);
        final ScrollView signUp = findViewById(R.id.register_form);
        final Button sign_up = findViewById(R.id.email_sign_up_button);
        sign_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide the current view

                signIn.setVisibility(View.GONE);
                signUp.setVisibility(View.VISIBLE);
                mStatusTextView.setText(R.string.sign_up);

                Button signUpDone = findViewById(R.id.email_sign_up_button_done);
                signUpDone.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // get all the data
                        validateRegister();
                    }
                });

                Button signInSwitch = findViewById(R.id.email_sign_in_button_switch);
                signInSwitch.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mStatusTextView.setText(R.string.sign_in);
                        signIn.setVisibility(View.VISIBLE);
                        signUp.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void validateRegister() {
        // get the proper views
        TextView usernameView = findViewById(R.id.username_register);
        TextView passwordView = findViewById(R.id.password_register);
        TextView passwordConfirmView = findViewById(R.id.password_register_confirm);
        TextView emailView = findViewById(R.id.email_register);
        TextView fullNameView = findViewById(R.id.full_name_register);

        // get the string values
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String passwordConfirm = passwordConfirmView.getText().toString();
        String email = emailView.getText().toString();
        String fullName = fullNameView.getText().toString();

        // boolean to determine validity
        boolean isValid = true;

        if(username.length() == 0)
        {
            usernameView.setError(getString(R.string.prompt_username));
            isValid = false;
        }
        if(password.length() == 0)
        {
            isValid = false;
            passwordView.setError(getString(R.string.enter_password));
        }
        if(!password.equals(passwordConfirm) || passwordConfirm.length() == 0)
        {
            isValid = false;
            passwordConfirmView.setError(getString(R.string.match_password));
        }
        if(!email.contains("@") || (!email.contains(".")))
        {
            isValid = false;
            emailView.setError(getString(R.string.error_invalid_email));
        }
        if(fullName.length() == 0 || !fullName.contains(" "))
        {
            isValid = false;
            fullNameView.setError(getString(R.string.invalid_name));
        }

        if(isValid) {
            // TODO: put  this into db
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String replace = email.replace('.', '(');
            Log.e("Email before validate", replace);;
            mDatabase.child(replace).child("Password").setValue(password);
            Log.e("Success", "Success!");
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);

        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String emailOrUsername = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean success = false;
                boolean correctEmail = false;
                boolean correctPassword = false;
                View focusView = null;

                for(DataSnapshot s : dataSnapshot.getChildren()){
/*                    String temp = s.getValue(String.class);
                    String tmp = temp.replace('(', '.');
                    Log.e("Email for checking", tmp);*/
                    String tmp = s.getKey();
                    String replace = tmp.replace('(', '.');
                    if(replace.equals(emailOrUsername)){
                        correctEmail = true;
                        if(s.child("Password").getValue(String.class).equals(password)){
                            correctPassword = true;
                            success = true;
                        }
                    }
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(emailOrUsername)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    focusView = mEmailView;
                    success = false;
                } else if (!correctEmail) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                }
                // Check for a valid password, if the user entered one.
                else if (!TextUtils.isEmpty(password) && !correctPassword) {
                    mPasswordView.setError("Password is wrong");
                    focusView = mPasswordView;
                }

                if(!success){
                    loginFailure(focusView);
                }
                else{
                    loginSuccess(emailOrUsername, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loginSuccess(String emailOrUsername, String password){
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserLoginTask(emailOrUsername, password);
        mAuthTask.execute((Void) null);
    }

    private void loginFailure(View focusView){
        focusView.requestFocus();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Error", "onConnectionFailed:" + connectionResult);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            showProgress(true);
            mStatusTextView.setText(R.string.signing_in);

        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> result) {
        try
        {
            GoogleSignInAccount account = result.getResult(ApiException.class);

            Log.d("ACCOUNT", account.getAccount().name.toString());
            // put the account name in shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.ACCOUNT_INFO), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.ACCOUNT_NAME), account.getAccount().name.toString());
            editor.commit();

            Log.d("SHARED PREF", "The account should be in shared prefs");

            // get the token to store
            String token = account.getIdToken();

            signInWithGoogle(account);


        }
        catch (ApiException e)
        {

        }
    }

    private void signInWithGoogle(GoogleSignInAccount account) {
        //TODO: add the user to the db if its not already in there


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        Log.d("SHARED PREF", "Account name check" + sharedPreferences.getString(getString(R.string.ACCOUNT_NAME), ""));

        // if the user doesn't exist
        //use id as password
        //maybe getid instead getidtoken
        Log.d("ID Token", account.getId());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // add a listener to the database
        final String email = account.getEmail().toString();
        final String password = account.getId().toString();
        final Vector<Boolean> numIntents = new Vector<>();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            Boolean success = false;
            Boolean correctPassword = false;
            Boolean validEmail = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s : dataSnapshot.getChildren()){
/*                    String temp = s.getValue(String.class);
                    String tmp = temp.replace('(', '.');
                    Log.e("Email for checking", tmp);*/
                    String tmp = s.getKey();
                    String replace = tmp.replace('(', '.');
                    if(replace.equals(email)){
                        validEmail = true;
                        if(s.child("Password").getValue(String.class).equals(password)){
                            success = true;
                            correctPassword =  true;

                            //login with google stuff!
                            mAuthTask = new UserLoginTask(email, password);
                            mAuthTask.execute((Void) null);
                            Log.e("GOOGLE:", "google sign in");
                            numIntents.add(true);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        String replace = email.replace('.', '(');
        Log.e("Email before validate", replace);
        mDatabase.child(replace).child("Password").setValue(password);
        Log.e("Success", "Success!");

        // if a task hasn't been launched yet, then launch it
        if(numIntents.size() > 0) {
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

           /* for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.*/
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                String replace = mEmail.replace('.', '(');
                Global.getInstance().setUsername(replace);
                Intent enter = new Intent(getApplicationContext(), MainActivity.class);
                //Intent enter = new Intent(getApplicationContext(), CalendarDelete.class);

                Log.d("INTENT", "entering main activity");

                startActivity(enter);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

