package com.example.projectv2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Unit test for the Notification model class.
 */
public class NotificationTest {

    private Notification notificationWithCurrentTime;
    private Notification notificationWithSpecifiedTime;
    private String sendTo = "User123";
    private String content = "Event update";
    private boolean isOrganiser = true;
    private boolean isAdmin = false;
    private String specifiedTime = "10-11-2024 15:45:30";

    @Before
    public void setUp() {
        // Initialize Notification with current time
        notificationWithCurrentTime = new Notification(sendTo, content, isOrganiser, isAdmin);

        // Initialize Notification with specified time
        notificationWithSpecifiedTime = new Notification(sendTo, content, specifiedTime, isOrganiser, isAdmin);
    }

    /**
     * Test the constructor with current time, verifying fields and time format.
     */
    @Test
    public void testNotificationConstructorWithCurrentTime() {
        assertEquals(sendTo, notificationWithCurrentTime.getSendTo());
        assertEquals(content, notificationWithCurrentTime.getContent());
        assertEquals(isOrganiser, notificationWithCurrentTime.isOrganiser());
        assertEquals(isAdmin, notificationWithCurrentTime.isAdmin());

        // Check if timeSent is in the correct format and is close to the current time
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        assertEquals("Time format or current time issue", currentTime, notificationWithCurrentTime.getTimeSent());
    }

    /**
     * Test the constructor with a specified time, verifying all fields are set correctly.
     */
    @Test
    public void testNotificationConstructorWithSpecifiedTime() {
        assertEquals(sendTo, notificationWithSpecifiedTime.getSendTo());
        assertEquals(content, notificationWithSpecifiedTime.getContent());
        assertEquals(specifiedTime, notificationWithSpecifiedTime.getTimeSent());
        assertEquals(isOrganiser, notificationWithSpecifiedTime.isOrganiser());
        assertEquals(isAdmin, notificationWithSpecifiedTime.isAdmin());
    }

    /**
     * Test the getSendTo method to verify it returns the correct recipient.
     */
    @Test
    public void testGetSendTo() {
        assertEquals("GetSendTo method failed", sendTo, notificationWithSpecifiedTime.getSendTo());
    }

    /**
     * Test the getContent method to verify it returns the correct content.
     */
    @Test
    public void testGetContent() {
        assertEquals("GetContent method failed", content, notificationWithSpecifiedTime.getContent());
    }

    /**
     * Test the getTimeSent method to verify it returns the correct time.
     */
    @Test
    public void testGetTimeSent() {
        assertEquals("GetTimeSent method failed", specifiedTime, notificationWithSpecifiedTime.getTimeSent());
    }

    /**
     * Test the isOrganiser method to verify it returns the correct flag.
     */
    @Test
    public void testIsOrganiser() {
        assertTrue("IsOrganiser method failed", notificationWithSpecifiedTime.isOrganiser());
    }

    /**
     * Test the isAdmin method to verify it returns the correct flag.
     */
    @Test
    public void testIsAdmin() {
        assertFalse("IsAdmin method failed", notificationWithSpecifiedTime.isAdmin());
    }
}
