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
    private Map<String, Set<Location>> graph;


    public MapPlanner(int degrees) {
        this.streets = new HashMap<>();
        this.degree = degrees;
        this.graph = new HashMap<>();
    }

    private class IntersectionDetails {
        String intersectionKey;
        Point turningPoint;
        Point destinationPoint;

        IntersectionDetails(String intersectionKey, Point turningPoint, Point destinationPoint) {
            this.intersectionKey = intersectionKey;
            this.turningPoint = turningPoint;
            this.destinationPoint = destinationPoint;
        }
    }

    /**
     * Identify the location of the depot.  That location is used as the starting point of any route request
     * to a destiation
     * @param depot -- the street ID and side of the street (left or right) where we find the depot
     * @return -- true if the depot was set.  False if there was a problem in setting the depot location.
     */
    public Boolean depotLocation( Location depot ) {
        //added input validation checks
        if(depot == null || depot.getStreetId()==null || depot.getStreetSide() == null || streets.get(depot.getStreetId())== null )return false;

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
        Street newStreet = new Street( streetId,start, end);
        streets.put(streetId, newStreet);

        // Update graph
        addToGraph(start.toString(), newStreet,StreetSide.Right);
        addToGraph(end.toString(), newStreet,StreetSide.Left);

        return true;
    }



    /**
     *  Given a depot location, return the street id of the street that is the furthest away from the depot by distance,
     *  allowing for left turns to get to the street.
     */
    public String furthestStreet() {
        // Return null if depot is not set
        if (this.depot == null) {
            return null;
        }

        String result = null;
        double maxDistance = -1.0;

        // Initialize route from depot
        Route currentRoute = new Route(this);
        currentRoute.appendTurn(TurnDirection.Straight, this.depot.getStreetId());

        // Data structures for tracking visited streets and routes
        Map<String, Integer> visitedStreets = new HashMap<>();
        Stack<Location> locationStack = new Stack<>();
        Map<String, Double> shortestDistances = new HashMap<>();
        Map<String, Route> recordedRoutes = new HashMap<>();

        // Set up initial location and visited status
        Location presentLocation = this.depot;
        visitedStreets.put(this.depot.getStreetId(), 0);
        shortestDistances.put(depot.getStreetId(), 0.0);
        recordedRoutes.put(presentLocation.getStreetId(), currentRoute);

        // Determine starting intersection key
        String intersectionKey;
        if (this.depot.getStreetSide() == StreetSide.Right) {
            intersectionKey = streets.get(presentLocation.getStreetId()).endCords();
        }
        else {
            intersectionKey = streets.get(presentLocation.getStreetId()).startCords();
        }

        // Get adjacent locations and add to stack
        Set<Location> adjacentLocations = graph.get(intersectionKey);
        for (Location loc : adjacentLocations) {
            if (!loc.getStreetId().equals(this.depot.getStreetId())) {
                locationStack.push(loc);
            }
        }

        // Main loop for exploring routes
        while (!locationStack.isEmpty()) {
            Location nextLocation = locationStack.peek();
            String upcomingStreetId = nextLocation.getStreetId();

            // Determine departure point
            Point departurePoint;
            if (presentLocation.getStreetSide() == StreetSide.Right) {
                departurePoint = streets.get(presentLocation.getStreetId()).getStart();
            } else {
                departurePoint = streets.get(presentLocation.getStreetId()).getEnd();
            }

            IntersectionDetails details = getIntersectionDetails(nextLocation, upcomingStreetId);
            String nextIntersectionKey = details.intersectionKey;
            Point turningPoint = details.turningPoint;
            Point destinationPoint = details.destinationPoint;

            Set<Location> nextAdjacentLocations = graph.get(nextIntersectionKey);

            // Determine turn type and update current location
            TurnDirection turnType = departurePoint.turnType(turningPoint, destinationPoint, this.degree);
            presentLocation = nextLocation;

            // Check if turn is valid and can be appended
            if (turnType != TurnDirection.UTurn && currentRoute.appendTurn(turnType, upcomingStreetId)) {
                double routeLength = currentRoute.length();

                // Check if street has been visited before
                if (visitedStreets.containsKey(upcomingStreetId)) {
                    SubRoute subRouteInstance = new SubRoute(recordedRoutes.get(upcomingStreetId), 1, visitedStreets.get(upcomingStreetId), this);
                    Route alternateRoute = subRouteInstance.extractRoute();
                    double previousLength = alternateRoute.length();

                    // Backtrack if new path is not shorter
                    if (previousLength <= routeLength) {
                        currentRoute = new SubRoute(currentRoute, 1, currentRoute.legs() - 1, this).extractRoute();
                        presentLocation = updatePresentLocation(currentRoute);
                        locationStack.pop();
                        continue;
                    }
                }

                // Update visited streets and distances
                visitedStreets.put(upcomingStreetId, currentRoute.legs());
                shortestDistances.put(upcomingStreetId, routeLength);

                // Store current route
                try {
                    SubRoute subRouteInstance = new SubRoute(currentRoute, 1, currentRoute.legs(), this);
                    Route routeSnapshot = subRouteInstance.extractRoute();
                    recordedRoutes.put(upcomingStreetId, routeSnapshot);
                } catch (Exception e) {
                    return null;
                }

                // Handle dead-ends
                if (nextAdjacentLocations == null || nextAdjacentLocations.isEmpty()) {
                    locationStack.pop();
                }

                // Add unexplored adjacent streets to stack
                for (Location loc : nextAdjacentLocations) {
                    if (!loc.getStreetId().equals(upcomingStreetId)) {
                        locationStack.push(loc);
                    }
                }
            } else {
                // Backtrack if turn is invalid
                currentRoute = new SubRoute(currentRoute, 1, currentRoute.legs() - 1, this).extractRoute();
                presentLocation = updatePresentLocation(currentRoute);
                locationStack.pop();
            }
        }

        // Find the farthest street
        for (Map.Entry<String, Double> entry : shortestDistances.entrySet()) {
            String streetIdentifier = entry.getKey();
            Double distanceValue = entry.getValue();

            if (Double.compare(distanceValue, maxDistance) > 0) {
                maxDistance = distanceValue;
                result = streetIdentifier;
            }
        }

        return result;
    }

    //method to update present location
    private Location updatePresentLocation(Route route) {
        if (streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)) {
            return new Location(route.turnOnto(route.legs()), StreetSide.Right);
        } else {
            return new Location(route.turnOnto(route.legs()), StreetSide.Left);
        }
    }

    //method to get the instersectins based on left or right side of the street
    private IntersectionDetails getIntersectionDetails(Location location, String streetId) {
        Street street = streets.get(streetId);
        if (location.getStreetSide() == StreetSide.Right) {
            return new IntersectionDetails(
                    street.endCords(),
                    street.getStart(),
                    street.getEnd()
            );
        } else {
            return new IntersectionDetails(
                    street.startCords(),
                    street.getEnd(),
                    street.getStart()
            );
        }
    }

    /**
     * Compute a route to the given destination from the depot without making any left turns.
     * @param targetLocation The destination for the route
     * @return The route to the destination, or null if no route exists
     */
    public Route routeNoLeftTurn(Location targetLocation) {
        // Check if depot is set
        if (this.depot == null) {
            return null;
        }

        String farthestStreet = null;
        double maxDistance = -1.0;

        // Initialize the route from the depot
        Route currentPath = new Route(this);
        currentPath.appendTurn(TurnDirection.Straight, this.depot.getStreetId());

        // Initialize data structures for tracking
        Map<String, Integer> exploredStreets = new HashMap<>();
        Map<String, Double> distanceToStreet = new HashMap<>();
        Map<String, Route> pathToStreet = new HashMap<>();
        Stack<Location> locationStack = new Stack<>();

        Location currentSpot = this.depot;
        exploredStreets.put(this.depot.getStreetId(), 0);
        pathToStreet.put(currentSpot.getStreetId(), currentPath);

        // Determine the starting intersection key
        String intersectionKey = (this.depot.getStreetSide() == StreetSide.Right)
                ? streets.get(currentSpot.getStreetId()).endCords()
                : streets.get(currentSpot.getStreetId()).startCords();

        // Get adjacent locations and add them to the stack
        Set<Location> adjacentLocations = graph.get(intersectionKey);
        for (Location loc : adjacentLocations) {
            if (!loc.getStreetId().equals(this.depot.getStreetId())) {
                locationStack.push(loc);
            }
        }

        // Main loop for path finding
        while (!locationStack.isEmpty()) {
            Location nextStreet = locationStack.peek();
            String nextStreetId = nextStreet.getStreetId();

            // Determine turn points
            Point turnOrigin = (currentSpot.getStreetSide() == StreetSide.Right)
                    ? streets.get(currentSpot.getStreetId()).getStart()
                    : streets.get(currentSpot.getStreetId()).getEnd();

            String nextIntersectionKey;
            Point turnMidpoint, turnDestination;

            if (nextStreet.getStreetSide() == StreetSide.Right) {
                nextIntersectionKey = streets.get(nextStreetId).endCords();
                turnMidpoint = streets.get(nextStreetId).getStart();
                turnDestination = streets.get(nextStreetId).getEnd();
            } else {
                nextIntersectionKey = streets.get(nextStreetId).startCords();
                turnMidpoint = streets.get(nextStreetId).getEnd();
                turnDestination = streets.get(nextStreetId).getStart();
            }

            Set<Location> nextAdjacentLocations = graph.get(nextIntersectionKey);

            TurnDirection turnType = turnOrigin.turnType(turnMidpoint, turnDestination, this.degree);
            currentSpot = nextStreet;

            // Check if the turn is valid (not U-turn or Left) and can be appended
            if (turnType != TurnDirection.UTurn && turnType != TurnDirection.Left && currentPath.appendTurn(turnType, nextStreetId)) {
                double pathLength = currentPath.length();

                // Check if we've visited this street before
                if (exploredStreets.containsKey(nextStreetId)) {
                    SubRoute subPath = new SubRoute(pathToStreet.get(nextStreetId), 1, exploredStreets.get(nextStreetId), this);
                    Route alternativePath = subPath.extractRoute();
                    double previousLength = alternativePath.length();

                    // If the new path is not shorter, backtrack
                    if (previousLength <= pathLength) {
                        currentPath = new SubRoute(currentPath, 1, currentPath.legs() - 1, this).extractRoute();
                        currentSpot = determineNewLocation(currentPath);
                        locationStack.pop();
                        continue;
                    }
                }

                // Update explored streets and distances
                exploredStreets.put(nextStreetId, currentPath.legs());
                distanceToStreet.put(nextStreetId, pathLength);

                // Store the current path
                try {
                    SubRoute subPath = new SubRoute(currentPath, 1, currentPath.legs(), this);
                    Route storedPath = subPath.extractRoute();
                    pathToStreet.put(nextStreetId, storedPath);
                } catch (Exception e) {
                    return null;
                }

                // Handle dead-ends
                if (nextAdjacentLocations == null || nextAdjacentLocations.isEmpty()) {
                    locationStack.pop();
                }

                // Add unexplored adjacent streets to the stack
                for (Location loc : nextAdjacentLocations) {
                    if (!loc.getStreetId().equals(nextStreetId)) {
                        locationStack.push(loc);
                    }
                }
            } else {
                // Backtrack if the turn is invalid
                if (turnType != TurnDirection.Left) {
                    currentPath = new SubRoute(currentPath, 1, currentPath.legs() - 1, this).extractRoute();
                    currentSpot = determineNewLocation(currentPath);
                }
                locationStack.pop();
            }
        }

        // Find the farthest street (not used in this method, but kept for consistency)
        for (Map.Entry<String, Double> entry : distanceToStreet.entrySet()) {
            String streetId = entry.getKey();
            Double distance = entry.getValue();

            if (Double.compare(distance, maxDistance) > 0) {
                maxDistance = distance;
                farthestStreet = streetId;
            }
        }

        // Return the path to the destination
        return pathToStreet.get(targetLocation.getStreetId());
    }

    /**
     * Helper method to determine the new location after backtracking
     */
    private Location determineNewLocation(Route route) {
        if (streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)) {
            return new Location(route.turnOnto(route.legs()), StreetSide.Right);
        } else {
            return new Location(route.turnOnto(route.legs()), StreetSide.Left);
        }
    }

    private void addToGraph(String point, Street street, StreetSide side) {
        graph.computeIfAbsent(point, k -> new HashSet<>());
        graph.get(point).add(new Location(street.getId(),side) );
    }

    public void printGraph() {
        System.out.println("Graph representation:");
        for (Map.Entry<String, Set<Location>> entry : graph.entrySet()) {
            String intersection = entry.getKey();
            Set<Location> connectedStreets = entry.getValue();

            System.out.print("Point " + intersection + " connects to: ");
            for (Location location : connectedStreets) {
                System.out.print(" Street : " + location.getStreetId() );
            }
            System.out.println();
        }
    }

    public Set<Location> getAdjacentStreets(String intersection) {
        return graph.getOrDefault(intersection,null);
    }

    Street getStreet(String streetId){
        if(streetId == null || streets.get(streetId) == null )return null;
        return streets.get(streetId);
    }

    int getDegree(){return degree;}

    MapPlanner getMapPlanner(){
        return this;
    }

}
