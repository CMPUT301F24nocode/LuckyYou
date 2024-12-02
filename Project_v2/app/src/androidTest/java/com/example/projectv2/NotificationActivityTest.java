package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.View.NotificationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * This class contains UI tests for the NotificationActivity. The tests ensure the correct display and
 * functionality of key UI elements such as the RecyclerView, SwipeRefreshLayout, and the top bar title.
 * The test suite also verifies the swipe-to-refresh functionality.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), NotificationActivity.class);
        intent.putExtra("userID", "790715741e05e509");
        ActivityScenario.launch(intent);

        // Check if RecyclerView is displayed
        onView(withId(R.id.notification_recylcerView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeRefreshIsDisplayed() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), NotificationActivity.class);
        intent.putExtra("userID", "790715741e05e509");
        ActivityScenario.launch(intent);

        // Check if SwipeRefreshLayout is displayed
        onView(withId(R.id.notification_swipe_refresh))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTopBarTitleIsDisplayed() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), NotificationActivity.class);
        intent.putExtra("userID", "790715741e05e509");
        ActivityScenario.launch(intent);

        // Check if the top bar title "Notifications" is displayed
        onView(withText("Notifications"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToRefreshFunctionality() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), NotificationActivity.class);
        intent.putExtra("userID", "790715741e05e509");
        ActivityScenario.launch(intent);

        // Perform a swipe down gesture to simulate swipe-to-refresh
        onView(withId(R.id.notification_swipe_refresh)).perform(ViewActions.swipeDown());

        // Verify RecyclerView is still displayed after swipe
        onView(withId(R.id.notification_recylcerView))
                .check(matches(isDisplayed()));
    }
}
