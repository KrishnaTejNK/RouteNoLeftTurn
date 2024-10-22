import java.util.HashSet;
import java.util.Set;

public class Intersection {
    private Point location;
    private Set<Street> connectedStreets;

    public Intersection(Point location) {
        this.location = location;
        this.connectedStreets = new HashSet<>();
    }

    public void addStreet(Street street) {
        connectedStreets.add(street);
    }

    public boolean removeStreet(Street street) {
        return connectedStreets.remove(street);
    }

    public Set<Street> getConnectedStreets() {
        return new HashSet<>(connectedStreets);
    }

    public Point getLocation() {
        return location;
    }

    public int getNumberOfConnectedStreets() {
        return connectedStreets.size();
    }

    public boolean isDeadEnd() {
        return connectedStreets.size() == 1;
    }

    public boolean isTIntersection() {
        return connectedStreets.size() == 3;
    }

    public TurnDirection getTurnDirection(Street fromStreet, Street toStreet) {
//        if (!connectedStreets.contains(fromStreet) || !connectedStreets.contains(toStreet)) {
//            throw new IllegalArgumentException("Both streets must be connected to this intersection");
//        }
//
//        Point fromEnd = fromStreet.getEnd().equals(location) ? fromStreet.getStart() : fromStreet.getEnd();
//        Point toStart = toStreet.getStart().equals(location) ? toStreet.getEnd() : toStreet.getStart();
//
//        return Point.turnType(fromEnd, location, toStart);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intersection that = (Intersection) o;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public String toString() {
        return "Intersection at " + location + " with " + connectedStreets.size() + " connected streets";
    }
}