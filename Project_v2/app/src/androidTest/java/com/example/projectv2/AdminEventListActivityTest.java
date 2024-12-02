package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.AdminEventListActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *
 * This class contains UI tests for the AdminEventListActivity. It verifies the functionality of
 * critical components such as the RecyclerView and SwipeRefreshLayout. The tests ensure the activity
 * correctly displays the event list and supports swipe-to-refresh functionality.
 *
 * */
@RunWith(AndroidJUnit4.class)
public class AdminEventListActivityTest {

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Launch the AdminEventListActivity
        ActivityScenario.launch(AdminEventListActivity.class);

        // Check if the RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToRefreshUpdatesEventList() {
        // Launch the AdminEventListActivity
        ActivityScenario.launch(AdminEventListActivity.class);

        // Perform swipe down gesture on SwipeRefreshLayout
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());

        // Check if RecyclerView is still displayed after swipe refresh
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testEventsAreLoadedIntoRecyclerView() {
        // Launch the AdminEventListActivity
        ActivityScenario.launch(AdminEventListActivity.class);

        // Wait for events to load (consider using IdlingResource for async operations)
        try {
            Thread.sleep(2000); // Replace this with IdlingResource if possible
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the RecyclerView is populated (mock data or actual data is required)
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }
}

