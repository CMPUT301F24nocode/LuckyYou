package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.LoginActivity;
import com.example.projectv2.View.SignUpActivity;
import com.example.projectv2.View.SplashScreenActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for the LoginActivity class.
 * Uses Firestore to fetch and validate user details for testing real-world behavior.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private FirebaseFirestore db;

    @Before
    public void setUp() {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Test navigation to MainActivity when the user exists in Firestore.
     */
    @Test
    public void testUserExistsNavigatesToMainActivity() {
        // Ensure the test userID exists in Firestore
        String testUserID = "1d0e750f99dbaaab";

        // Launch LoginActivity
        Intent intent = new Intent();
        intent.putExtra("deviceID", testUserID);
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        // Verify navigation to SplashScreenActivity for transition
        onView(withText("Finding Best Events for You!")).check(matches(isDisplayed()));

        // Add delay to allow navigation to complete (replace with IdlingResource for robust testing)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify MainActivity is launched
        onView(withId(R.layout.homescreen)).check(matches(isDisplayed()));
    }

    /**
     * Test navigation to SignUpActivity when the user does not exist in Firestore.
     */
    @Test
    public void testUserDoesNotExistNavigatesToSignUpActivity() {
        // Use a random userID that doesn't exist in Firestore
        String invalidUserID = "nonexistent_user";

        // Launch LoginActivity with the invalid userID
        Intent intent = new Intent();
        intent.putExtra("deviceID", invalidUserID);
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        // Verify navigation to SignUpActivity
        onView(withId(R.layout.sign_up)).check(matches(isDisplayed()));
    }

    /**
     * Test navigation to SignUpActivity on sign-up button click.
     */
    @Test
    public void testSignUpButtonNavigatesToSignUpActivity() {
        // Launch LoginActivity
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);

        // Click the sign-up button
        onView(withId(R.id.signup_button)).perform(click());

        // Verify SignUpActivity is displayed
        onView(withId(R.layout.sign_up)).check(matches(isDisplayed()));
    }
}
