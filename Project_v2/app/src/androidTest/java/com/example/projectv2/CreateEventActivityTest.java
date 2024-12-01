package com.example.projectv2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.CreateEventActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {

    @Rule
    public ActivityScenarioRule<CreateEventActivity> activityRule =
            new ActivityScenarioRule<>(CreateEventActivity.class);
    @Before
    public void setUp() {
        // Adjust Espresso's timeouts
        IdlingPolicies.setMasterPolicyTimeout(10, SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(10, SECONDS);
    }
    /**
     * Test to ensure that the "Next" button displays an error when the event name is empty.
     */
    @Test
    public void testEmptyEventNameValidation() {
        // Click on the "Next" button without entering an event name
        Espresso.onView(withId(R.id.create_event_next_button)).perform(click());
    }

    /**
     * Test to ensure the user can input all required fields and transition to the next activity.
     */
    @Test
    public void testEventCreationFlow() {
        // Step 1: Fill in fields in CreateEventActivity
        Espresso.onView(withId(R.id.create_event_name_view))
                .perform(scrollTo(), typeText("Event Name Test"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_details_view))
                .perform(scrollTo(), typeText("Event details for testing"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_rules_view))
                .perform(scrollTo(), typeText("Event rules for testing"));
        Espresso.closeSoftKeyboard();

        // Step 2: Click the "Next" button to navigate to CreateEventOptionsActivity
        Espresso.onView(withId(R.id.create_event_next_button))
                .perform(scrollTo(), click());

        // Step 3: Validate CreateEventOptionsActivity is displayed
        Espresso.onView(withId(R.id.create_event_options_root))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Step 4: Fill in fields in CreateEventOptionsActivity
        Espresso.onView(withId(R.id.create_event_deadline_view))
                .perform(typeText("30-12-2023"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_start_date))
                .perform(typeText("01-01-2024"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_ticket_price))
                .perform(typeText("50"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_attendees_num_view))
                .perform(typeText("100"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.create_event_entrants_num_view))
                .perform(typeText("150"));
        Espresso.closeSoftKeyboard();

        // Step 5: Interact with checkboxes
        Espresso.onView(withId(R.id.create_event_geolocation_checkbox_view))
                .perform(click()); // Enable geolocation


        Espresso.onView(withId(R.id.create_event_button))
                .perform(click());

// Wait for the Toast to appear
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Espresso.onView(withText("Event created successfully!"))
                .inRoot(new ToastMatcher())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));


    }
}
