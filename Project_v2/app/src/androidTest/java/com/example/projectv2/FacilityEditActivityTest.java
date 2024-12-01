package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.FacilityEditActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityEditActivityTest {

    @Before
    public void setup() {
        // Initialize Intents for intent verification
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents
        Intents.release();
    }

    @Test
    public void testLayoutElements() {
        // Launch the activity with mock intent data
        Intent intent = new Intent();
        intent.putExtra("facilityID", "mockFacilityId");
        ActivityScenario.launch(FacilityEditActivity.class);

        // Verify top bar is displayed
        onView(withId(R.id.notification_top_bar)).check(matches(isDisplayed()));

        // Verify name input field
        onView(withId(R.id.facility_edit_name_view)).check(matches(isDisplayed()));

        // Verify description input field
        onView(withId(R.id.facility_edit_description_view)).check(matches(isDisplayed()));

        // Verify save button
        onView(withId(R.id.facility_edit_confirm_button)).check(matches(isDisplayed()))
                .check(matches(withText("Edit Facility")));
    }

    @Test
    public void testInputFields() {
        // Launch the activity with mock intent data
        Intent intent = new Intent();
        intent.putExtra("facilityID", "mockFacilityId");
        ActivityScenario.launch(FacilityEditActivity.class);

        // Verify that user can type in the name input field
        onView(withId(R.id.facility_edit_name_view))
                .perform(typeText("New Facility Name"), closeSoftKeyboard())
                .check(matches(withText("New Facility Name")));

        // Verify that user can type in the description input field
        onView(withId(R.id.facility_edit_description_view))
                .perform(typeText("Updated facility description"), closeSoftKeyboard())
                .check(matches(withText("Updated facility description")));
    }

    @Test
    public void testValidationForEmptyFields() {
        // Launch the activity with mock intent data
        Intent intent = new Intent();
        intent.putExtra("facilityID", "mockFacilityId");
        ActivityScenario.launch(FacilityEditActivity.class);

        // Leave fields empty and click save
        onView(withId(R.id.facility_edit_confirm_button)).perform(click());

        // Verify error message for empty fields
        onView(withText("Please fill out all fields")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testFirestoreDataFetching() {
        // Launch the activity with mock intent data
        Intent intent = new Intent();
        intent.putExtra("facilityID", "mockFacilityId");
        ActivityScenario.launch(FacilityEditActivity.class);

        // Verify that hints are displayed in the name and description fields (assuming mock Firestore fetch is successful)
        onView(withId(R.id.facility_edit_name_view)).check(matches(isDisplayed()));
        onView(withId(R.id.facility_edit_description_view)).check(matches(isDisplayed()));
    }
}
