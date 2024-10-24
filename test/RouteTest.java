import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class RouteTest {
    private MapPlanner mapPlanner;
    private Route route;

    @Before
    public void setUp() {
        mapPlanner = new MapPlanner(30);
        route = new Route(mapPlanner);

        // Add some streets to the mapPlanner for testing
        mapPlanner.addStreet("Main St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("1st Ave", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("2nd St", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Park Ave", new Point(0, 100), new Point(0, 0));
    }

    // Append Turn Tests
    @Test
    public void testAppendTurnToEmptyRoute() {
        assertTrue(route.appendTurn(TurnDirection.Straight, "Main St"));
        assertEquals(1, route.legs());
    }

    @Test
    public void testAppendTurnToNonEmptyRoute() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertTrue(route.appendTurn(TurnDirection.Left, "1st Ave"));
        assertEquals(2, route.legs());
    }

    @Test
    public void testAppendInvalidTurn() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertFalse(route.appendTurn(TurnDirection.Right, "1st Ave"));
    }

    @Test
    public void testAppendNullOrEmptyStreet() {
        assertFalse(route.appendTurn(TurnDirection.Straight, null));
        assertFalse(route.appendTurn(TurnDirection.Straight, ""));
    }

    // Turn Onto Tests
    @Test
    public void testTurnOntoValidLeg() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals("Main St", route.turnOnto(1));
    }

    @Test
    public void testTurnOntoInvalidLeg() {
        assertNull(route.turnOnto(0));
        assertNull(route.turnOnto(1)); // Empty route
    }

    // Turn Direction Tests
    @Test
    public void testTurnDirectionValidLeg() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals(TurnDirection.Straight, route.turnDirection(1));
    }

    @Test
    public void testTurnDirectionInvalidLeg() {
        assertNull(route.turnDirection(0));
        assertNull(route.turnDirection(1)); // Empty route
    }

    // Legs Tests
    @Test
    public void testLegsCount() {
        assertEquals(0, route.legs());
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "1st Ave");
        assertEquals(2, route.legs());
    }

    // Length Tests
    @Test
    public void testLengthEmptyRoute() {
        assertEquals(0.0, route.length(), 0.001);
    }

    @Test
    public void testLengthSingleLegRoute() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        assertEquals(50.0, route.length(), 0.001); // Half of Main St length
    }

    @Test
    public void testLengthMultipleLegRoute() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "1st Ave");
        route.appendTurn(TurnDirection.Left, "2nd St");
        assertEquals(200.0, route.length(), 0.001); // 50 + 100 + 100
    }

    // Loops Tests
    @Test
    public void testLoopsNoLoop() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Right, "1st Ave");
        assertTrue(route.loops().isEmpty());
    }

    @Test
    public void testLoopsSimpleLoop() {
        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "1st Ave");
        route.appendTurn(TurnDirection.Left, "2nd St");
        route.appendTurn(TurnDirection.Left, "Park Ave");

        List<SubRoute> loops = route.loops();
        System.out.println(route.legs());
        assertEquals(1, loops.size());

        assertEquals(1, loops.get(0).getStartLeg());

        assertEquals(4, loops.get(0).getEndLeg());

    }

    // Simplify Tests

    @Test

    public void testSimplifyNoStraightSegments() {

        route.appendTurn(TurnDirection.Straight, "Main St");
        route.appendTurn(TurnDirection.Left, "1st Ave");
        route.appendTurn(TurnDirection.Left, "2nd St");

        Route simplified = route.simplify();
        assertEquals(3,simplified.legs());

    }


    @Test

    public void testSimplifyStraightSegments() {

        route.appendTurn(TurnDirection.Straight,"Main St");
        route.appendTurn(TurnDirection.UTurn,"Main St");
        route.appendTurn(TurnDirection.Left,"1st Ave");
        route.appendTurn(TurnDirection.Straight,"1st Ave");

        Route simplified =route.simplify();
        assertEquals (2,simplified.legs());

    }


    // Error Handling Tests

    @Test

    public void testAppendNonExistentStreet () {

        assertFalse (route.appendTurn( TurnDirection.Straight,"NonexistentSt"));

    }

}