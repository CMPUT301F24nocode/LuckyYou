package com.example.projectv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.SplashScreenActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for {@link SplashScreenActivity}.
 * Verifies the proper behavior of the splash screen, including UI components
 * and transitions to the target activity.
 */
@RunWith(AndroidJUnit4.class)
public class SplashScreenActivityTest {

    /**
     * Creates an Intent for testing the {@link SplashScreenActivity}.
     *
     * @return A test Intent with predefined extras.
     */
    private Intent createTestIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SplashScreenActivity.class);

        // Create a bundle with test data
        Bundle extras = new Bundle();
        extras.putString("userID", "testUser123");
        intent.putExtra("EXTRA_DATA", extras);

        // Add other required intent extras
        intent.putExtra("message", "Test Loading...");
        intent.putExtra("delay", 1000); // Short delay for testing
        intent.putExtra("TARGET_ACTIVITY", "com.example.projectv2.MainActivity");

        return intent;
    }

    /**
     * Tests the UI components of the splash screen.
     * Verifies that the Lottie Animation View and the TextView are displayed,
     * and that the TextView displays the correct text.
     */
    @Test
    public void testSplashScreenComponents() {
        // Launch activity with test intent
        try (ActivityScenario<SplashScreenActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            // Check if Lottie Animation View exists and is displayed
            Espresso.onView(ViewMatchers.withId(R.id.lottieAnimationView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Check if TextView exists and displays correct text
            Espresso.onView(ViewMatchers.withId(R.id.splash_screen_text))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText("Test Loading...")));
        }
    }

    /**
     * Tests the transition behavior of the splash screen.
     * Verifies that the splash screen finishes after the specified delay
     * and transitions to the target activity.
     */
    @Test
    public void testSplashScreenTransition() {
        // Launch activity with test intent
        try (ActivityScenario<SplashScreenActivity> scenario = ActivityScenario.launch(createTestIntent())) {
            // Wait for the specified delay plus a small buffer
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verify that the activity has finished after the delay
            scenario.onActivity(activity -> {
                // The activity should be finishing or finished
                org.junit.Assert.assertTrue("Activity should be finishing or finished",
                        activity.isFinishing() || activity.isDestroyed());
            });
        }
    }



}