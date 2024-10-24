public class Street {
    private String id;
    private Point start;
    private Point end;

    // Constructor: Initializes a new Street with given id and endpoints
    public Street(String id, Point start, Point end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    // Returns the unique identifier of this street
    public String getId() {
        return id;
    }

    // Returns the start point of this street
    public Point getStart() {
        return start;
    }

    // Returns the end point of this street
    public Point getEnd() {
        return end;
    }

    // Calculates and returns the length of this street
    public double getLength() {
        return start.distanceTo(end);
    }

    // Returns the opposite end point given one end of the street
    public Point getOtherEnd(Point point) {
        if (point.equals(start)) {
            return end;
        } else if (point.equals(end)) {
            return start;
        } else {
            return null; // or throw an IllegalArgumentException
        }
    }

    // Returns the string representation of the start coordinates
    String startCords() {
        return this.getStart().toString();
    }

    // Returns the string representation of the end coordinates
    String endCords() {
        return this.getEnd().toString();
    }
}