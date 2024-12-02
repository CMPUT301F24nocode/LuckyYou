package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.EventEditActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
/**

 * This class contains UI tests for the EventEditActivity. It ensures that the activity launches
 * correctly with a valid intent and verifies the functionality of key UI components, such as the
 * "Edit Poster" button and the top bar. The tests use Espresso for UI interaction and validation.

 */
@RunWith(AndroidJUnit4.class)
public class EventEditActivityTest {

    private static final String TEST_EVENT_NAME = "Event Name Test";

    /**
     * Verifies the functionality of the "Edit Poster" button.
     */
    @Test
    public void testEditPosterButtonClick() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventEditActivity.class);
        intent.putExtra("name", TEST_EVENT_NAME);
        ActivityScenario.launch(intent);

        // Perform a click on the "Edit Poster" button
        onView(withId(R.id.create_event_next_button)).perform(click());
    }

    /**
     * Verifies that the activity launches with a valid intent and the UI behaves as expected.
     */
    @Test
    public void testActivityLaunchWithValidIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventEditActivity.class);
        intent.putExtra("name", TEST_EVENT_NAME);
        ActivityScenario.launch(intent);

        // Verify the top bar is displayed, meaning the activity launched successfully
        onView(withId(R.id.top_bar)).check(matches(isDisplayed()));
    }
}
