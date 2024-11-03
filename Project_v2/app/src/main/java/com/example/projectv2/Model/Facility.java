package com.example.projectv2.Model;

import android.net.Uri;

public class Facility {
    private String description;
    private String facilityName;
    private String ownerName;
    private String ownerID;
    private Uri imageLink;

    // Default constructor required for calls to DataSnapshot.getValue(Facility.class)
    public Facility() {
    }

    public Facility(String description, String facilityName, String ownerName, String ownerID, Uri imageLink) {
        this.description = description;
        this.facilityName = facilityName;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.imageLink = imageLink;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public Uri getImageLink() {
        return imageLink;
    }

    public void setImageLink(Uri imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "description='" + description + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", imageLink=" + imageLink +
                '}';
    }
}

