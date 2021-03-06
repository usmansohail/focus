package com.proflow.focus_v2.activities;


import android.support.test.espresso.DataInteraction;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class modifyTimeBlock {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void modifyTimeBlock() {
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
        appCompatEditText.perform(replaceText("te"), closeSoftKeyboard());


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

        ViewInteraction bottomBarTab = onView(
                allOf(withId(R.id.tab_schedules),
                        childAtPosition(
                                allOf(withId(R.id.bb_bottom_bar_item_container),
                                        childAtPosition(
                                                withId(R.id.bb_bottom_bar_outer_container),
                                                1)),
                                1),
                        isDisplayed()));
        bottomBarTab.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.toolbar_add_item),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.schedule_add_time_block),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());


        ViewInteraction appCompatImageButton4 = onView(
                first(allOf(withId(R.id.monday_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed())));
        appCompatImageButton4.perform(click());

        ViewInteraction appCompatImageButton5 = onView(
                first(allOf(withId(R.id.wednesday_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                0),
                        isDisplayed())));
        appCompatImageButton5.perform(click());

        ViewInteraction appCompatRadioButton = onView(
                first(allOf(withId(R.id.create_time_block_end_time_pm), withText("PM"),
                        childAtPosition(
                                allOf(withId(R.id.time_block_end_time_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed())));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.create_time_block_start_time_hour_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatTextView1 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(10);
        appCompatTextView1.perform(click());

        ViewInteraction appCompatSpinner21 = onView(
                allOf(withId(R.id.create_time_block_end_time_hour_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner21.perform(click());

        DataInteraction appCompatTextView21 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatTextView21.perform(click());

        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

        ViewInteraction appCompatCheckBox3 = onView(
                first(allOf(withId(R.id.profile_list_checkbox),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed())));
        appCompatCheckBox3.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.schedule_name_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard());

        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

        ViewInteraction appCompatImageButton9 = onView(
                first(allOf(withId(R.id.schedule_more_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed())));
        appCompatImageButton9.perform(click());

        ViewInteraction appCompatImageButton10 = onView(
                first(allOf(withId(R.id.schedule_more_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed())));
        appCompatImageButton10.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.schedule_add_time_block),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatImageButton11 = onView(
                allOf(withId(R.id.tuesday_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatImageButton11.perform(click());

        ViewInteraction appCompatImageButton12 = onView(
                allOf(withId(R.id.thursday_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatImageButton12.perform(click());

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.create_time_block_end_time_pm), withText("PM"),
                        childAtPosition(
                                allOf(withId(R.id.time_block_end_time_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

        ViewInteraction appCompatImageButton14 = onView(
                first(allOf(withId(R.id.schedule_info_edit_button),
                        childAtPosition(
                                withParent(withId(R.id.schedule_time_block_list_view)),
                                5),
                        isDisplayed())));
        appCompatImageButton14.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.create_time_block_end_time_hour_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(8);
        appCompatTextView.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.create_time_block_start_time_hour_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(10);
        appCompatTextView2.perform(click());



        onView(withId(R.id.toolbar_confirm)).perform(confirmButton);

    }


    public static Matcher<View> first(final Matcher<View> matcher) {
        return new BaseMatcher<View>() {
            boolean isFirst = true;

            @Override
            public boolean matches(Object item) {
                if (isFirst && matcher.matches(item)) {
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