public class Street {

    private Point start;
    private Point end;

    public Street( Point start, Point end) {

        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double getLength() {
        this.start.distanceTo(this.end);
        return start.distanceTo(end);
    }

}