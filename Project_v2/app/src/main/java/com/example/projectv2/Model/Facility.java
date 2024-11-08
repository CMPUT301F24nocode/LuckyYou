package com.example.projectv2.Model;

public class Facility {
    private String name;
    private String description;
    private String id;

    public Facility(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
