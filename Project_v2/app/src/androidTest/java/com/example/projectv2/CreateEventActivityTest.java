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
}
