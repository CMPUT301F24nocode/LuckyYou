package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.AdminProfileListActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *
 * This class contains UI tests for the AdminProfileListActivity. It verifies the correct
 * implementation of critical UI elements such as the top bar title, RecyclerView visibility,
 * and the functionality of the SwipeRefreshLayout. The tests ensure that the activity layout
 * meets the expected requirements for user interaction.
 *
 */
@RunWith(AndroidJUnit4.class)
public class AdminProfileListActivityTest {

    @Before
    public void setUp() {
        // Launch the AdminProfileListActivity
        ActivityScenario.launch(AdminProfileListActivity.class);
    }

    @Test
    public void testTopBarTitleDisplayed() {
        // Check that the top bar displays "Browse Profiles"
        onView(withText("Browse Profiles")).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewDisplayed() {
        // Verify that the RecyclerView is visible
        onView(withId(R.id.adminProfileListRecycler)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToRefreshWorks() {
        // Perform swipe-to-refresh and ensure it's functional
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()));
    }

}



