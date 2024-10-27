package com.example.projectv2.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a notification with recipient, content, time sent,
 * and flags for organiser and admin roles.
 */
public class Notification {
    private String sendTo;       // The recipient of the notification
    private String content;      // The content of the notification message
    private String timeSent;     // The time the notification was sent, formatted as dd-MM-yyyy HH:mm:ss
    private boolean isOrganiser; // Flag indicating if the notification is from an organiser
    private boolean isAdmin;     // Flag indicating if the notification is from an admin

    /**
     * Constructs a Notification instance with the current device time.
     *
     * @param sendTo      The recipient of the notification
     * @param content     The content of the notification
     * @param isOrganiser True if the notification is for organisers, false otherwise
     * @param isAdmin     True if the notification is for admins, false otherwise
     */
    public Notification(String sendTo, String content, boolean isOrganiser, boolean isAdmin) {
        this.sendTo = sendTo;
        this.content = content;
        this.isOrganiser = isOrganiser;
        this.isAdmin = isAdmin;
        this.timeSent = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    /**
     * Constructs a Notification instance with a specified time.
     *
     * @param sendTo      The recipient of the notification
     * @param content     The content of the notification
     * @param timeSent    The specified time the notification was sent
     * @param isOrganiser True if the notification is for organisers, false otherwise
     * @param isAdmin     True if the notification is for admins, false otherwise
     */
    public Notification(String sendTo, String content, String timeSent, boolean isOrganiser, boolean isAdmin) {
        this.sendTo = sendTo;
        this.content = content;
        this.isOrganiser = isOrganiser;
        this.isAdmin = isAdmin;
        this.timeSent = timeSent;
    }

    /**
     * Gets the recipient of the notification.
     *
     * @return The recipient of the notification
     */
    public String getSendTo() { return sendTo; }

    /**
     * Gets the content of the notification.
     *
     * @return The content of the notification
     */
    public String getContent() { return content; }

    /**
     * Gets the time the notification was sent.
     *
     * @return The time the notification was sent
     */
    public String getTimeSent() { return timeSent; }

    /**
     * Checks if the notification is for organisers.
     *
     * @return True if the notification is for organisers, false otherwise
     */
    public boolean isOrganiser() { return isOrganiser; }

    /**
     * Checks if the notification is for admins.
     *
     * @return True if the notification is for admins, false otherwise
     */
    public boolean isAdmin() { return isAdmin; }
}
