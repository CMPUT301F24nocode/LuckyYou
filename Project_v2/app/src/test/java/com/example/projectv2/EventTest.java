package com.example.projectv2;

import android.net.Uri;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.Event;

import org.mockito.Mockito;

public class EventTest {

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

    @Test
    public void testMinimalConstructor() {
        // Test the constructor with only a few parameters
        Event event = new Event("event123", "John Doe", "2024-07-15", "2024-07-16");

        // Verify that the event was created
        assertNotNull(event);
        assertEquals("event123", event.getEventID());
    }

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