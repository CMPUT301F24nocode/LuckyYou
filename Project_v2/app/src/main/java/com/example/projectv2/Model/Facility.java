package com.example.projectv2.Model;

/**
 * Represents a facility with a name, description, and unique identifier.
 */
public class Facility {
    private final String owner;
    private final String name;
    private final String description;
    private final String id;

    /**
     * Constructs a Facility with the specified name, description, and unique identifier.
     *
     * @param owner       the device ID of the owner of the facility
     * @param name        the name of the facility
     * @param description a brief description of the facility
     * @param id          the unique identifier for the facility
     * @throws IllegalArgumentException if any of the parameters are null or empty
     */
    public Facility(String owner, String name, String description, String id) {
        if (owner == null || owner.isEmpty() ||
                name == null || name.isEmpty() ||
                description == null || description.isEmpty() ||
                id == null || id.isEmpty()) {
            throw new IllegalArgumentException("None of the parameters can be null or empty.");
        }

        this.owner = owner;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getOwner() {
        return owner;
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
