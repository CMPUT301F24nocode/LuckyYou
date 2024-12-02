package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.View.FacilityCreateActivity;
import com.example.projectv2.View.FacilityListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * This class contains UI tests for the FacilityListActivity. The tests ensure that the layout elements,
 * button interactions, and admin mode features function correctly. The test suite covers scenarios such
 * as activity launch, clicking the create facility button, refreshing the facility list, and verifying
 * admin mode visibility.

 */
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
        Intents.release();
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Verify key UI elements are displayed
        onView(withId(R.id.facility_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.create_facility_button)).check(matches(isDisplayed()));
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateFacilityButton() {
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Click on create facility button
        onView(withId(R.id.create_facility_button)).perform(click());

        // Verify intent to FacilityCreateActivity
        intended(hasComponent(FacilityCreateActivity.class.getName()));
    }

    @Test
    public void testSwipeRefreshLayout() {
        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Perform swipe refresh
        onView(withId(R.id.swipe_refresh_layout)).perform(click());
    }

    @Test
    public void testAdminModeVisibility() {
        // Enable admin mode
        sharedPreferences.edit().putBoolean("AdminMode", true).apply();

        ActivityScenario<FacilityListActivity> scenario = ActivityScenario.launch(FacilityListActivity.class);

        // Verify create facility button is visible
        onView(withId(R.id.create_facility_button)).check(matches(isDisplayed()));
    }
}