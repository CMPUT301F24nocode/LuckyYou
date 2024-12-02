package com.example.projectv2;

import java.util.Random;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.CreateEventActivity;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.regex.Pattern.matches;

import android.view.View;

@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {


    @Rule
    public ActivityScenarioRule<CreateEventActivity> activityRule =
            new ActivityScenarioRule<>(CreateEventActivity.class);
    private View decorView;

    @Before
    public void setUp() {
        // Adjust Espresso's timeouts
        IdlingPolicies.setMasterPolicyTimeout(10, SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(10, SECONDS);
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<CreateEventActivity>() {
            @Override
            public void perform(CreateEventActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    /**
     * Test to ensure that the "Next" button displays an error when the event name is empty.
     */
    @Test
    public void testEmptyEventNameValidation() {
        // Click on the "Next" button without entering an event name
        onView(withId(R.id.create_event_next_button)).perform(click());
    }

    /**
     * Test to ensure the user can input all required fields and transition to the next activity.
     */
    @Test
    public void testEventCreationFlow() {
        Random random = new Random();

        // Fill in fields in CreateEventActivity
        onView(withId(R.id.create_event_name_view))
                .perform(scrollTo(), typeText("Event Name Test" + random.nextInt(1000)));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_details_view))
                .perform(scrollTo(), typeText("Event details for testing"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_rules_view))
                .perform(scrollTo(), typeText("Event rules for testing"));
        Espresso.closeSoftKeyboard();

        // Navigate to CreateEventOptionsActivity
        onView(withId(R.id.create_event_next_button))
                .perform(scrollTo(), click());

        // Validate CreateEventOptionsActivity is displayed
        onView(withId(R.id.create_event_options_root))
                .check(ViewAssertions.matches(isDisplayed()));

        // Fill in fields in CreateEventOptionsActivity
        onView(withId(R.id.create_event_deadline_view))
                .perform(typeText("30-12-2023"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_start_date))
                .perform(typeText("01-01-2024"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_ticket_price))
                .perform(typeText("50"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_attendees_num_view))
                .perform(typeText("100"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_event_entrants_num_view))
                .perform(typeText("150"));
        Espresso.closeSoftKeyboard();

        // Interact with checkboxes
        onView(withId(R.id.create_event_geolocation_checkbox_view))
                .perform(click());

        // Click the "Create Event" button
        onView(withId(R.id.create_event_button))
                .perform(click());

        // Wait for the Toast to appear
        try {
            Thread.sleep(2000); // Adjust this duration based on your app's behavior
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withText("Event created successfully!"))
                .inRoot(withDecorView(Matchers.not(decorView)))// Here you use decorView
                .check(ViewAssertions.matches(isDisplayed()));

    }
}
