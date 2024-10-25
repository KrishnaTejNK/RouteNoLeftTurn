import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SubRouteTest {
    private MapPlanner mapPlanner;
    private Route mainRoute;
    private SubRoute subRoute;

    @Before
    public void setUp() {
        mapPlanner = new MapPlanner(30); // Assuming 30 degree tolerance
        mainRoute = new Route(mapPlanner);

        // Set up a sample route
        mapPlanner.addStreet("A St", new Point(0, 0), new Point(100, 0));
        mapPlanner.addStreet("1st Ave", new Point(100, 0), new Point(100, 100));
        mapPlanner.addStreet("2nd St", new Point(100, 100), new Point(0, 100));
        mapPlanner.addStreet("Park Ave", new Point(0, 100), new Point(0, 0));

        mainRoute.appendTurn(TurnDirection.Straight, "A St");
        mainRoute.appendTurn(TurnDirection.Left, "1st Ave");
        mainRoute.appendTurn(TurnDirection.Left, "2nd St");
        mainRoute.appendTurn(TurnDirection.Left, "Park Ave");
        mainRoute.appendTurn(TurnDirection.Left, "A St");
        subRoute = new SubRoute(mainRoute, 2, 3, mapPlanner);
    }

    @Test
    public void testConstructor() {
        assertNotNull(subRoute);
        assertEquals(mainRoute, subRoute.getWalk());
        assertEquals(2, subRoute.getStartLeg());
        assertEquals(3, subRoute.getEndLeg());
    }

    @Test
    public void testGettersAndSetters() {
        Route newRoute = new Route(mapPlanner);
        subRoute.setWalk(newRoute);
        assertEquals(newRoute, subRoute.getWalk());

        subRoute.setStartLeg(3);
        assertEquals(3, subRoute.getStartLeg());

        subRoute.setEndLeg(4);
        assertEquals(4, subRoute.getEndLeg());
    }

    @Test
    public void testSubrouteStart() {
        assertEquals(1, subRoute.subrouteStart());
    }

    @Test
    public void testSubrouteEnd() {
        assertEquals(2, subRoute.subrouteEnd());
    }

    @Test
    public void testExtractRoute() {
        Route extractedRoute = subRoute.extractRoute();
        assertNotNull(extractedRoute);
        assertEquals(2, extractedRoute.legs());
        assertEquals("1st Ave", extractedRoute.turnOnto(1));
        assertEquals("2nd St", extractedRoute.turnOnto(2));
    }

    @Test
    public void testSubRouteWithSameStartAndEndLeg() {
        SubRoute sameStartEnd = new SubRoute(mainRoute, 2, 2, mapPlanner);
        Route extractedRoute = sameStartEnd.extractRoute();
        assertEquals(1, extractedRoute.legs());
    }

    @Test
    public void testSubRouteWithFullRoute() {
        SubRoute fullRoute = new SubRoute(mainRoute, 1, mainRoute.legs(), mapPlanner);
        Route extractedRoute = fullRoute.extractRoute();
        assertEquals(mainRoute.legs(), extractedRoute.legs());
    }

    @Test
    public void testSubRouteWithInvalidStartLeg() {
        assertThrows(IllegalArgumentException.class, () -> new SubRoute(mainRoute, 0, 2, mapPlanner));
    }

    @Test
    public void testSubRouteWithInvalidEndLeg() {
        SubRoute invalidEnd = new SubRoute(mainRoute, 1, mainRoute.legs() + 1, mapPlanner);
        Route extractedRoute = invalidEnd.extractRoute();
        assertEquals(mainRoute.legs(), extractedRoute.legs());
    }

    @Test
    public void testSubRouteWithStartGreaterThanEnd() {
        SubRoute invalidOrder = new SubRoute(mainRoute, 3, 2, mapPlanner);
        Route extractedRoute = invalidOrder.extractRoute();
        assertEquals(0, extractedRoute.legs());
    }

    @Test
    public void testExtractRoutePreservesCorrectTurns() {
        Route extractedRoute = subRoute.extractRoute();

        assertEquals(TurnDirection.Left, extractedRoute.turnDirection(1));
        assertEquals(TurnDirection.Left, extractedRoute.turnDirection(2));
    }

    @Test
    public void testExtractRouteWithLoop() {
        SubRoute loopRoute = new SubRoute(mainRoute, 1, 5, mapPlanner);
        Route extractedRoute = loopRoute.extractRoute();
        assertEquals(5, extractedRoute.legs());
        assertFalse(extractedRoute.loops().isEmpty());
    }

    @Test
    public void testSubRouteWithEmptyMainRoute() {
        Route emptyRoute = new Route(mapPlanner);
        SubRoute emptySubRoute = new SubRoute(emptyRoute, 1, 1, mapPlanner);
        Route extractedRoute = emptySubRoute.extractRoute();
        assertEquals(0, extractedRoute.legs());
    }

    @Test
    public void testSubRouteWithNullMainRoute() {



    }

    @Test
    public void testSubRouteWithNullMapPlanner() {
        assertThrows(IllegalArgumentException.class, () -> new SubRoute(mainRoute, 1, 2, null));
    }
}