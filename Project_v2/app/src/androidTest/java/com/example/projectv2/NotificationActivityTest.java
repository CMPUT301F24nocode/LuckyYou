package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.Model.Notification;
import com.example.projectv2.View.NotificationActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest {

    @Test
    public void testRecyclerViewWithMockData() {
        // Prepare mock notifications
        List<Notification> mockNotifications = Arrays.asList(
                new Notification("user1", "Sample Admin Notification", "2 mins ago", false, true),
                new Notification("user2", "Sample Organizer Notification", "5 mins ago", true, false)
        );

        // Launch activity and inject mock data
        ActivityScenario<NotificationActivity> scenario = ActivityScenario.launch(NotificationActivity.class);

        scenario.onActivity(activity -> {
            // Inject mock notifications
            activity.injectMockNotifications(mockNotifications);
        });

        // Check if RecyclerView is displayed
        onView(withId(R.id.notification_recylcerView))
                .check(matches(isDisplayed()));

        // Check if mock notification texts are displayed
        onView(withText("Sample Admin Notification"))
                .check(matches(isDisplayed()));

        onView(withText("Sample Organizer Notification"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeRefreshIsDisplayed() {
        ActivityScenario.launch(NotificationActivity.class);

        // Check if SwipeRefreshLayout is displayed
        onView(withId(R.id.notification_swipe_refresh))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTopBarTitleIsDisplayed() {
        ActivityScenario.launch(NotificationActivity.class);

        // Check if the top bar title "Notifications" is displayed
        onView(withText("Notifications"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToRefreshFunctionality() {
        ActivityScenario.launch(NotificationActivity.class);

        // Perform a swipe down gesture to simulate swipe-to-refresh
        onView(withId(R.id.notification_swipe_refresh)).perform(ViewActions.swipeDown());

        // Verify RecyclerView is still displayed after swipe
        onView(withId(R.id.notification_recylcerView))
                .check(matches(isDisplayed()));
    }
}
