package com.proflow.focus_v2;

/**
 * Created by Usman Sohail on 10/27/2017.
 */

/**
 * Profile test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import com.fdunlap.focus_v2.test.*;

import com.proflow.focus_v2.activities.MainActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.proflow.focus_v2.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class profileTest {

    String name = "Sample Profile";

    /*
    @Test
    public void makeProfile()
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.startActivity(new Intent(appContext, MainActivity.class));


        // click the profiles button
        onView(withId(R.id.tab_profiles)).perform(click());


        // click on the add profile button
        onView(withId(R.id.toolbar_add_item)).perform(click());


        // type the name of the profile
        onView(withId(R.id.profile_name_edit_text)).perform(typeText(name), closeSoftKeyboard());

        // select at least one item, just do the first one
        onView(withId(R.id.create_profile_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        // click create profile
        onView(withId(R.id.toolbar_confirm)).perform( new ViewAction() {
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
        });

        // check data for new profile
        onView(first(withText(name))).check(matches(isDisplayed()));

    }

    */


    @Test
    public void modifyProfile()
    {
        String newName = "New Profile Name";

        Context appContext = getTargetContext();
        appContext.startActivity(new Intent(appContext, MainActivity.class));


        // click the profiles button
        onView(withId(R.id.tab_profiles)).perform(click());


        // edit the name
        onView(withId(R.id.profile_name_edit_text)).perform(clearText(), typeText(newName));

        // press the confirm button
        onView(withId(R.id.toolbar_confirm)).perform( new ViewAction() {
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
        });

        // check data for new profile
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
