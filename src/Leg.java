public class Leg {
    TurnDirection turn;
    String streetTurnedOnto;
    Point startPoint;
    Point endPoint;
    private double accumulatedLength;

    // Constructor: Initializes a new Leg with given parameters
    public Leg(TurnDirection turn, String streetTurnedOnto, Point startPoint, Point endPoint) {
        this.turn = turn;
        this.streetTurnedOnto = streetTurnedOnto;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // Returns the turn direction of this leg
    public TurnDirection getTurn() {
        return turn;
    }

    // Sets the turn direction of this leg
    public void setTurn(TurnDirection turn) {
        this.turn = turn;
    }

    // Returns the street name turned onto in this leg
    public String getStreetTurnedOnto() {
        return streetTurnedOnto;
    }

    // Sets the street name turned onto in this leg
    public void setStreetTurnedOnto(String streetTurnedOnto) {
        this.streetTurnedOnto = streetTurnedOnto;
    }

    // Sets the accumulated length of this leg
    public void setAccumulatedLength(double length) {
        this.accumulatedLength = length;
    }

    // Returns the accumulated length of this leg
    public double getAccumulatedLength() {
        return accumulatedLength;
    }

    // Returns the start point of this leg
    public Point getStartPoint() {
        return this.startPoint;
    }

    // Sets the start point of this leg
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    // Returns the end point of this leg
    public Point getEndPoint() {
        return this.endPoint;
    }

    // Sets the end point of this leg
    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
}