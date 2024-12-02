package com.example.projectv2;

import android.net.Uri;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.Event;

import org.mockito.Mockito;

/**
 * Unit tests for the {@link Event} class.
 *
 * <p>This class tests the behavior and functionality of the Event model class,
 * ensuring that constructors, getters, and setters function as expected.</p>
 */
public class EventTest {

    /**
     * Tests the full constructor of the {@link Event} class.
     * Verifies that all attributes are properly initialized when provided.
     */
    @Test
    public void testFullConstructor() {
        // Create a mock Uri
        Uri mockUri = Mockito.mock(Uri.class);

        // Create an Event with all parameters
        Event event = new Event(
                "event123",
                "John Doe",
                "Summer Festival",
                "A fun summer event",
                "No outside food",
                "2024-07-01",
                "2024-07-15",
                "50.00",
                mockUri,
                "Community Center"
        );

        // Verify all properties are set correctly
        assertEquals("event123", event.getEventID());
        assertEquals("John Doe", event.getOwner());
        assertEquals("Summer Festival", event.getName());
        assertEquals("A fun summer event", event.getDetail());
        assertEquals("No outside food", event.getRules());
        assertEquals("2024-07-01", event.getDeadline());
        assertEquals("2024-07-15", event.getStartDate());
        assertEquals("50.00", event.getTicketPrice());
        assertEquals(mockUri, event.getImageUri());
        assertEquals("Community Center", event.getFacility());
    }

    /**
     * Tests the setter for the event ID in the {@link Event} class.
     * Verifies that the event ID can be updated and retrieved correctly.
     */
    @Test
    public void testSetEventID() {
        Event event = new Event(
                "event123",
                "John Doe",
                "Summer Festival",
                "A fun summer event",
                "No outside food",
                "2024-07-01",
                "2024-07-15",
                "50.00",
                null,
                "Community Center"
        );

        // Change the event ID
        event.setEventID("newEvent456");
        assertEquals("newEvent456", event.getEventID());
    }

    /**
     * Tests the minimal constructor of the {@link Event} class.
     * Verifies that an Event object can be created with only essential attributes.
     */
    @Test
    public void testMinimalConstructor() {
        // Test the constructor with only a few parameters
        Event event = new Event("event123", "John Doe", "2024-07-15", "2024-07-16");

        // Verify that the event was created
        assertNotNull(event);
        assertEquals("event123", event.getEventID());
    }

    /**
     * Tests how the {@link Event} class handles null values for attributes.
     * Ensures that attributes can be null without causing errors.
     */
    @Test
    public void testNullValues() {
        // Create an event with null values
        Event event = new Event(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Verify that null values are handled correctly
        assertNull(event.getEventID());
        assertNull(event.getOwner());
        assertNull(event.getName());
        assertNull(event.getDetail());
        assertNull(event.getRules());
        assertNull(event.getDeadline());
        assertNull(event.getStartDate());
        assertNull(event.getTicketPrice());
        assertNull(event.getImageUri());
        assertNull(event.getFacility());
    }

    /**
     * Tests all getter methods of the {@link Event} class.
     * Verifies that all attributes can be retrieved correctly after initialization.
     */
    @Test
    public void testGetters() {
        Uri mockUri = Mockito.mock(Uri.class);

        Event event = new Event(
                "event123",
                "John Doe",
                "Summer Festival",
                "A fun summer event",
                "No outside food",
                "2024-07-01",
                "2024-07-15",
                "50.00",
                mockUri,
                "Community Center"
        );

        // Systematically test each getter
        assertEquals("event123", event.getEventID());
        assertEquals("John Doe", event.getOwner());
        assertEquals("Summer Festival", event.getName());
        assertEquals("A fun summer event", event.getDetail());
        assertEquals("No outside food", event.getRules());
        assertEquals("2024-07-01", event.getDeadline());
        assertEquals("2024-07-15", event.getStartDate());
        assertEquals("50.00", event.getTicketPrice());
        assertEquals(mockUri, event.getImageUri());
        assertEquals("Community Center", event.getFacility());
    }
}
