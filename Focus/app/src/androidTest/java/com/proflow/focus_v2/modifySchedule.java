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
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.proflow.focus_v2.activities.MainActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class modifySchedule {

    ViewAction confirmButton = new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
            return ViewMatchers.isEnabled(); // no constraints, they are checked above
        }

        @Override
        public String getDescription() {
            return "click plus button";
        }

        @Override
        public void perform(UiController uiController, View view) {
            view.performClick();
        }
    };


    // test the ability to modify the schedule
    @Test
    public void modifySchedule()
    {

        String scheduleName = "schedule name";
        String newName = "New Schedule Name";

        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        // click on the schedule page
        onView(withId(R.id.tab_schedules)).perform(click());

        // double click on the item with the correct name
        onView(first(allOf(withId(R.id.schedule_more_button)))).perform(doubleClick());

        // change the name
        onView(withId(R.id.schedule_name_edit_text)).perform(clearText(), typeText(newName));

        // press confirm
        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

        // confirm the new schedule name is displayed
        onView(first(withText(newName))).check(matches(isDisplayed()));


    }




    public static Matcher<View> first(final Matcher<View> matcher)
    {
        return new BaseMatcher<View>() {
            boolean isFirst = true;

            @Override
            public boolean matches(Object item) {
                if(isFirst && matcher.matches(item))
                {
                    isFirst = false;
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches the first item of it's kind");
            }
        };
    }


    public static Matcher<View> first(final Matcher<View> matcher, final Matcher<View> matcherTwo)
    {
        return new BaseMatcher<View>() {
            boolean isFirst = true;

            @Override
            public boolean matches(Object item) {
                if(isFirst && matcher.matches(item))
                {
                    isFirst = false;
                    if(matcherTwo.matches(item)) {

                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches the first item of it's kind");
            }
        };
    }


}
