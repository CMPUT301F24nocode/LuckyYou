package com.example.projectv2.Model;

import android.net.Uri;

public class Event {
    private String name;
    private String detail;
    private String rules;
    private String deadline;
    private String startDate;
    private String ticketPrice;
    private Uri imageUri; // New field for image URI

    public Event(String name, String detail, String rules,String deadline, String startDate, String ticketPrice, Uri imageUri) {
        this.name = name;
        this.detail = detail;
        this.rules = rules;
        this.deadline = deadline;
        this.startDate = startDate;
        this.ticketPrice = ticketPrice;
        this.imageUri = imageUri;
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
}