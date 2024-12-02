package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.View.EntrantListActivity;
import com.example.projectv2.View.EventEditActivity;
import com.example.projectv2.View.EventLandingPageOrganizerActivity;
import com.example.projectv2.View.QrOrganiserActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**

 * This class contains UI tests for the EventLandingPageOrganizerActivity. It verifies the presence
 * and functionality of key UI elements, including buttons for editing events, viewing QR codes, and
 * accessing the entrant list. It also ensures that intent-based navigation to related activities is
 * functioning correctly.

 */
@RunWith(AndroidJUnit4.class)
public class EventLandingPageOrganizerActivityTest {

    @Before
    public void setup() {
        // Initialize Intents for intent verification
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLayoutElements() {
        // Launch the activity with mock intent data
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventLandingPageOrganizerActivity.class);
        intent.putExtra("name", "Test Event");
        intent.putExtra("details", "This is a test event description.");
        intent.putExtra("eventID", "mockEventId");
        ActivityScenario.launch(intent);

        // Verify key UI elements are displayed
        onView(withId(R.id.event_name_view_organiser)).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_view_organiser)).check(matches(isDisplayed()));
        onView(withId(R.id.event_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qrcode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_list_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditButtonInteraction() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventLandingPageOrganizerActivity.class);
        intent.putExtra("name", "Test Event");
        intent.putExtra("details", "This is a test event description.");
        intent.putExtra("eventID", "mockEventId");
        ActivityScenario.launch(intent);

        onView(withId(R.id.event_edit_button)).perform(click());

        // Verify intent to EventEditActivity
        intended(hasComponent(EventEditActivity.class.getName()));
    }

    @Test
    public void testQRCodeButtonInteraction() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventLandingPageOrganizerActivity.class);
        intent.putExtra("name", "Test Event");
        intent.putExtra("details", "This is a test event description.");
        intent.putExtra("eventID", "mockEventId");
        ActivityScenario.launch(intent);

        onView(withId(R.id.qrcode_button)).perform(click());

        // Verify intent to QrOrganiserActivity
        intended(hasComponent(QrOrganiserActivity.class.getName()));
    }

    @Test
    public void testEntrantListButtonInteraction() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventLandingPageOrganizerActivity.class);
        intent.putExtra("name", "Test Event");
        intent.putExtra("details", "This is a test event description.");
        intent.putExtra("eventID", "mockEventId");
        ActivityScenario.launch(intent);

        onView(withId(R.id.view_entrant_list_button)).perform(click());

        // Verify intent to EntrantListActivity
        intended(hasComponent(EntrantListActivity.class.getName()));
    }

    @Test
    public void testGeolocationButtonVisibility() {
        // Launch the activity with mock intent data where geolocation is disabled
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventLandingPageOrganizerActivity.class);
        intent.putExtra("name", "Test Event");
        intent.putExtra("details", "This is a test event description.");
        intent.putExtra("eventID", "mockEventId");
        ActivityScenario.launch(intent);

        // Verify that the location button is not visible
        onView(withId(R.id.location_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}

