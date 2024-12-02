package com.example.projectv2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.projectv2.Model.Facility;

/**
 * Unit test for the Facility model class.
 */
public class FacilityTest {

    private Facility facility;
    private String owner = "17f6ae952ae27ffa";
    private String name = "Main Hall";
    private String description = "A spacious facility for large events.";
    private String id = "1";

    @Before
    public void setUp() {
        // Initialize the Facility object with test data
        facility = new Facility(owner, name, description, id);
    }

    /**
     * Test that the Facility constructor correctly initializes all fields.
     */
    @Test
    public void testFacilityConstructor() {
        assertEquals(name, facility.getName());
        assertEquals(description, facility.getDescription());
    }

    /**
     * Test the getName method to verify it returns the correct name.
     */
    @Test
    public void testGetName() {
        assertEquals("Name getter failed", name, facility.getName());
    }

    /**
     * Test the getDescription method to verify it returns the correct description.
     */
    @Test
    public void testGetDescription() {
        assertEquals("Description getter failed", description, facility.getDescription());
    }
}
