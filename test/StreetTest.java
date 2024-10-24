import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StreetTest {
    private Street street;
    private Point start;
    private Point end;

    @Before
    public void setUp() {
        start = new Point(0, 0);
        end = new Point(3, 4);
        street = new Street("Main St", start, end);
    }

    @Test
    public void testConstructor() {
        assertNotNull(street);
        assertEquals("Main St", street.getId());
        assertEquals(start, street.getStart());
        assertEquals(end, street.getEnd());
    }

    @Test
    public void testGetId() {
        assertEquals("Main St", street.getId());
    }

    @Test
    public void testGetStart() {
        assertEquals(start, street.getStart());
    }

    @Test
    public void testGetEnd() {
        assertEquals(end, street.getEnd());
    }

    @Test
    public void testGetLength() {
        assertEquals(5.0, street.getLength(), 0.001);
    }

    @Test
    public void testGetOtherEndWithStart() {
        assertEquals(end, street.getOtherEnd(start));
    }

    @Test
    public void testGetOtherEndWithEnd() {
        assertEquals(start, street.getOtherEnd(end));
    }

    @Test
    public void testGetOtherEndWithInvalidPoint() {
        assertNull(street.getOtherEnd(new Point(1, 1)));
    }

    @Test
    public void testStartCords() {
        assertEquals("0,0", street.startCords());
    }

    @Test
    public void testEndCords() {
        assertEquals("3,4", street.endCords());
    }

    @Test
    public void testStreetWithSameStartAndEnd() {
        Street samePointStreet = new Street("Circle St", new Point(1, 1), new Point(1, 1));
        assertEquals(0.0, samePointStreet.getLength(), 0.001);
    }

    @Test
    public void testStreetWithNegativeCoordinates() {
        Street negativeStreet = new Street("Negative St", new Point(-2, -2), new Point(-5, -6));
        assertEquals(5.0, negativeStreet.getLength(), 0.001);
    }

    @Test
    public void testStreetWithLargeCoordinates() {
        Street largeStreet = new Street("Large St", new Point(1000000, 2000000), new Point(3000000, 4000000));
        assertEquals(2828427.125, largeStreet.getLength(), 0.001);
    }
}