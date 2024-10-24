import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void turnType() {
        Point origin = new Point( 0, 0 );
        Point p1 = new Point( 0, 1 );
        Point p2 = new Point( 1, 1 );
        Point p3 = new Point( 1, 0 );
        Point p4 = new Point( 1, 10 );
        Point p5 = new Point( 1, -10 );

        assertEquals( TurnDirection.Right, origin.turnType( p1, p4, 1 ) );
        assertEquals( TurnDirection.Straight, origin.turnType( p1, p4, 20 ) );
        assertEquals( TurnDirection.Right, origin.turnType( p1, p5, 1 ) );
        assertEquals( TurnDirection.UTurn, origin.turnType( p1, p5, 20 ) );
        assertEquals( TurnDirection.Left, p3.turnType( p2, p1, 1 ) );
    }

    // Input Validation Tests
    @Test
    public void testConstructorWithValidInputs() {
        Point p = new Point(5, 10);
        assertEquals(5, p.getX());
        assertEquals(10, p.getY());
    }

    @Test
    public void testConstructorWithNegativeInputs() {
        Point p = new Point(-3, -7);
        assertEquals(-3, p.getX());
        assertEquals(-7, p.getY());
    }

    // Boundary Tests
    @Test
    public void testConstructorWithZeroInputs() {
        Point p = new Point(0, 0);
        assertEquals(0, p.getX());
        assertEquals(0, p.getY());
    }

    @Test
    public void testConstructorWithMaxIntegerValues() {
        Point p = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, p.getX());
        assertEquals(Integer.MAX_VALUE, p.getY());
    }

    @Test
    public void testConstructorWithMinIntegerValues() {
        Point p = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, p.getX());
        assertEquals(Integer.MIN_VALUE, p.getY());
    }

    // Control Flow Tests
    @Test
    public void testDistanceToSamePoint() {
        Point p = new Point(3, 4);
        assertEquals(0.0, p.distanceTo(p), 0.001);
    }

    @Test
    public void testDistanceToHorizontalPoint() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 0);
        assertEquals(3.0, p1.distanceTo(p2), 0.001);
    }

    @Test
    public void testDistanceToVerticalPoint() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 4);
        assertEquals(4.0, p1.distanceTo(p2), 0.001);
    }

    @Test
    public void testDistanceToDiagonalPoint() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);
        assertEquals(5.0, p1.distanceTo(p2), 0.001);
    }

    @Test
    public void testTurnTypeStraight() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(5, 0);
        Point p3 = new Point(10, 0);
        assertEquals(TurnDirection.Straight, p1.turnType(p2, p3, 5));
    }

    @Test
    public void testTurnTypeLeft() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(5, 0);
        Point p3 = new Point(5, 5);
        assertEquals(TurnDirection.Left, p1.turnType(p2, p3, 5));
    }

    @Test
    public void testTurnTypeRight() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(5, 0);
        Point p3 = new Point(5, -5);
        assertEquals(TurnDirection.Right, p1.turnType(p2, p3, 5));
    }

    @Test
    public void testTurnTypeUTurn() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(5, 0);
        Point p3 = new Point(0, 0);
        assertEquals(TurnDirection.UTurn, p1.turnType(p2, p3, 5));
    }

    // Data Flow Tests
    @Test
    public void testEqualsWithSamePoint() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testEqualsWithDifferentPoints() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(4, 3);
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testToString() {
        Point p = new Point(3, 4);
        assertEquals("3,4", p.toString());
    }

    @Test
    public void testDataFlowThroughAllMethods() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(6, 0);

        assertEquals(0, p1.getX());
        assertEquals(0, p1.getY());
        assertEquals(5.0, p1.distanceTo(p2), 0.001);
        assertEquals(TurnDirection.Right, p1.turnType(p2, p3, 5));
        assertFalse(p1.equals(p2));
        assertEquals("0,0", p1.toString());
    }
}