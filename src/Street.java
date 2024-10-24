public class Street {
    private String id;
    private Point start;
    private Point end;


    public Street(String id, Point start, Point end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double getLength() {
        return start.distanceTo(end);
    }
    public Point getOtherEnd(Point point) {
        if (point.equals(start)) {
            return end;
        } else if (point.equals(end)) {
            return start;
        } else {
            return null; // or throw an IllegalArgumentException
        }
    }

    String startCords (){
        return this.getStart().toString();
    }

    String endCords(){
        return this.getEnd().toString();
    }

}