package com.example.projectv2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import com.example.projectv2.Model.User;

/**
 * Unit tests for the {@link User} class.
 *
 * <p>This class tests the behavior and functionality of the User model class,
 * ensuring that constructors, getters, setters, and field initializations
 * function as expected.</p>
 */
public class UserTest {

    /**
     * Tests the default constructor of the {@link User} class.
     * Verifies that all attributes are initialized to their default values.
     */
    @Test
    public void testDefaultConstructor() {
        User user = new User();

        assertFalse(user.isAdmin());
        assertFalse(user.isOrganizer());
        assertEquals("", user.getProfileImage());
        assertEquals(0.0, user.getLatitude(), 0.0001);
        assertEquals(0.0, user.getLongitude(), 0.0001);
        assertNotNull(user.getAdminNotifList());
        assertNotNull(user.getOrganizerNotifList());
    }

    /**
     * Tests the partial constructor of the {@link User} class.
     * Verifies that attributes provided as parameters are properly initialized,
     * and other attributes are set to their default values.
     */
    @Test
    public void testPartialConstructor() {
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String name = firstName + " " + lastName;
        String phoneNumber = "1234567890";
        String deviceID = "device123";

        User user = new User(email, firstName, lastName, phoneNumber, deviceID);

        assertFalse(user.isAdmin());
        assertTrue(user.isOrganizerNotif());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(deviceID, user.getDeviceID());
    }

    /**
     * Tests the full constructor of the {@link User} class.
     * Verifies that all attributes are initialized with the specified parameters.
     */
    @Test
    public void testFullConstructor() {
        User user = new User(
                true, true, "admin@example.com", "Admin", "User",
                true, true, "9876543210", "device456"
        );

        assertTrue(user.isAdmin());
        assertTrue(user.isAdminNotif());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("Admin User", user.getName());
        assertTrue(user.isOrganizer());
        assertTrue(user.isOrganizerNotif());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals("device456", user.getDeviceID());
    }

    /**
     * Tests the setters and getters of the {@link User} class.
     * Verifies that attributes can be updated and retrieved correctly.
     */
    @Test
    public void testSettersAndGetters() {
        User user = new User();

        // Set attributes
        user.setAdmin(true);
        user.setAdminNotif(true);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setOrganizer(true);
        user.setOrganizerNotif(true);
        user.setPhoneNumber("1234567890");
        user.setProfileImage("http://example.com/profile.jpg");
        user.setDeviceID("device789");
        user.setLatitude(51.5074);
        user.setLongitude(0.1278);

        // Add admin notification
        ArrayList<Map<String, Object>> adminNotifList = new ArrayList<>();
        Map<String, Object> adminNotif = new HashMap<>();
        adminNotif.put("message", "Test Admin Notification");
        adminNotifList.add(adminNotif);
        user.setAdminNotifList(adminNotifList);

        // Add organizer notification
        ArrayList<Map<String, Object>> organizerNotifList = new ArrayList<>();
        Map<String, Object> organizerNotif = new HashMap<>();
        organizerNotif.put("message", "Test Organizer Notification");
        organizerNotifList.add(organizerNotif);
        user.setOrganizerNotifList(organizerNotifList);

        // Verify attributes
        assertTrue(user.isAdmin());
        assertTrue(user.isAdminNotif());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertTrue(user.isOrganizer());
        assertTrue(user.isOrganizerNotif());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("http://example.com/profile.jpg", user.getProfileImage());
        assertEquals("device789", user.getDeviceID());
        assertEquals(51.5074, user.getLatitude(), 0.0001);
        assertEquals(0.1278, user.getLongitude(), 0.0001);
        assertEquals(adminNotifList, user.getAdminNotifList());
        assertEquals(organizerNotifList, user.getOrganizerNotifList());
    }
}
