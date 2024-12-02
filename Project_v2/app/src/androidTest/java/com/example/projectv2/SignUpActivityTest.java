package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule = new ActivityScenarioRule<>(SignUpActivity.class);

    @Test
    public void testEmailField() {
        onView(withId(R.id.signup_email))
                .perform(replaceText("test@example.com"))
                .check(matches(withText("test@example.com")));
    }

    @Test
    public void testFirstNameField() {
        onView(withId(R.id.signup_firstname))
                .perform(replaceText("John"))
                .check(matches(withText("John")));
    }

    @Test
    public void testLastNameField() {
        onView(withId(R.id.signup_secondname))
                .perform(replaceText("Doe"))
                .check(matches(withText("Doe")));
    }

    @Test
    public void testPhoneNumberField() {
        onView(withId(R.id.signup_phonenumber))
                .perform(replaceText("1234567890"))
                .check(matches(withText("1234567890")));
    }

    @Test
    public void testSignUpButtonWithoutPhoneNumber() {
        onView(withId(R.id.signup_email)).perform(replaceText("test@example.com"));
        onView(withId(R.id.signup_firstname)).perform(replaceText("John"));
        onView(withId(R.id.signup_secondname)).perform(replaceText("Doe"));
        onView(withId(R.id.signup_button)).perform(click());

        // Check if MainActivity is started (mock Intent validation can be used here)
    }

    @Test
    public void testSignUpButtonWithInvalidPhoneNumber() {
        onView(withId(R.id.signup_email)).perform(replaceText("test@example.com"));
        onView(withId(R.id.signup_firstname)).perform(replaceText("John"));
        onView(withId(R.id.signup_secondname)).perform(replaceText("Doe"));
        onView(withId(R.id.signup_phonenumber)).perform(replaceText("abcd"));
        onView(withId(R.id.signup_button)).perform(click());

        // Check if the error is displayed on the phone number field
        onView(withId(R.id.signup_phonenumber))
                .check(matches(withText("abcd")));
    }
}
