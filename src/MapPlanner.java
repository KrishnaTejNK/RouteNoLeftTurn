import java.util.*;

public class MapPlanner {
    /**
     * Create the Map Planner object.  The degrees provided tell us how much deviation from straight-forward
     * is needed to identify an actual turn in a route rather than a straight-on driving.
     * @param degrees
     */
    private int degree;
    private Location depot;
    private Map<String, Street> streets;
    private Map<Point, Intersection> intersections;

    public MapPlanner(int degrees) {
        this.streets = new HashMap<>();
        this.intersections = new HashMap<>();
        this.degree = degrees;
    }
    /**
     * Identify the location of the depot.  That location is used as the starting point of any route request
     * to a destiation
     * @param depot -- the street ID and side of the street (left or right) where we find the depot
     * @return -- true if the depot was set.  False if there was a problem in setting the depot location.
     */
    public Boolean depotLocation( Location depot ) {
        if(depot == null || depot.getStreetId()==null || depot.getStreetSide() == null)return false;

        this.depot = depot;
        return true;
    }

    /**
     * Add a street to our map of the city.  The street is identified by the unique street id.
     * Although the parameters indicate a start and an end to the street, the street is bi-directional.
     * The start and end are just relevant when identifying the side of the street for some location.
     *
     * Street coordinates are in metres.
     *
     * Streets that share coordinates of endpoints meet at an intersection and you can drive from one street to the
     * other at that intersection.
     * @param streetId -- unique identifier for the street.
     * @param start -- coordinates of the starting intersection for the street
     * @param end -- coordinates of the ending entersection for the street
     * @return -- true if the street could be added.  False if the street isn't available in the map.
     */
    public Boolean addStreet(String streetId, Point start, Point end) {
        // Input validation
        if (streetId == null || start == null || end == null || streetId.isEmpty()) {
            return false;
        }

        // Check if the street already exists
        if (streets.containsKey(streetId)) {
            return false;
        }

        // Create and add the new street
        Street newStreet = new Street( start, end);
        streets.put(streetId, newStreet);

        // Update intersections
        updateIntersection(newStreet.getStart(), newStreet);
        updateIntersection(newStreet.getEnd(), newStreet);

        return true;
    }
    private void updateIntersection(Point point, Street street) {
        Intersection intersection = intersections.getOrDefault(point, new Intersection(point));
        intersection.addStreet(street);
        intersections.put(point, intersection);
    }

    /**
     *  Given a depot location, return the street id of the street that is furthest away from the depot by distance,
     *  allowing for left turns to get to the street.
     */
    public String furthestStreet() {
        return null;
    }

    /**
     * Compute a route to the given destination from the depot, given the current map and not allowing
     * the route to make any left turns at intersections.
     * @param destination -- the destination for the route
     * @return -- the route to the destination, or null if no route exists.
     */
    public Route routeNoLeftTurn( Location destination ) {
        return null;
    }

    Street getStreet(String streetId){
        return streets.get(streetId);
    }
}
