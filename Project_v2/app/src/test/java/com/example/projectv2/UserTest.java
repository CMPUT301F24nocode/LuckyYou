package com.example.projectv2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for the User model class.
 */
public class UserTest {

    private User userWithDefaultConstructor;
    private User userWithPartialConstructor;
    private User userWithFullConstructor;

    @Before
    public void setUp() {
        // Initialize User with default constructor
        userWithDefaultConstructor = new User();

        // Initialize User with partial constructor (non-admin by default)
        userWithPartialConstructor = new User("test@example.com", "John", "Doe", "1234567890L", "deviceID123");

        // Initialize User with full constructor
        userWithFullConstructor = new User(true, false, "admin@example.com", "Admin", "User", true, true, "9876543210", "deviceID456");
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        assertFalse(userWithDefaultConstructor.isAdmin());
        assertNull(userWithDefaultConstructor.getEmail());
        assertNull(userWithDefaultConstructor.getName());
        assertEquals("", userWithDefaultConstructor.getProfileImage());
        assertTrue(userWithDefaultConstructor.getAdminNotifList().isEmpty());
        assertTrue(userWithDefaultConstructor.getOrganizerNotifList().isEmpty());
    }

    /**
     * Test partial constructor.
     */
    @Test
    public void testPartialConstructor() {
        assertFalse(userWithPartialConstructor.isAdmin());
        assertEquals("test@example.com", userWithPartialConstructor.getEmail());
        assertEquals("deviceID123", userWithPartialConstructor.getDeviceID());
        assertEquals(1234567890L, userWithPartialConstructor.getPhoneNumber());
    }

    /**
     * Test full constructor.
     */
    @Test
    public void testFullConstructor() {
        assertTrue(userWithFullConstructor.isAdmin());
        assertFalse(userWithFullConstructor.isAdminNotif());
        assertEquals("admin@example.com", userWithFullConstructor.getEmail());
//        assertEquals("Admin", userWithFullConstructor.getFirstName());
        assertEquals("deviceID456", userWithFullConstructor.getDeviceID());
        assertTrue(userWithFullConstructor.isOrganizer());
        assertEquals(9876543210L, userWithFullConstructor.getPhoneNumber());
    }

    /**
     * Test getters for all fields.
     */
    @Test
    public void testGetters() {
        userWithPartialConstructor.setName("John Doe");
        assertEquals("John Doe", userWithPartialConstructor.getName());
        assertFalse(userWithPartialConstructor.isAdmin());
        assertEquals("test@example.com", userWithPartialConstructor.getEmail());
        assertEquals("deviceID123", userWithPartialConstructor.getDeviceID());
        assertEquals(1234567890L, userWithPartialConstructor.getPhoneNumber());
    }

    /**
     * Test setters for all fields.
     */
    @Test
    public void testSetters() {
        userWithDefaultConstructor.setAdmin(true);
        userWithDefaultConstructor.setEmail("new@example.com");
        userWithDefaultConstructor.setName("New Name");
        userWithDefaultConstructor.setPhoneNumber("5555555555");
        userWithDefaultConstructor.setDeviceID("newDeviceID");
        userWithDefaultConstructor.setProfileImage("newProfileImage");

        assertTrue(userWithDefaultConstructor.isAdmin());
        assertEquals("new@example.com", userWithDefaultConstructor.getEmail());
        assertEquals("New Name", userWithDefaultConstructor.getName());
        assertEquals(5555555555L, userWithDefaultConstructor.getPhoneNumber());
        assertEquals("newDeviceID", userWithDefaultConstructor.getDeviceID());
        assertEquals("newProfileImage", userWithDefaultConstructor.getProfileImage());
    }

    /**
     * Test setting notification lists.
     */
    @Test
    public void testSetNotificationLists() {
        ArrayList<Map<String, Object>> adminNotifications = new ArrayList<>();
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", "Admin Notification");
        adminNotifications.add(notification);

        userWithDefaultConstructor.setAdminNotifList(adminNotifications);
        assertEquals(1, userWithDefaultConstructor.getAdminNotifList().size());
        assertEquals("Admin Notification", userWithDefaultConstructor.getAdminNotifList().get(0).get("message"));
    }
}
