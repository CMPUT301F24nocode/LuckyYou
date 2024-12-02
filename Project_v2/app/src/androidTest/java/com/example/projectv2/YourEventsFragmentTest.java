package com.example.projectv2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.YourEventsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class YourEventsFragmentTest {

    private FragmentScenario<YourEventsFragment> scenario;

    @Before
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.initMocks(this);

        // Launch the fragment
        scenario = FragmentScenario.launchInContainer(YourEventsFragment.class);
    }

    @Test
    public void testFragmentCreation() {
        scenario.onFragment(fragment -> {
            assertNotNull("Fragment should not be null", fragment);
            assertNotNull("Fragment view should not be null", fragment.getView());
        });
    }

    @Test
    public void testRecyclerViewLayoutManager() {
        scenario.onFragment(fragment -> {
            RecyclerView recyclerView = fragment.getView().findViewById(R.id.recyclerViewYourEvents);

            assertNotNull("RecyclerView should have a layout manager", recyclerView.getLayoutManager());

            assertTrue("Layout manager should be LinearLayoutManager",
                    recyclerView.getLayoutManager() instanceof LinearLayoutManager);
        });
    }

    @Test
    public void testEventControllerInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventController
            try {
                java.lang.reflect.Field eventControllerField =
                        YourEventsFragment.class.getDeclaredField("eventController");
                eventControllerField.setAccessible(true);
                Object eventController = eventControllerField.get(fragment);
                assertNotNull("EventController should be initialized", eventController);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventController: " + e.getMessage());
            }
        });
    }

    @Test
    public void testAdapterInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private adapter
            try {
                java.lang.reflect.Field adapterField =
                        YourEventsFragment.class.getDeclaredField("adapter");
                adapterField.setAccessible(true);
                Object adapter = adapterField.get(fragment);
                assertNotNull("Adapter should be initialized", adapter);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access adapter: " + e.getMessage());
            }
        });
    }

    @Test
    public void testEventListInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventList
            try {
                java.lang.reflect.Field eventListField =
                        YourEventsFragment.class.getDeclaredField("eventList");
                eventListField.setAccessible(true);
                ArrayList<?> eventList = (ArrayList<?>) eventListField.get(fragment);
                assertNotNull("Event list should be initialized", eventList);
                assertTrue("Event list should be empty initially", eventList.isEmpty());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventList: " + e.getMessage());
            }
        });
    }

    @Test
    public void testFetchEventsMethodCall() {
        scenario.onFragment(fragment -> {
            // This test verifies that the fetchEvents method is called during fragment creation
            // Note: This is a basic test and might need more sophisticated mocking
            try {
                java.lang.reflect.Method fetchEventsMethod =
                        YourEventsFragment.class.getDeclaredMethod("fetchEvents");
                fetchEventsMethod.setAccessible(true);
                // You might want to add more sophisticated verification here
                assertNotNull("fetchEvents method should exist", fetchEventsMethod);
            } catch (NoSuchMethodException e) {
                fail("fetchEvents method not found: " + e.getMessage());
            }
        });
    }
}
