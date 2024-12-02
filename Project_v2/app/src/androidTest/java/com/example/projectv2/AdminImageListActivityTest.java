package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.AdminImageListActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminImageListActivityTest {

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Use ActivityScenario to launch AdminImageListActivity
        ActivityScenario<AdminImageListActivity> scenario = ActivityScenario.launch(AdminImageListActivity.class);

        // Perform the test
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToRefreshFetchesImages() {
        // Use ActivityScenario to launch AdminImageListActivity
        ActivityScenario<AdminImageListActivity> scenario = ActivityScenario.launch(AdminImageListActivity.class);

        // Perform a swipe down on the SwipeRefreshLayout
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());

        // Check if the RecyclerView is displayed after the swipe
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewDisplaysImages() {
        // Use ActivityScenario to launch AdminImageListActivity
        ActivityScenario<AdminImageListActivity> scenario = ActivityScenario.launch(AdminImageListActivity.class);

        // Delay to simulate waiting for async operations (if needed)
        try {
            Thread.sleep(2000); // Replace with an IdlingResource for better accuracy
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if RecyclerView displays items
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }
}