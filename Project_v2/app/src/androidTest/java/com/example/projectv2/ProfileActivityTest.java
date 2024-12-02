package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectv2.View.ProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testProfileFieldsVisibility() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ProfileActivity.class);
        intent.putExtra("userID", "9a11accac88317b1");
        intent.putExtra("adminView", false);

        ActivityScenario.launch(intent);

        // Verify profile fields are visible
        onView(withId(R.id.profile_name_box)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_email_box)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_phone_box)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_pic_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileButtonFunctionality() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ProfileActivity.class);
        intent.putExtra("userID", "9a11accac88317b1");
        intent.putExtra("adminView", false);

        ActivityScenario.launch(intent);

        // Enter new details and click the edit profile button
        onView(withId(R.id.profile_name_box)).perform(replaceText("New Name"));
        onView(withId(R.id.profile_email_box)).perform(replaceText("new.email@example.com"));
        onView(withId(R.id.profile_phone_box)).perform(replaceText("1234567890"));

        onView(withId(R.id.profile_edit_button)).perform(click());

        // Verify Snackbar or confirmation message
        onView(withText("Profile Updated Successfully!")).check(matches(isDisplayed()));
    }


    @Test
    public void testSwipeRefreshFunctionality() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ProfileActivity.class);
        intent.putExtra("userID", "9a11accac88317b1");
        intent.putExtra("adminView", false);

        ActivityScenario.launch(intent);

        // Perform swipe-to-refresh action
        onView(withId(R.id.profile_swipe_refresh)).perform(ViewActions.swipeDown());

        // Verify the profile image view is still displayed
        onView(withId(R.id.profile_pic_view)).check(matches(isDisplayed()));
    }
}
