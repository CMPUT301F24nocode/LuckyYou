package com.example.projectv2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.AvailableEventsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class AvailableEventsFragmentTest {

    private FragmentScenario<AvailableEventsFragment> scenario;

    @Before
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.initMocks(this);

        // Launch the fragment
        scenario = FragmentScenario.launchInContainer(AvailableEventsFragment.class);
    }

    @Test
    public void testFragmentCreation() {
        // Verify the fragment is created successfully
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
            assertNotNull(fragment.getView());
        });
    }

    @Test
    public void testEventControllerInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventController
            try {
                java.lang.reflect.Field eventControllerField =
                        AvailableEventsFragment.class.getDeclaredField("eventController");
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
                        AvailableEventsFragment.class.getDeclaredField("adapter");
                adapterField.setAccessible(true);
                Object adapter = adapterField.get(fragment);
                assertNotNull("Adapter should be initialized", adapter);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access adapter: " + e.getMessage());
            }
        });
    }

    // Optional: If you want to test refresh functionality
    @Test
    public void testRefreshEventsMethod() {
        scenario.onFragment(fragment -> {
            // Spy on the method to verify it's called
            fragment.refreshEventsFromFirestore();
        });
    }
}