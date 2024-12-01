package com.example.projectv2;

import android.net.Uri;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.Event;

/**
 * Unit test for the Event model class.
 */
public class EventTest {

    private Event event;
    private String eventID = "E123";
    private String owner = "Owner1";
    private String name = "Music Fest";
    private String detail = "A large outdoor concert.";
    private String rules = "No outside food allowed";
    private String deadline = "2023-12-01";
    private String startDate = "2023-12-05";
    private String ticketPrice = "50";
    private Uri imageUri = Uri.parse("https://example.com/image.jpg");
    private String facility = "Main Hall";

    @Before
    public void setUp() {
        // Initialize the Event object with test data
        event = new Event(eventID, owner, name, detail, rules, deadline, startDate, ticketPrice, imageUri, facility);
    }

    /**
     * Test that the Event constructor correctly initializes all fields.
     */
    @Test
    public void testEventConstructor() {
        assertEquals(eventID, event.getEventID());
        assertEquals(owner, event.getOwner());
        assertEquals(name, event.getName());
        assertEquals(detail, event.getDetail());
        assertEquals(rules, event.getRules());
        assertEquals(deadline, event.getDeadline());
        assertEquals(startDate, event.getStartDate());
        assertEquals(ticketPrice, event.getTicketPrice());
        assertEquals(imageUri, event.getImageUri());
        assertEquals(facility, event.getFacility());
    }

    /**
     * Test the getter methods to verify that they return the correct values.
     */
    @Test
    public void testGetters() {
        assertEquals("Owner getter failed", owner, event.getOwner());
        assertEquals("Name getter failed", name, event.getName());
        assertEquals("Detail getter failed", detail, event.getDetail());
        assertEquals("Rules getter failed", rules, event.getRules());
        assertEquals("Deadline getter failed", deadline, event.getDeadline());
        assertEquals("Start Date getter failed", startDate, event.getStartDate());
        assertEquals("Ticket Price getter failed", ticketPrice, event.getTicketPrice());
        assertEquals("Image URI getter failed", imageUri, event.getImageUri());
        assertEquals("Facility getter failed", facility, event.getFacility());
    }

    /**
     * Test the setEventID method to verify that it correctly updates the eventID.
     */
    @Test
    public void testSetEventID() {
        String newEventID = "E456";
        event.setEventID(newEventID);
        assertEquals("EventID setter failed", newEventID, event.getEventID());
    }
}
