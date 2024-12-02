package com.example.projectv2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.EventStatusFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class EventStatusFragmentTest {

    private FragmentScenario<EventStatusFragment> scenario;

    @Before
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.initMocks(this);

        // Launch the fragment
        scenario = FragmentScenario.launchInContainer(EventStatusFragment.class);
    }

    @Test
    public void testFragmentCreation() {
        scenario.onFragment(fragment -> {
            assertNotNull("Fragment should not be null", fragment);
            assertNotNull("Fragment view should not be null", fragment.getView());
        });
    }

    @Test
    public void testEventControllerInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventController
            try {
                java.lang.reflect.Field eventControllerField =
                        EventStatusFragment.class.getDeclaredField("eventController");
                eventControllerField.setAccessible(true);
                Object eventController = eventControllerField.get(fragment);
                assertNotNull("EventController should be initialized", eventController);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventController: " + e.getMessage());
            }
        });
    }

    @Test
    public void testEventListInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventList
            try {
                java.lang.reflect.Field eventListField =
                        EventStatusFragment.class.getDeclaredField("eventList");
                eventListField.setAccessible(true);
                ArrayList<?> eventList = (ArrayList<?>) eventListField.get(fragment);
                assertNotNull("Event list should be initialized", eventList);
                assertTrue("Event list should be empty initially", eventList.isEmpty());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventList: " + e.getMessage());
            }
        });
    }
}