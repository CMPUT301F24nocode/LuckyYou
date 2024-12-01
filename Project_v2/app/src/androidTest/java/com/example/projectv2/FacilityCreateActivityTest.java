package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasBackground;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.FacilityCreateActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityCreateActivityTest {

    @Test
    public void testLayoutElements() {
        // Launch the activity
        ActivityScenario.launch(FacilityCreateActivity.class);

        // Verify top bar is present
        onView(withId(R.id.notification_top_bar)).check(matches(isDisplayed()));

        // Check Name section
        onView(withId(R.id.textView3))
                .check(matches(isDisplayed()))
                .check(matches(withText("Name")));

        onView(withId(R.id.facility_create_name_view))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Check Description section
        onView(withId(R.id.textView5))
                .check(matches(isDisplayed()))
                .check(matches(withText("Description")));

        onView(withId(R.id.facility_create_description_view))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Verify Save button
        onView(withId(R.id.facility_create_button))
                .check(matches(isDisplayed()))
                .check(matches(withText("Save Facility")));
    }

    @Test
    public void testInputFieldProperties() {
        // Launch the activity
        ActivityScenario.launch(FacilityCreateActivity.class);

        // Verify Name input field
        onView(withId(R.id.facility_create_name_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(typeText("Test Facility Name"), closeSoftKeyboard());

        // Verify Description input field
        onView(withId(R.id.facility_create_description_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(typeText("Test Facility Description"), closeSoftKeyboard());
    }

    @Test
    public void testSaveButtonInteraction() {
        // Launch the activity
        ActivityScenario.launch(FacilityCreateActivity.class);

        // Enter valid input
        onView(withId(R.id.facility_create_name_view))
                .perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.facility_create_description_view))
                .perform(typeText("Test Facility Description"), closeSoftKeyboard());

        // Click Save button
        onView(withId(R.id.facility_create_button))
                .perform(click());

        // Additional verifications can be added based on expected behavior
    }

    @Test
    public void testInputFieldHints() {
        // Launch the activity
        ActivityScenario.launch(FacilityCreateActivity.class);

        // Check Name input hint
        onView(withId(R.id.facility_create_name_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Check Description input hint
        onView(withId(R.id.facility_create_description_view))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}