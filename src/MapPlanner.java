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
    /**
     * Identify the location of the depot.  That location is used as the starting point of any route request
     * to a destiation
     * @param depot -- the street ID and side of the street (left or right) where we find the depot
     * @return -- true if the depot was set.  False if there was a problem in setting the depot location.
     */
    public Boolean depotLocation( Location depot ) {
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
        if(this.depot == null){
            return null;
        }

        String answer = null;
        double max = -1.0;

        Route route = new Route(this);
        route.appendTurn(TurnDirection.Straight, this.depot.getStreetId());

        Map<String, Integer> visited = new HashMap<>();
        Map<String, Double> shortestPath = new HashMap<>();
        Map<String, Route> visitedRoute = new HashMap<>();
        Stack<Location> stack = new Stack<>();

        Location currentLocation = this.depot;
        visited.put(this.depot.getStreetId(), 0);
        shortestPath.put(depot.getStreetId(),0.0);
        visitedRoute.put(currentLocation.getStreetId(), route);

        String key;
        if(this.depot.getStreetSide() == StreetSide.Right){
            key = streets.get(currentLocation.getStreetId()).endCords();
        }else{
            key = streets.get(currentLocation.getStreetId()).startCords();
        }

        Set<Location> neighbors = graph.get(key);

        for(Location loc : neighbors){
            if(loc.getStreetId().equals(this.depot.getStreetId())){
                continue;
            }
            stack.push(loc);
        }


        while(!stack.isEmpty()){
            Location currentStreet = stack.peek();
            String nextStreetId = currentStreet.getStreetId();
            Point turnFrom;
            if(currentLocation.getStreetSide() == StreetSide.Right){
                turnFrom = streets.get(currentLocation.getStreetId()).getStart();
            }else{
                turnFrom = streets.get(currentLocation.getStreetId()).getEnd();
            }


            String nextKey;
            Point turnAt;
            Point turnTo;

            if(currentStreet.getStreetSide() == StreetSide.Right){
                nextKey = streets.get(nextStreetId).endCords();
                turnAt = streets.get(nextStreetId).getStart();
                turnTo = streets.get(nextStreetId).getEnd();
            }else{
                nextKey = streets.get(nextStreetId).startCords();
                turnAt = streets.get(nextStreetId).getEnd();
                turnTo = streets.get(nextStreetId).getStart();
            }

            Set<Location> nextNeighbors = graph.get(nextKey);

            TurnDirection turn = turnFrom.turnType(turnAt, turnTo, this.degree);
            currentLocation = currentStreet;
            if(turn != TurnDirection.UTurn && route.appendTurn(turn , nextStreetId)){
                double currentLength = route.length();
                if(visited.containsKey(nextStreetId)){
                    SubRoute subRoute = new SubRoute(visitedRoute.get(nextStreetId), 1, visited.get(nextStreetId), this);
                    Route newRoute = subRoute.extractRoute();
                    double prevLength = newRoute.length();

                    if(prevLength <= currentLength){
                        route = new SubRoute(route, 1, route.legs() - 1,this).extractRoute();
                        if(streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)){
                            currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Right);
                        }else{
                            currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Left);
                        }

                        stack.pop();

                        continue;
                    }
                }
                visited.put(nextStreetId, route.legs());
                shortestPath.put(nextStreetId, currentLength);

                Route visitRoute;
                try{
                    SubRoute subRoute = new SubRoute(route, 1, route.legs(),this);
                    visitRoute = subRoute.extractRoute();
                }catch (Exception e){
                    return null;
                }

                visitedRoute.put(nextStreetId, visitRoute);


                if(nextNeighbors == null || nextNeighbors.isEmpty()){
                    stack.pop();
                }

                for(Location loc : nextNeighbors){
                    if(loc.getStreetId().equals(nextStreetId)){
                        continue;
                    }
                    stack.push(loc);
                }
            }else{

                route = new SubRoute(route, 1, route.legs() - 1,this).extractRoute();

                if(streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)){
                    currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Right);
                }else{
                    currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Left);
                }
                stack.pop();
            }
        }

        for(Map.Entry<String, Double> entry : shortestPath.entrySet()){
            String streetId = entry.getKey();
            Double distance = entry.getValue();

            if(Double.compare(distance,max) > 0){
                max = distance;
                answer = streetId;
            }
        }
        return answer;
    }

    /**
     * Compute a route to the given destination from the depot, given the current map and not allowing
     * the route to make any left turns at intersections.
     * @param destination -- the destination for the route
     * @return -- the route to the destination, or null if no route exists.
     */
    public Route routeNoLeftTurn(Location destination) {
        if(this.depot == null){
            return null;
        }

        String answer = null;
        double max = -1.0;

        Route route = new Route(this);
        route.appendTurn(TurnDirection.Straight, this.depot.getStreetId());

        Map<String, Integer> visited = new HashMap<>();
        Map<String, Double> shortestPath = new HashMap<>();
        Map<String, Route> visitedRoute = new HashMap<>();
        Stack<Location> stack = new Stack<>();

        Location currentLocation = this.depot;
        visited.put(this.depot.getStreetId(), 0);
        visitedRoute.put(currentLocation.getStreetId(), route);

        String key;
        if(this.depot.getStreetSide() == StreetSide.Right){
            key = streets.get(currentLocation.getStreetId()).endCords();
        }else{
            key = streets.get(currentLocation.getStreetId()).startCords();
        }

        Set<Location> neighbors = graph.get(key);

        for(Location loc : neighbors){
            if(loc.getStreetId().equals(this.depot.getStreetId())){
                continue;
            }
            stack.push(loc);
        }


        while(!stack.isEmpty()){
            Location currentStreet = stack.peek();
            String nextStreetId = currentStreet.getStreetId();
            Point turnFrom;
            if(currentLocation.getStreetSide() == StreetSide.Right){
                turnFrom = streets.get(currentLocation.getStreetId()).getStart();
            }else{
                turnFrom = streets.get(currentLocation.getStreetId()).getEnd();
            }


            String nextKey;
            Point turnAt;
            Point turnTo;

            if(currentStreet.getStreetSide() == StreetSide.Right){
                nextKey = streets.get(nextStreetId).endCords();
                turnAt = streets.get(nextStreetId).getStart();
                turnTo = streets.get(nextStreetId).getEnd();
            }else{
                nextKey = streets.get(nextStreetId).startCords();
                turnAt = streets.get(nextStreetId).getEnd();
                turnTo = streets.get(nextStreetId).getStart();
            }

            Set<Location> nextNeighbors = graph.get(nextKey);

            TurnDirection turn = turnFrom.turnType(turnAt, turnTo, this.degree);
            currentLocation = currentStreet;
            if(turn != TurnDirection.UTurn && turn != TurnDirection.Left && route.appendTurn(turn , nextStreetId)){
                double currentLength = route.length();
                if(visited.containsKey(nextStreetId)){
                    SubRoute subRoute = new SubRoute(visitedRoute.get(nextStreetId), 1, visited.get(nextStreetId), this);
                    Route newRoute = subRoute.extractRoute();
                    double prevLength = newRoute.length();

                    if(prevLength <= currentLength){
                        route = new SubRoute(route, 1, route.legs() - 1,this).extractRoute();
                        if(streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)){
                            currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Right);
                        }else{
                            currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Left);
                        }

                        stack.pop();

                        continue;
                    }
                }
                visited.put(nextStreetId, route.legs());
                shortestPath.put(nextStreetId, currentLength);

                Route visitRoute;
                try{
                    SubRoute subRoute = new SubRoute(route, 1, route.legs(),this);
                    visitRoute = subRoute.extractRoute();
                }catch (Exception e){
                    return null;
                }

                visitedRoute.put(nextStreetId, visitRoute);


                if(nextNeighbors == null || nextNeighbors.isEmpty()){
                    stack.pop();
                }

                for(Location loc : nextNeighbors){
                    if(loc.getStreetId().equals(nextStreetId)){
                        continue;
                    }
                    stack.push(loc);
                }
            }else{
                if(turn != TurnDirection.Left) {
                    route = new SubRoute(route, 1, route.legs() - 1, this).extractRoute();

                    if (streets.get(route.turnOnto(route.legs())).getStart().equals(route.legs.get(route.legs()).startPoint)) {
                        currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Right);
                    } else {
                        currentLocation = new Location(route.turnOnto(route.legs()), StreetSide.Left);
                    }
                }
                stack.pop();
            }
        }

        for(Map.Entry<String, Double> entry : shortestPath.entrySet()){
            String streetId = entry.getKey();
            Double distance = entry.getValue();

            if(Double.compare(distance,max) > 0){
                max = distance;
                answer = streetId;
            }
        }
        return visitedRoute.get(destination.getStreetId());
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
