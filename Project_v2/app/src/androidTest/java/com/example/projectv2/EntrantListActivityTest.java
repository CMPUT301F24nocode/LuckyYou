package com.example.projectv2;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.EntrantListActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
/**
 * <p>
 * This class contains UI tests for the EntrantListActivity. It verifies the correct display and
 * functionality of critical UI components such as RecyclerView, Spinner, and Buttons. The tests
 * also mock Firestore interactions to simulate the behavior of fetching event data, including
 * entrant lists for different categories (e.g., Waiting, Selected, Cancelled, and Attendee).
 * </p>

 */
@RunWith(AndroidJUnit4.class)
public class EntrantListActivityTest {

    private FirebaseFirestore mockFirestore;

    @Before
    public void setUp() {
        // Mock Firestore and its components
        mockFirestore = mock(FirebaseFirestore.class);
        CollectionReference mockEventCollection = mock(CollectionReference.class);
        DocumentReference mockEventDocument = mock(DocumentReference.class);
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        DocumentSnapshot mockSnapshot = mock(DocumentSnapshot.class);

        // Set up mock Firestore behavior
        when(mockFirestore.collection("events")).thenReturn(mockEventCollection);
        when(mockEventCollection.document("3549864")).thenReturn(mockEventDocument);
        when(mockEventDocument.get()).thenReturn(mockTask);

        // Mock task result
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);

        // Mock snapshot data
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.get("entrantList.Waiting")).thenReturn(Arrays.asList("user1", "user2"));
        when(mockSnapshot.get("entrantList.Selected")).thenReturn(Collections.emptyList());
        when(mockSnapshot.get("entrantList.Cancelled")).thenReturn(Collections.emptyList());
        when(mockSnapshot.get("entrantList.Attendee")).thenReturn(Collections.emptyList());
    }

    @After
    public void tearDown() {
        // Clean up resources if needed
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantListActivity.class);
        intent.putExtra("eventId", "3549864");

        try (ActivityScenario<EntrantListActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> activity.db = mockFirestore);

            // Verify RecyclerView is displayed
            Espresso.onView(ViewMatchers.withId(R.id.entrantRecyclerView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testSpinnerIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantListActivity.class);
        intent.putExtra("eventId", "3549864");

        try (ActivityScenario<EntrantListActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> activity.db = mockFirestore);

            // Verify Spinner is displayed
            Espresso.onView(ViewMatchers.withId(R.id.entrant_list_dropdown))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testButtonsAreDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantListActivity.class);
        intent.putExtra("eventId", "3549864");

        try (ActivityScenario<EntrantListActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> activity.db = mockFirestore);

            // Verify Buttons are displayed
            Espresso.onView(ViewMatchers.withId(R.id.send_notification_toAll_button))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            Espresso.onView(ViewMatchers.withId(R.id.cancel_all_entrants_button))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}
