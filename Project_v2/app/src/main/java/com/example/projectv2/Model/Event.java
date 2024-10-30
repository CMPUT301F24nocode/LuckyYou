package com.example.projectv2.Model;

public class Event {
    private String eventId;
    private String name;
    private String details;
    private String date;
    private String createdByDevice;

    // No-argument constructor for Firebase
    public Event() {}

    // Constructor with parameters
    public Event(String eventId, String name, String details, String date, String createdByDevice) {
        this.eventId = eventId;
        this.name = name;
        this.details = details;
        this.date = date;
        this.createdByDevice = createdByDevice;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCreatedByDevice() { return createdByDevice; }
    public void setCreatedByDevice(String createdByDevice) { this.createdByDevice = createdByDevice; }
}
