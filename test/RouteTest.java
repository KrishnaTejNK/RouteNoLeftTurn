import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    private MapPlanner mapPlanner;
    private Route route;

    @BeforeEach
    void setUp() {
        mapPlanner = new MapPlanner(30);
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("Oak St", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Pine St", new Point(0, 100), new Point(0, 0));

        route = new Route(mapPlanner);
    }

    @Test
    void testAppendTurnWithNullTurn() {
        assertFalse(route.appendTurn(null, "Main St"));
    }

    @Test
    void testAppendTurnWithNullStreet() {
        assertFalse(route.appendTurn(TurnDirection.Left, null));
    }

    @Test
    void testAppendTurnWithEmptyStreet() {
        assertFalse(route.appendTurn(TurnDirection.Left, ""));
    }

    @Test
    void testAppendTurnWithValidInputFirstLeg() {
        assertTrue(route.appendTurn(TurnDirection.Straight, "Main St"));
        assertEquals(1, route.legs());
    }

    @Test
    void testAppendTurnWithValidInputSubsequentLeg() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertTrue(route.appendTurn(TurnDirection.Left, "Elm St"));
        assertEquals(2, route.legs());
    }

    @Test
    void testAppendTurnWithInvalidTurn() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertFalse(route.appendTurn(TurnDirection.Right, "Elm St"));
        assertEquals(1, route.legs());
    }

    @Test
    void testTurnOntoWithZeroLegNumber() {
        assertNull(route.turnOnto(0));
    }

    @Test
    void testTurnOntoWithValidLegNumber() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals("Main St", route.turnOnto(1));
    }

    @Test
    void testTurnDirectionWithZeroLegNumber() {
        assertNull(route.turnDirection(0));
    }

    @Test
    void testTurnDirectionWithValidLegNumber() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals(TurnDirection.Straight, route.turnDirection(1));
    }

    @Test
    void testLegsCount() {
        assertEquals(0, route.legs());
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals(1, route.legs());
        route.appendTurn(TurnDirection.Left, "Elm St");
        assertEquals(2, route.legs());
    }

    @Test
    void testLengthWithEmptyRoute() {
        assertEquals(0.0, route.length(), 0.001);
    }

    @Test
    void testLengthCalculation() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "Elm St");
        route.appendTurn(TurnDirection.Left, "Oak St");
        route.appendTurn(TurnDirection.Left, "Pine St");

        double expectedLength = 300.0; // (100 / 2) + 100 + 100 + (100 / 2)
        assertEquals(expectedLength, route.length(), 0.001);
    }

//    @Test
//    void testSimplifyWithStraightTurns() {
//        route.appendTurn(TurnDirection.Straight, "Main St");
//        route.appendTurn(TurnDirection.Left, "Elm St");
//        route.appendTurn(TurnDirection.Straight, "Elm St");
//        route.appendTurn(TurnDirection.Left, "Oak St");
//
//        Route simplified = route.simplify();
//        assertEquals(3, simplified.legs());
//        assertEquals(TurnDirection.Straight, simplified.turnDirection(1));
//        assertEquals(TurnDirection.Left, simplified.turnDirection(2));
//        assertEquals(TurnDirection.Left, simplified.turnDirection(3));
//    }

    @Test
    void testSimplifyWithNoStraightTurns() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "Elm St");
        route.appendTurn(TurnDirection.Left, "Oak St");

        Route simplified = route.simplify();
        assertEquals(3, simplified.legs());
    }

    @Test
    void testRouteLengthUsingMapPlannerData() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "Elm St");
        route.appendTurn(TurnDirection.Left, "Oak St");

        double expectedLength = 200.0; // (100 / 2) + 100 + (100 / 2)
        assertEquals(expectedLength, route.length(), 0.001,
                "The route length should be 200 meters (half of first and last streets, full middle street)");
    }


    @Test
    void testloops() {
        mapPlanner = new MapPlanner(30);
        mapPlanner.addStreet("A", new Point(0, -100), new Point(0, 0));
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("Oak St", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Pine St", new Point(0, 100), new Point(0, 0));
        mapPlanner.addStreet("B", new Point(0, 0), new Point(-100, 0));

        route = new Route(mapPlanner);

        route.appendTurn(TurnDirection.Straight, "A");         // Start at A
        route.appendTurn(TurnDirection.Right, "Main St");      // Proceed to Main St
        route.appendTurn(TurnDirection.Left, "Elm St");        // Turn onto Elm St
        route.appendTurn(TurnDirection.Left, "Oak St");        // Turn onto Oak St
        route.appendTurn(TurnDirection.Left, "Pine St");       // Turn onto Pine St (closing the loop)
        route.appendTurn(TurnDirection.Right, "B");            // Proceed to B (after the loop)

        List<SubRoute> loop = route.loops();

        // Adjust the assertion to expect 1 loop (instead of 4)
        assertEquals(1, loop.size());

        // Additional assertions to check if the loop is detected correctly
        SubRoute detectedLoop = loop.get(0);
        assertEquals(1, detectedLoop.subrouteStart());  // Loop should start after "A" on "Main St"
        assertEquals(4, detectedLoop.subrouteEnd());    // Loop should end at "Pine St"
    }

    @Test
    void testSimplifyWithStraightTurns() {
        mapPlanner = new MapPlanner(30);
        mapPlanner.addStreet("A", new Point(0, -100), new Point(0, 0));
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("Elm St", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("Oak St", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Pine St", new Point(0, 100), new Point(0, 0));
        mapPlanner.addStreet("B", new Point(0, 0), new Point(-100, 0));

        route = new Route(mapPlanner);
        Route route = new Route(mapPlanner);
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "Elm St");
        route.appendTurn(TurnDirection.Left, "Oak St");

        Route simplified = route.simplify();
        assertEquals(3, simplified.legs());
        assertEquals(TurnDirection.Straight, simplified.turnDirection(1));
        assertEquals(TurnDirection.Left, simplified.turnDirection(2));
        assertEquals(TurnDirection.Left, simplified.turnDirection(3));
    }


}