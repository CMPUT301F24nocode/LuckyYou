/**
 * Model class representing a user. Each user has attributes such as name, email, role (admin or organizer),
 * notification preferences, phone number, profile image, device ID, and lists for admin and organizer notifications.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a user in the system, with attributes for role (admin or organizer),
 * contact details, notification preferences, and profile information.
 */
public class User {
    private boolean isAdmin;
    private boolean adminNotif;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isOrganizer;
    private boolean organizerNotif;
    private String phoneNumber;
    private String profileImage;
    private String deviceID;
    private ArrayList<Map<String, Object>> adminNotifList;
    private ArrayList<Map<String, Object>> organizerNotifList;
    private String name;

    /**
     * Default constructor for creating a User object with no initial data.
     */
    public User(String mail, String john, String doe, long l, String deviceID123) {}

    /**
     * Partial constructor that defaults to a non-admin user. Calls the full constructor
     * with isAdmin set to false.
     *
     * @param email       the user's email
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param phoneNumber the user's phone number
     * @param deviceID    the device ID associated with the user
     */
    public User(String email, String firstName, String lastName, String phoneNumber, String deviceID) {
        this(false, true, email, firstName, lastName, false, true, String.valueOf(phoneNumber), deviceID);
    }

    /**
     * Full constructor for creating a User object with specified attributes.
     *
     * @param isAdmin        whether the user is an admin
     * @param adminNotif     whether the user has admin notifications enabled
     * @param email          the user's email
     * @param firstName      the user's first name
     * @param lastName       the user's last name
     * @param isOrganizer    whether the user is an organizer
     * @param organizerNotif whether the user has organizer notifications enabled
     * @param phoneNumber    the user's phone number
     * @param deviceID       the device ID associated with the user
     */
    public User(boolean isAdmin, boolean adminNotif, String email, String firstName, String lastName, boolean isOrganizer, boolean organizerNotif, String phoneNumber, String deviceID) {
        this.isAdmin = isAdmin;
        this.adminNotif = adminNotif;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isOrganizer = isOrganizer;
        this.organizerNotif = organizerNotif;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;
        this.profileImage = "";
        this.adminNotifList = new ArrayList<>();
        this.organizerNotifList = new ArrayList<>();
    }

    // Getters

    /**
     * Returns whether the user is an admin.
     *
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Returns whether admin notifications are enabled for the user.
     *
     * @return true if admin notifications are enabled, false otherwise
     */
    public boolean isAdminNotif() {
        return adminNotif;
    }

    /**
     * Returns the user's email address.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's full name.
     *
     * @return the user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the user is an organizer.
     *
     * @return true if the user is an organizer, false otherwise
     */
    public boolean isOrganizer() {
        return isOrganizer;
    }

    /**
     * Returns whether organizer notifications are enabled for the user.
     *
     * @return true if organizer notifications are enabled, false otherwise
     */
    public boolean isOrganizerNotif() {
        return organizerNotif;
    }

    /**
     * Returns the user's phone number.
     *
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the URI of the user's profile image.
     *
     * @return the URI of the user's profile image
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Returns the device ID associated with the user.
     *
     * @return the user's device ID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Returns the list of admin notifications for the user.
     *
     * @return the list of admin notifications
     */
    public ArrayList<Map<String, Object>> getAdminNotifList() {
        return adminNotifList;
    }

    /**
     * Returns the list of organizer notifications for the user.
     *
     * @return the list of organizer notifications
     */
    public ArrayList<Map<String, Object>> getOrganizerNotifList() {
        return organizerNotifList;
    }

    // Setters

    /**
     * Sets whether the user is an admin.
     *
     * @param admin true if the user is an admin, false otherwise
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Sets whether admin notifications are enabled for the user.
     *
     * @param adminNotif true if admin notifications are enabled, false otherwise
     */
    public void setAdminNotif(boolean adminNotif) {
        this.adminNotif = adminNotif;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's full name.
     *
     * @param name the user's full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets whether the user is an organizer.
     *
     * @param organizer true if the user is an organizer, false otherwise
     */
    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    /**
     * Sets whether organizer notifications are enabled for the user.
     *
     * @param organizerNotif true if organizer notifications are enabled, false otherwise
     */
    public void setOrganizerNotif(boolean organizerNotif) {
        this.organizerNotif = organizerNotif;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber the user's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the URI of the user's profile image.
     *
     * @param imageLink the URI of the profile image
     */
    public void setProfileImage(String imageLink) {
        this.profileImage = imageLink;
    }

    /**
     * Sets the device ID associated with the user.
     *
     * @param deviceID the device ID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Sets the list of admin notifications for the user.
     *
     * @param adminNotifList the list of admin notifications
     */
    public void setAdminNotifList(ArrayList<Map<String, Object>> adminNotifList) {
        this.adminNotifList = adminNotifList;
    }

    /**
     * Sets the list of organizer notifications for the user.
     *
     * @param organizerNotifList the list of organizer notifications
     */
    public void setOrganizerNotifList(ArrayList<Map<String, Object>> organizerNotifList) {
        this.organizerNotifList = organizerNotifList;
    }
}
