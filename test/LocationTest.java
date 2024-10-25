import org.junit.Test;
import static org.junit.Assert.*;

public class LocationTest {

    // Input Validation Tests
    @Test
    public void testConstructorWithValidInputs() {
        Location location = new Location("A St", StreetSide.Left);
        assertNotNull(location);
        assertEquals("A St", location.getStreetId());
        assertEquals(StreetSide.Left, location.getStreetSide());
    }

    @Test
    public void testConstructorWithNullStreet() {
        Location location = new Location(null, StreetSide.Right);
        assertNull(location.getStreetId());
        assertEquals(StreetSide.Right, location.getStreetSide());
    }

    @Test
    public void testConstructorWithNullStreetSide() {
        Location location = new Location("Oak Ave", null);
        assertEquals("Oak Ave", location.getStreetId());
        assertNull(location.getStreetSide());
    }

    @Test
    public void testConstructorWithEmptyStreet() {
        Location location = new Location("", StreetSide.Left);
        assertEquals("", location.getStreetId());
        assertEquals(StreetSide.Left, location.getStreetSide());
    }

    // Boundary Tests
    @Test
    public void testConstructorWithLongStreetName() {
        String longStreetName = "A".repeat(1000);
        Location location = new Location(longStreetName, StreetSide.Right);
        assertEquals(longStreetName, location.getStreetId());
    }

    // Control Flow Tests
    @Test
    public void testGetStreetId() {
        Location location = new Location("B St", StreetSide.Left);
        assertEquals("B St", location.getStreetId());
    }

    @Test
    public void testGetStreetSide() {
        Location location = new Location("Pine Rd", StreetSide.Right);
        assertEquals(StreetSide.Right, location.getStreetSide());
    }

    // Data Flow Tests
    @Test
    public void testDataFlowThroughAllFields() {
        Location location = new Location("Maple Ave", StreetSide.Left);
        assertEquals("Maple Ave", location.getStreetId());
        assertEquals(StreetSide.Left, location.getStreetSide());
    }

    @Test
    public void testMultipleLocations() {
        Location location1 = new Location("First St", StreetSide.Left);
        Location location2 = new Location("Second Ave", StreetSide.Right);

        assertEquals("First St", location1.getStreetId());
        assertEquals(StreetSide.Left, location1.getStreetSide());
        assertEquals("Second Ave", location2.getStreetId());
        assertEquals(StreetSide.Right, location2.getStreetSide());
    }
}