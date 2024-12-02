package com.example.projectv2;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.projectv2.View.LoginActivity;
import com.example.projectv2.View.SignUpActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UI Tests for LoginActivity
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private static final String TEST_DEVICE_ID = "1d0e750f99dbaaab";

    @Before
    public void setUp() {
        // Initialize Intents for intent verification
        Intents.init();

        // Get the context and initialize Firebase
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(context);
    }

    @After
    public void tearDown() {
        // Release Intents after tests
        Intents.release();
    }

    /**
     * Test scenario for existing user
     */
    @Test
    public void testExistingUserLogin() throws InterruptedException {
        // Create a CountDownLatch to synchronize the asynchronous test
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);

        db.collection("Users")
                .document(TEST_DEVICE_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Use runOnUiThread to launch the activity safely
                        try {
                            runOnUiThread(() -> {
                                try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                                    intended(hasComponent(MainActivity.class.getName()));
                                }
                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    latch.countDown();
                });

        // Wait for the async operation to complete
        latch.await(5, TimeUnit.SECONDS);
    }


    /**
     * Test scenario for new user (no existing document)
     */
    @Test
    public void testNewUserSignUp() throws InterruptedException {
        // Ensure no user exists for this device ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);

        db.collection("Users")
                .document(TEST_DEVICE_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().exists()) {
                        // Use runOnUiThread to launch the activity safely
                        try {
                            runOnUiThread(() -> {
                                try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
                                    onView(withId(R.id.signup_button)).check(matches(isDisplayed()));
                                    // Click on SignUp button
                                    onView(withId(R.id.signup_button)).perform(click());

                                    // Verify SignUpActivity is started
                                    intended(hasComponent(SignUpActivity.class.getName()));

                                }
                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    latch.countDown();
                });

        // Wait for the async operation to complete
        latch.await(5, TimeUnit.SECONDS);

    }
}