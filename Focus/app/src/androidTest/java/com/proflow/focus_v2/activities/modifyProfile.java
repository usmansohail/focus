package com.proflow.focus_v2.activities;


import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.proflow.focus_v2.R;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class modifyProfile {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void modifyProfileName() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.toolbar_add_item),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.profile_name_edit_text),
                        childAtPosition(
                                allOf(withId(R.id.fragment_create_profile),
                                        childAtPosition(
                                                withId(R.id.Main_Frame),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Test"), closeSoftKeyboard());

        ViewInteraction appCompatCheckBox = onView(
                first(allOf(withId(R.id.app_list_checkbox),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.app_list_linear_layout),
                                        0),
                                2),
                        isDisplayed())));
        appCompatCheckBox.perform(click());

        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

        ViewInteraction appCompatImageButton3 = onView(
                first(allOf(withId(R.id.profile_list_edit_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                3),
                        isDisplayed())));
        appCompatImageButton3.perform(click());



        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.profile_name_edit_text),
                        childAtPosition(
                                allOf(withId(R.id.fragment_create_profile),
                                        childAtPosition(
                                                withId(R.id.Main_Frame),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Test modified"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.profile_name_edit_text), withText("Test modified"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_create_profile),
                                        childAtPosition(
                                                withId(R.id.Main_Frame),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

    }

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
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}