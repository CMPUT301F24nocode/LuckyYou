package com.example.projectv2.Model;

import android.net.Uri;
import java.io.Serializable;

public class Event implements Serializable {
    private String name;
    private String detail;
    private String rules;
    private String deadline;
    private String startDate;
    private String ticketPrice;
    private Uri imageUri;
    private String facility;
    private String eventID;

    public Event(String name, String detail, String rules, String deadline, String startDate, String ticketPrice, Uri imageUri, String facility, String eventID) {
        this.name = name;
        this.detail = detail;
        this.rules = rules;
        this.deadline = deadline;
        this.startDate = startDate;
        this.ticketPrice = ticketPrice;
        this.imageUri = imageUri;
        this.facility = facility;
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getRules() {
        return rules;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getFacility() {
        return facility;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventID() {
        return eventID;
    }
}
