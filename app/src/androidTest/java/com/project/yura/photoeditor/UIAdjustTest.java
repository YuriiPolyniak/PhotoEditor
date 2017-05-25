package com.project.yura.photoeditor;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

public class UIAdjustTest {
    @Rule
    public ActivityTestRule<EditImageActivity> mActivityRule = new ActivityTestRule<>(
            EditImageActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void colorCorrectionTest() {
        onView(withId(R.id.adjust_select)).perform(click());
        onView(withId(R.id.balance_adjust_select)).perform(click());
        onView(withId(R.id.color_picker_button)).perform(click());
        onView(withId(R.id.v_radius_slider)).perform(swipeRight());
        //onView(withId(R.id.color_picker_left_preview)).check(matches(withContentDescription("-16729601")));
       // onView(withId(R.id.color_picker_right_preview)).check(matches(withContentDescription("-4652801")));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.seek_bar_balance)).check(matches(withContentDescription("-16776961")));
        onView(withId(R.id.seek_bar_balance)).perform(swipeLeft());
        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.preview_button)).perform(click());
        onView(withId(R.id.ok_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
    }

    @Test
    public void brightnessAdjustTest() {
        onView(withId(R.id.adjust_select)).perform(click());
        onView(withId(R.id.bright_adjust_select)).perform(click());
        onView(withId(R.id.seek_bar)).perform(swipeRight());

        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.preview_button)).perform(click());
        onView(withId(R.id.ok_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
    }
}
