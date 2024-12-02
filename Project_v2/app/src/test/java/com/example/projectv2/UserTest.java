package com.example.projectv2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import com.example.projectv2.Model.User;

public class UserTest {

    @Test
    public void testDefaultConstructor() {
        User user = new User();

        assertFalse(user.isAdmin());
        assertFalse(user.isOrganizer());
        assertEquals("", user.getProfileImage());
        assertEquals(0.0, user.getLatitude(),0.0001);
        assertEquals(0.0, user.getLongitude(),0.0001);
        assertNotNull(user.getAdminNotifList());
        assertNotNull(user.getOrganizerNotifList());
    }

    @Test
    public void testPartialConstructor() {
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String name=firstName+" "+lastName;
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

    @Test
    public void testSettersAndGetters() {
        User user = new User();

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

        ArrayList<Map<String, Object>> adminNotifList = new ArrayList<>();
        Map<String, Object> adminNotif = new HashMap<>();
        adminNotif.put("message", "Test Admin Notification");
        adminNotifList.add(adminNotif);
        user.setAdminNotifList(adminNotifList);

        ArrayList<Map<String, Object>> organizerNotifList = new ArrayList<>();
        Map<String, Object> organizerNotif = new HashMap<>();
        organizerNotif.put("message", "Test Organizer Notification");
        organizerNotifList.add(organizerNotif);
        user.setOrganizerNotifList(organizerNotifList);

        assertTrue(user.isAdmin());
        assertTrue(user.isAdminNotif());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertTrue(user.isOrganizer());
        assertTrue(user.isOrganizerNotif());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("http://example.com/profile.jpg", user.getProfileImage());
        assertEquals("device789", user.getDeviceID());
        assertEquals(51.5074, user.getLatitude(),0.0001);
        assertEquals(0.1278, user.getLongitude(),0.0001);
        assertEquals(adminNotifList, user.getAdminNotifList());
        assertEquals(organizerNotifList, user.getOrganizerNotifList());
    }
}
