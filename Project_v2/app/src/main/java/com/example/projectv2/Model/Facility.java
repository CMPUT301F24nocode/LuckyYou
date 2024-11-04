package com.example.projectv2.Model;

import android.net.Uri;

public class Facility {
    private String name;
    private String description;
    private Uri imageUri;

    public Facility(String name, String description, Uri imageUri) {
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
