import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LegTest {
    private Point startPoint;
    private Point endPoint;

    @Before
    public void setUp() {
        startPoint = new Point(0, 0);
        endPoint = new Point(100, 0);
    }

    // Input Validation Tests
    @Test
    public void testConstructorWithValidInputs() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        assertNotNull(leg);
        assertEquals(TurnDirection.Straight, leg.getTurn());
        assertEquals("A St", leg.getStreetTurnedOnto());
        assertEquals(startPoint, leg.getStartPoint());
        assertEquals(endPoint, leg.getEndPoint());
    }

    @Test
    public void testConstructorWithNullInputs() {
        Leg leg = new Leg(null, null, null, null);
        assertNull(leg.getTurn());
        assertNull(leg.getStreetTurnedOnto());
        assertNull(leg.getStartPoint());
        assertNull(leg.getEndPoint());
    }

    @Test
    public void testSetStreetTurnedOntoWithEmptyString() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setStreetTurnedOnto("");
        assertEquals("", leg.getStreetTurnedOnto());
    }

    // Boundary Tests
    @Test
    public void testSetAccumulatedLengthWithZero() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setAccumulatedLength(0.0);
        assertEquals(0.0, leg.getAccumulatedLength(), 0.001);
    }

    @Test
    public void testSetAccumulatedLengthWithLargeValue() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setAccumulatedLength(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, leg.getAccumulatedLength(), 0.001);
    }

    // Control Flow Tests
    @Test
    public void testSetAndGetTurn() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setTurn(TurnDirection.Right);
        assertEquals(TurnDirection.Right, leg.getTurn());
    }

    @Test
    public void testSetAndGetStreetTurnedOnto() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setStreetTurnedOnto("Oak St");
        assertEquals("Oak St", leg.getStreetTurnedOnto());
    }

    @Test
    public void testSetAndGetStartPoint() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        Point newStart = new Point(10, 10);
        leg.setStartPoint(newStart);
        assertEquals(newStart, leg.getStartPoint());
    }

    @Test
    public void testSetAndGetEndPoint() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        Point newEnd = new Point(110, 10);
        leg.setEndPoint(newEnd);
        assertEquals(newEnd, leg.getEndPoint());
    }

    // Data Flow Tests

    @Test
    public void testAccumulatedLengthDataFlow() {
        Leg leg = new Leg(TurnDirection.Straight, "A St", startPoint, endPoint);
        leg.setAccumulatedLength(100.0);
        assertEquals(100.0, leg.getAccumulatedLength(), 0.001);
        leg.setAccumulatedLength(200.0);
        assertEquals(200.0, leg.getAccumulatedLength(), 0.001);
    }
}