package com.example.projectv2.Model;

public class Profile {
    private String id;
    private String imageUrl;
    private String name;
    private String description;

    // Constructor
    public Profile(String id, String imageUrl, String name, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
