/**
 * Model class representing an event. Each event has details including ID, owner, name,
 * description, rules, dates, ticket price, an optional image, and facility information.
 * Implements Serializable for ease of passing Event objects between activities.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Model;

import android.net.Uri;
import java.io.Serializable;

/**
 * Represents an event with details such as name, owner, description, rules, dates, and associated
 * facility. The class is Serializable, allowing instances to be easily passed in intents.
 */
public class Event implements Serializable {
    private String eventID;
    private String owner;
    private String name;
    private String detail;
    private String rules;
    private String deadline;
    private String startDate;
    private String ticketPrice;
    private Uri imageUri;
    private String facility;

    /**
     * Constructs an Event with specified details.
     *
     * @param eventID     the unique identifier for the event
     * @param owner       the owner or creator of the event
     * @param name        the name of the event
     * @param detail      a description of the event
     * @param rules       the rules or guidelines for the event
     * @param deadline    the registration deadline for the event
     * @param startDate   the start date of the event
     * @param ticketPrice the price of the event's ticket
     * @param imageUri    the URI of the event's image, if any
     * @param facility    the facility where the event is hosted
     */
    public Event(String eventID, String owner, String name, String detail, String rules, String deadline, String startDate, String ticketPrice, Uri imageUri, String facility) {
        this.eventID = eventID;
        this.owner = owner;
        this.name = name;
        this.detail = detail;
        this.rules = rules;
        this.deadline = deadline;
        this.startDate = startDate;
        this.ticketPrice = ticketPrice;
        this.imageUri = imageUri;
        this.facility = facility;
    }

    public Event(String eventID, String owner, String date, String date1) {this.eventID = eventID;
        this.owner = owner;
        this.deadline = date;
        this.startDate = date1;
    }

    /**
     * Returns the owner of the event.
     *
     * @return the owner of the event
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the name of the event.
     *
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the detailed description of the event.
     *
     * @return the detailed description of the event
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Returns the rules for the event.
     *
     * @return the rules for the event
     */
    public String getRules() {
        return rules;
    }

    /**
     * Returns the registration deadline for the event.
     *
     * @return the registration deadline for the event
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Returns the start date of the event.
     *
     * @return the start date of the event
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Returns the ticket price for the event.
     *
     * @return the ticket price for the event
     */
    public String getTicketPrice() {
        return ticketPrice;
    }

    /**
     * Returns the URI of the image associated with the event.
     *
     * @return the URI of the image associated with the event
     */
    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * Returns the facility where the event is hosted.
     *
     * @return the facility where the event is hosted
     */
    public String getFacility() {
        return facility;
    }

    /**
     * Sets the unique identifier for the event.
     *
     * @param eventID the unique identifier for the event
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Returns the unique identifier of the event.
     *
     * @return the unique identifier of the event
     */
    public String getEventID() {
        return eventID;
    }
}
