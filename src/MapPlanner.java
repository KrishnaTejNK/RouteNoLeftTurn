public class MapPlanner {

    /**
     * Create the Map Planner object.  The degrees provided tell us how much deviation from straight-forward
     * is needed to identify an actual turn in a route rather than a straight-on driving.
     * @param degrees
     */
    public MapPlanner( int degrees ) {}

    /**
     * Identify the location of the depot.  That location is used as the starting point of any route request
     * to a destiation
     * @param depot -- the street ID and side of the street (left or right) where we find the depot
     * @return -- true if the depot was set.  False if there was a problem in setting the depot location.
     */
    public Boolean depotLocation( Location depot ) {
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
    public Boolean addStreet( String streetId, Point start, Point end ) {
        return true;
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
}
