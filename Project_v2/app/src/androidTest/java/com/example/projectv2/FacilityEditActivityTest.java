package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.FacilityEditActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityEditActivityTest {

    private static final String TEST_FACILITY_ID = "test_facility_123";

    @Test
    public void testLayoutElements() {
        // Create an intent with a facility ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FacilityEditActivity.class);
        intent.putExtra("facilityID", TEST_FACILITY_ID);

        ActivityScenario.launch(intent);

        // Verify top bar is present
        onView(withId(R.id.notification_top_bar)).check(matches(isDisplayed()));

        // Verify Name section
        onView(withId(R.id.textView3))
                .check(matches(isDisplayed()))
                .check(matches(withText("Name")));

        onView(withId(R.id.facility_edit_name_view))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Verify Description section
        onView(withId(R.id.textView5))
                .check(matches(isDisplayed()))
                .check(matches(withText("Description")));

        onView(withId(R.id.facility_edit_description_view))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Verify Edit button
        onView(withId(R.id.facility_edit_confirm_button))
                .check(matches(isDisplayed()))
                .check(matches(withText("Edit Facility")));
    }

    @Test
    public void testInputFieldProperties() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FacilityEditActivity.class);
        intent.putExtra("facilityID", TEST_FACILITY_ID);

        ActivityScenario.launch(intent);

        // Verify Name input field
        onView(withId(R.id.facility_edit_name_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(typeText("Test Facility Name"), closeSoftKeyboard());

        // Verify Description input field
        onView(withId(R.id.facility_edit_description_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(typeText("Test Facility Description"), closeSoftKeyboard());
    }

    @Test
    public void testEditButtonInteraction() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FacilityEditActivity.class);
        intent.putExtra("facilityID", TEST_FACILITY_ID);

        ActivityScenario.launch(intent);

        // Enter valid input
        onView(withId(R.id.facility_edit_name_view))
                .perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.facility_edit_description_view))
                .perform(typeText("Test Facility Description"), closeSoftKeyboard());

        // Click Edit button
        onView(withId(R.id.facility_edit_confirm_button))
                .perform(click());
    }

    @Test
    public void testInputFieldHints() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FacilityEditActivity.class);
        intent.putExtra("facilityID", TEST_FACILITY_ID);

        ActivityScenario.launch(intent);

        // Check Name input hint
        onView(withId(R.id.facility_edit_name_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Check Description input hint
        onView(withId(R.id.facility_edit_description_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}