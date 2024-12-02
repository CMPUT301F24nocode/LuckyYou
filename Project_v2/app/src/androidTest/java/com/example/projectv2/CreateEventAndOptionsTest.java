package com.example.projectv2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.CreateEventOptionsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 *
 * This class contains UI tests for the CreateEventOptionsActivity. The tests verify the correct display
 * and functionality of the user interface elements for creating events. It includes checks for the presence
 * of UI components, interactions with the geolocation toggle, and a complete flow test for event creation.
 *
 */
@RunWith(AndroidJUnit4.class)
public class CreateEventAndOptionsTest {

    @Rule
    public ActivityScenarioRule<CreateEventOptionsActivity> activityRule =
            new ActivityScenarioRule<>(CreateEventOptionsActivity.class);

    @Test
    public void testEventOptionsUI_ElementsDisplayed() {
        // Check if all primary UI elements are present
        Espresso.onView(ViewMatchers.withId(R.id.create_event_deadline_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_attendees_num_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_entrants_num_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_start_date))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_ticket_price))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_geolocation_checkbox_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.create_event_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEventOptions_GeolocationToggle() {
        // Test geolocation checkbox
        Espresso.onView(ViewMatchers.withId(R.id.create_event_geolocation_checkbox_view))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.create_event_geolocation_checkbox_view))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()));
    }

    @Test
    public void testEventCreation_CompleteFlow() {
        // Fill in all event options
        Espresso.onView(ViewMatchers.withId(R.id.create_event_deadline_view))
                .perform(ViewActions.typeText("15-12-2023"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.create_event_start_date))
                .perform(ViewActions.typeText("31-12-2023"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.create_event_attendees_num_view))
                .perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.create_event_entrants_num_view))
                .perform(ViewActions.typeText("50"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.create_event_ticket_price))
                .perform(ViewActions.typeText("10"), ViewActions.closeSoftKeyboard());

        // Click create event
        Espresso.onView(ViewMatchers.withId(R.id.create_event_button))
                .perform(ViewActions.click());

    }
}
