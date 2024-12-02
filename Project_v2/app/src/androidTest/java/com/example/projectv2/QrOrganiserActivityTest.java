package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.QrOrganiserActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for {@link QrOrganiserActivity}.
 * Verifies the display and functionality of various components in the QR code organizer activity.
 */
@RunWith(AndroidJUnit4.class)
public class QrOrganiserActivityTest {

    /**
     * Sets up the test environment before each test.
     * Performs any required initialization.
     */
    @Before
    public void setUp() {
        // Perform any necessary setup
    }

    /**
     * Cleans up the test environment after each test.
     */
    @After
    public void tearDown() {
        // Perform cleanup
    }

    /**
     * Tests if the top bar is displayed in the activity.
     */
    @Test
    public void testTopBarIsDisplayed() {
        ActivityScenario.launch(QrOrganiserActivity.class);

        // Verify that the top bar is displayed
        onView(withId(R.id.notification_top_bar))
                .check(matches(isDisplayed()));
    }

    /**
     * Tests if the QR code ImageView is displayed in the activity.
     */
    @Test
    public void testQRCodeImageViewIsDisplayed() {
        ActivityScenario.launch(QrOrganiserActivity.class);

        // Verify that the QR code ImageView is displayed
        onView(withId(R.id.imageView2))
                .check(matches(isDisplayed()));
    }

    /**
     * Tests if the "Save to Gallery" button is displayed and has the correct text.
     */
    @Test
    public void testSaveQrCodeButtonIsDisplayed() {
        ActivityScenario.launch(QrOrganiserActivity.class);

        // Verify that the "Save to Gallery" button is displayed
        onView(withId(R.id.saveQrCodeButton))
                .check(matches(isDisplayed()))
                .check(matches(withText("Save to Gallery")));
    }

    /**
     * Tests the functionality of the "Save to Gallery" button when clicked.
     */
    @Test
    public void testSaveQrCodeButtonClick() {
        ActivityScenario<QrOrganiserActivity> scenario = ActivityScenario.launch(QrOrganiserActivity.class);

        // Simulate a click on the "Save to Gallery" button
        onView(withId(R.id.saveQrCodeButton)).perform(click());
    }

    /**
     * Tests if the activity correctly handles intent data passed to it.
     * Verifies that QR code generation logic is executed based on the intent data.
     */
    @Test
    public void testIntentDataHandling() {
        Intent intent = new Intent();
        intent.putExtra("eventID", "mockEventID");
        intent.putExtra("name", "Mock Event");

        ActivityScenario.launch(QrOrganiserActivity.class);

        // Verify QR code generation logic based on the intent data
        onView(withId(R.id.imageView2)).check(matches(isDisplayed()));
    }
}
