package com.proflow.focus_v2;

/**
 * Created by Usman Sohail on 10/27/2017.
 */

/**
 * Profile test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;

import com.proflow.focus_v2.activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import android.support.test.espresso.contrib.RecyclerViewActions;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class profileTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.startActivity(new Intent(appContext, MainActivity.class));
    }

    @Test
    public void makeProfile()
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        // click the profiles button
        onView(withId(R.id.tab_profiles)).perform(click());


        // click on the add profile button
        onView(withId(R.id.create_profile)).perform(click());


        // type the name of the profile
        onView(withId(R.id.profile_name_edit_text)).perform(typeText("Sample Profile"), closeSoftKeyboard());

        // select at least one item, just do the first one
        onView(withId(R.id.create_profile_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        // click create profile
        onView(withId(R.id.confirm_create_profile)).perform(click());

        // check data for new profile

    }


}
