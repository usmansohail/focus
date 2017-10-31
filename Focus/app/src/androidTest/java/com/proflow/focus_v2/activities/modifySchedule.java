package com.proflow.focus_v2.activities;


import android.support.test.espresso.ViewInteraction;
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
import static android.support.test.espresso.Espresso.pressBack;
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
public class modifySchedule {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void modifySchedule() {
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
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.app_list_checkbox),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.app_list_linear_layout),
                                        0),
                                2),
                        isDisplayed()));
        appCompatCheckBox.perform(click());

        ViewInteraction appCompatCheckBox2 = onView(
                allOf(withId(R.id.app_list_checkbox),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.app_list_linear_layout),
                                        0),
                                2),
                        isDisplayed()));
        appCompatCheckBox2.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.toolbar_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                3),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

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

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.schedule_name_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatCheckBox3 = onView(
                allOf(withId(R.id.profile_list_checkbox),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatCheckBox3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.toolbar_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                3),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withId(R.id.schedule_more_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withId(R.id.schedule_more_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton6.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.schedule_name_edit_text), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("test modified"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.schedule_name_edit_text), withText("test modified"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Main_Frame),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatCheckBox4 = onView(
                allOf(withId(R.id.schedule_repeat_weekly_radio),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                1),
                        isDisplayed()));
        appCompatCheckBox4.perform(click());

        ViewInteraction appCompatImageButton7 = onView(
                allOf(withId(R.id.toolbar_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                3),
                        isDisplayed()));
        appCompatImageButton7.perform(click());

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