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
        mapPlanner.printGraph();
        Set<Location> intersectionStreets = mapPlanner.getAdjacentStreets("0,0");
        assertEquals(2, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getStreetId().equals("Main St")));
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getStreetId().equals("Oak St")));

        intersectionStreets = mapPlanner.getAdjacentStreets("100,0");
        assertEquals(2, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getStreetId().equals("Main St")));
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getStreetId().equals("Elm St")));

        intersectionStreets = mapPlanner.getAdjacentStreets("100,100");
        assertEquals(1, intersectionStreets.size());
        assertTrue(intersectionStreets.stream().anyMatch(s -> s.getStreetId().equals("Elm St")));
    }

    @Test
    void testNonExistentIntersection() {
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        Set<Location> intersectionStreets = mapPlanner.getAdjacentStreets("50, 50");
        mapPlanner.printGraph();
        assertNull(mapPlanner.getAdjacentStreets("50, 50"));
    }

    @Test
    void testComplexIntersection() {
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(0, 0), new Point(0, 100));
        mapPlanner.addStreet("Oak St", new Point(0, 0), new Point(100, 100));
        mapPlanner.addStreet("Pine St", new Point(0, 0), new Point(-100, -100));
        mapPlanner.printGraph();
        Set<Location> intersectionStreets = mapPlanner.getAdjacentStreets("0,0");
        assertEquals(4, intersectionStreets.size());
    }

    @Test
    void testInvalidStreetAddition() {
        assertFalse(mapPlanner.addStreet(null, new Point(0, 0), new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("", new Point(0, 0), new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("Main St", null, new Point(100, 0)));
        assertFalse(mapPlanner.addStreet("Main St", new Point(0, 0), null));
    }

    @Test
    void testFurthestStreetWithSingleStreet() {
        MapPlanner MP = new MapPlanner(30);

        MP.addStreet("Main St", new Point(0, 0), new Point(100, 0));

        MP.depotLocation(new Location("Main St", StreetSide.Left));

        assertEquals("Main St", MP.furthestStreet());
    }

    @Test
    void testFurthestStreetSimpleStreets() {

        mapPlanner = new MapPlanner(30);

        // Add streets to the map with new names
        mapPlanner.addStreet("First Ave", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Second Blvd", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("Third St", new Point(100, 100), new Point(200, 100));
        mapPlanner.addStreet("Fourth Dr", new Point(200, 100), new Point(200, 0));
        mapPlanner.addStreet("Fifth Way", new Point(200, 0), new Point(100, 0));
        mapPlanner.addStreet("Sixth Lane", new Point(200, 100), new Point(300, 100));
        mapPlanner.addStreet("Seventh Pl", new Point(300, 100), new Point(400, 100));
        mapPlanner.addStreet("Eighth St", new Point(400, 100), new Point(400, 200));
        mapPlanner.addStreet("Ninth Ave", new Point(400, 200), new Point(300, 200));
        mapPlanner.addStreet("Tenth Blvd", new Point(300, 200), new Point(200, 200));
        mapPlanner.addStreet("Eleventh St", new Point(200, 200), new Point(100, 200));
        mapPlanner.addStreet("Twelfth Ln", new Point(100, 200), new Point(0, 200));
        mapPlanner.addStreet("Thirteenth Way", new Point(0, 200), new Point(0, 100));
        mapPlanner.addStreet("Fourteenth St", new Point(0, 100), new Point(0, 0));
        mapPlanner.addStreet("Fifteenth Ave", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Sixteenth Blvd", new Point(200, 300), new Point(300, 300));
        mapPlanner.addStreet("Seventeenth Ave", new Point(300, 300), new Point(400, 300));

        mapPlanner.depotLocation(new Location("First Ave", StreetSide.Right));

        assertEquals("Tenth Blvd", mapPlanner.furthestStreet());
    }

    @Test
    void testFurthestStreetwithMultipleStreets(){
        MapPlanner MP = new MapPlanner(30);
        MP.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        MP.addStreet("Elm St", new Point(100, 0), new Point(100, 100));
        MP.addStreet("2nd Ave", new Point(100, 100), new Point(200, 100));
        MP.addStreet("2.5 Ave", new Point(200, 100), new Point(200, 0));
        MP.addStreet("2.75 Ave", new Point(200, 0), new Point(100, 0));
        MP.addStreet("3rd Ave", new Point(200, 100), new Point(300, 100));
        MP.addStreet("4th Ave", new Point(300, 100), new Point(400, 100));
        MP.addStreet("Oak St", new Point(400, 100), new Point(400, 200));
        MP.addStreet("Pine St", new Point(400, 200), new Point(300, 200));
        MP.addStreet("Maple St", new Point(300, 200), new Point(200, 200));
        MP.addStreet("Birch St", new Point(200, 200), new Point(100, 200));
        MP.addStreet("Ash St", new Point(100, 200), new Point(0, 200));
        MP.addStreet("Willow St", new Point(0, 200), new Point(0, 100));
        MP.addStreet("Cedar St", new Point(0, 100), new Point(0, 0));
        MP.addStreet("5th Ave", new Point(100, 100), new Point(0, 100));
        MP.addStreet("6th Ave", new Point(200, 300), new Point(300, 300));
        MP.addStreet("7th Ave", new Point(300, 300), new Point(400, 300));

        MP.printGraph();

        MP.depotLocation(new Location("Main St", StreetSide.Right));

        System.out.println(MP.furthestStreet());
        MP.depotLocation(new Location("2nd Ave", StreetSide.Right));
        System.out.println(MP.routeNoLeftTurn(new Location("Main St", StreetSide.Right)));
    }




}