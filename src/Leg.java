public class Leg {
    TurnDirection turn;
    String streetTurnedOnto;
    Point startPoint;
    Point endPoint;
    private double accumulatedLength;


    public Leg(TurnDirection turn, String streetTurnedOnto, Point startPoint, Point endPoint) {
        this.turn = turn;
        this.streetTurnedOnto = streetTurnedOnto;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public TurnDirection getTurn() {
        return turn;
    }

    public void setTurn(TurnDirection turn) {
        this.turn = turn;
    }

    public String getStreetTurnedOnto() {
        return streetTurnedOnto;
    }

    public void setStreetTurnedOnto(String streetTurnedOnto) {
        this.streetTurnedOnto = streetTurnedOnto;
    }

    public void setAccumulatedLength(double length) {
        this.accumulatedLength = length;
    }

    public double getAccumulatedLength() {
        return accumulatedLength;
    }


    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

}
