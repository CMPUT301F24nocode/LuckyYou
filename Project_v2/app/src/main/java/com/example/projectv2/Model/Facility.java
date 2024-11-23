/**
 * Model class representing a facility. Each facility has a name, description, and unique identifier.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
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
     * @param name        the name of the facility
     * @param description a brief description of the facility
     * @param id          the unique identifier for the facility
     */
    public Facility(String owner, String name, String description, String id) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.id = id;
    }

    /**
     * Returns the name of the facility.
     *
     * @return the name of the facility
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the facility.
     *
     * @return the description of the facility
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unique identifier of the facility.
     *
     * @return the unique identifier of the facility
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the facility.
     *
     * @return the name of the facility
     */
    public String getOwner() {
        return owner;
    }

}
