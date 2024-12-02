package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.R;
import com.example.projectv2.View.FacilityEditActivity;
import com.example.projectv2.View.FacilityLandingPageActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityLandingPageActivityTest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        // Initialize Intents for intent verification
        Intents.init();

        // Get the application context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        // Add mock data to SharedPreferences for testing
        sharedPreferences.edit().putBoolean("AdminMode", true).apply();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLayoutElements() {
        // Launch the activity with mock intent data
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), FacilityLandingPageActivity.class);
        intent.putExtra("facility_name", "Test Facility");
        intent.putExtra("facility_description", "This is a test facility description.");
        intent.putExtra("facility_id", "mockFacilityId");
        ActivityScenario.launch(intent);

        // Verify key UI elements are displayed
        onView(withId(R.id.event_name_view)).check(matches(isDisplayed()));
        onView(withId(R.id.facility_description_view)).check(matches(isDisplayed()));
        onView(withId(R.id.facility_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.more_settings_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditButtonInteraction() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), FacilityLandingPageActivity.class);
        intent.putExtra("facility_name", "Test Facility");
        intent.putExtra("facility_description", "This is a test facility description.");
        intent.putExtra("facility_id", "mockFacilityId");
        ActivityScenario.launch(intent);

        onView(withId(R.id.facility_edit_button)).perform(click());

        // Verify intent to FacilityEditActivity
        intended(hasComponent(FacilityEditActivity.class.getName()));
    }

    @Test
    public void testAdminModeVisibility() {
        // Disable admin mode in SharedPreferences
        sharedPreferences.edit().putBoolean("AdminMode", false).apply();

        // Launch the activity with mock intent data
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), FacilityLandingPageActivity.class);
        intent.putExtra("facility_name", "Test Facility");
        intent.putExtra("facility_description", "This is a test facility description.");
        intent.putExtra("facility_id", "mockFacilityId");
        ActivityScenario.launch(intent);

        // Verify more settings button is hidden
        onView(withId(R.id.more_settings_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}

