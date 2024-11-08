package com.example.projectv2.Model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the Facility model class.
 */
public class FacilityTest {

    private Facility facility;
    private String name = "Main Hall";
    private String description = "A spacious facility for large events.";

    @Before
    public void setUp() {
        // Initialize the Facility object with test data
        facility = new Facility(name, description);
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
