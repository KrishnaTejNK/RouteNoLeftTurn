import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class MapPlannerTest {

    private MapPlanner mapPlanner;

    @BeforeEach
    void setUp() {
        mapPlanner = new MapPlanner(30);
    }

    @Test
    void testAddStreet() {
        assertTrue(mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0)));
        assertTrue(mapPlanner.addStreet("Elm St", new Point(100, 0), new Point(100, 100)));
        assertFalse(mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0))); // Duplicate street
    }

    @Test
    void testGraphStructure() {
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("Oak St", new Point(0, 0), new Point(0, 100));

        Set<Street> intersectionStreets = mapPlanner.getAdjacentStreets(new Point(0, 0));
        assertEquals(2, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getId().equals("Main St")));
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getId().equals("Oak St")));

        intersectionStreets = mapPlanner.getAdjacentStreets(new Point(100, 0));
        assertEquals(2, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getId().equals("Main St")));
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getId().equals("Elm St")));

        intersectionStreets = mapPlanner.getAdjacentStreets(new Point(100, 100));
        assertEquals(1, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getId().equals("Elm St")));
    }

    @Test
    void testNonExistentIntersection() {
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        Set<Street> intersectionStreets = mapPlanner.getAdjacentStreets(new Point(50, 50));
        assertTrue(intersectionStreets.isEmpty());
    }

    @Test
    void testComplexIntersection() {
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(0, 0), new Point(0, 100));
        mapPlanner.addStreet("Oak St", new Point(0, 0), new Point(100, 100));
        mapPlanner.addStreet("Pine St", new Point(0, 0), new Point(-100, -100));

        Set<Street> intersectionStreets = mapPlanner.getAdjacentStreets(new Point(0, 0));
        assertEquals(4, intersectionStreets.size());
    }

    @Test
    void testInvalidStreetAddition() {
        assertFalse(mapPlanner.addStreet(null, new Point(0, 0), new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("", new Point(0, 0), new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("Main St", null, new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("Main St", new Point(0, 0), null));
    }
}