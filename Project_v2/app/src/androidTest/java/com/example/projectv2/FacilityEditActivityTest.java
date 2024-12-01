package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.FacilityEditActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class FacilityEditActivityTest {

    // Mock facility ID for testing
    private static final String TEST_FACILITY_ID = "test_facility_123";

    // Rule to launch the activity with a test facility ID
    @Rule
    public ActivityScenarioRule<FacilityEditActivity> activityRule =
            new ActivityScenarioRule<>(createTestIntent());

    /**
     * Create an intent with a test facility ID
     * @return Intent with test facility ID
     */
    private static Intent createTestIntent() {
        Intent intent = new Intent();
        intent.putExtra("facilityID", TEST_FACILITY_ID);
        return intent;
    }

    @Before
    public void setup() {
        // Ensure the test facility exists in Firestore before running tests
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("facilities").document(TEST_FACILITY_ID)
                .set(createTestFacilityData())
                .addOnFailureListener(e -> {
                    throw new RuntimeException("Could not set up test facility in Firestore: " + e.getMessage());
                });
    }

    /**
     * Test that all UI elements are displayed correctly
     */
    @Test
    public void testUIElementsDisplayed() {
        // Check top bar is displayed
        onView(withId(R.id.notification_top_bar))
                .check(matches(isDisplayed()));

        // Check name label and edit text
        onView(withId(R.id.textView3))
                .check(matches(isDisplayed()))
                .check(matches(withText("Name")));
        onView(withId(R.id.facility_edit_name_view))
                .check(matches(isDisplayed()));

        // Check description label and edit text
        onView(withId(R.id.textView5))
                .check(matches(isDisplayed()))
                .check(matches(withText("Description")));
        onView(withId(R.id.facility_edit_description_view))
                .check(matches(isDisplayed()));

        // Check confirm button
        onView(withId(R.id.facility_edit_confirm_button))
                .check(matches(isDisplayed()))
                .check(matches(withText("Edit Facility")));
    }

    /**
     * Test initial hints match string resources
     */
    @Test
    public void testInitialHints() {
        // Check name edit text hint
        onView(withId(R.id.facility_edit_name_view))
                .check(matches(withHint(R.string.create_facility_name_text)));

        // Check description edit text hint
        onView(withId(R.id.facility_edit_description_view))
                .check(matches(withHint(R.string.create_facility_details_text)));
    }

    /**
     * Test successful facility update
     */
    @Test
    public void testSuccessfulFacilityUpdate() {
        // Type new name and description
        onView(withId(R.id.facility_edit_name_view))
                .perform(clearText(), typeText("Updated Facility Name"));

        onView(withId(R.id.facility_edit_description_view))
                .perform(clearText(), typeText("Updated facility description"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click save button
        onView(withId(R.id.facility_edit_confirm_button))
                .perform(click());

        // Check for success toast
        onView(withText("Facility updated successfully"))
                .check(matches(isDisplayed()));
    }

    /**
     * Test validation - empty fields
     */
    @Test
    public void testEmptyFieldValidation() {
        // Clear both fields
        onView(withId(R.id.facility_edit_name_view))
                .perform(clearText());

        onView(withId(R.id.facility_edit_description_view))
                .perform(clearText());

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click save button
        onView(withId(R.id.facility_edit_confirm_button))
                .perform(click());

        // Check for validation toast
        onView(withText("Please fill out all fields"))
                .check(matches(isDisplayed()));
    }

    /**
     * Helper method to create test facility data
     * @return Map of test facility data
     */
    private Map<String, Object> createTestFacilityData() {
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", "Test Facility");
        facilityData.put("description", "Test facility description");
        return facilityData;
    }
}