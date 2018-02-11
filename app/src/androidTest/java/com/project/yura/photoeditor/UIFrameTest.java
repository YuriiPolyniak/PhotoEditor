package com.project.yura.photoeditor;

import android.support.test.rule.ActivityTestRule;

import com.project.yura.photoeditor.ui.activity.EditImageActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

public class UIFrameTest {
    @Rule
    public ActivityTestRule<EditImageActivity> mActivityRule = new ActivityTestRule<>(
            EditImageActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void frameTest() {
        onView(withId(R.id.frame_select)).perform(click());
        onView(withId(R.id.preview_recycler)).perform(click());
        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.preview_button)).perform(click());
        onView(withId(R.id.ok_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.resize_button)).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
    }
}
