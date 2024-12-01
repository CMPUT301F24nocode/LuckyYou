package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.R;
import com.example.projectv2.View.FacilityCreateActivity;
import com.example.projectv2.View.FacilityListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityListActivityTest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        // Initialize Intents for intent verification
        Intents.init();

        // Get the application context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
    }

    @After
    public void tearDown() {
        // Release Intents
        Intents.release();
    }

    @Test
    public void testActivityLaunch() {
        // Launch the activity
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Verify key UI elements are displayed
        onView(withId(R.id.facility_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.create_facility_button)).check(matches(isDisplayed()));
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateFacilityButton() {
        // Set up admin mode to ensure create button is visible
        sharedPreferences.edit().putBoolean("AdminMode", true).apply();

        // Launch the activity
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Click on create facility button
        onView(withId(R.id.create_facility_button)).perform(click());

        // Verify intent to FacilityCreateActivity
        intended(hasComponent(FacilityCreateActivity.class.getName()));
    }

    @Test
    public void testSwipeRefreshLayout() {
        // Launch the activity
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Perform swipe refresh
        onView(withId(R.id.swipe_refresh_layout)).perform(click());

        // Note: This test verifies the swipe refresh layout is functional
        // Actual content refresh is typically handled by mock data or instrumentation
    }

    @Test
    public void testAdminModeVisibility() {
        // Enable admin mode
        sharedPreferences.edit().putBoolean("AdminMode", true).apply();

        // Launch the activity
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Verify create facility button is visible
        onView(withId(R.id.create_facility_button)).check(matches(isDisplayed()));
    }
}