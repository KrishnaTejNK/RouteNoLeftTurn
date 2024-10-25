import java.util.*;


public class Route {
    MapPlanner mapPlanner;
    Map<Integer,Leg> legs;
    Route(){
        legs = new HashMap<Integer, Leg>();
    }

    Route(MapPlanner mapPlanner){
        this.mapPlanner = mapPlanner;
        legs = new HashMap<Integer, Leg>();
    }

    /**
     * Adds a new leg to the route
     * @param turn The type of turn to make
     * @param streetTurnedOnto The ID of the street being turned onto
     * @return True if the leg was successfully added, false otherwise
     */
    public Boolean appendTurn(TurnDirection turn, String streetTurnedOnto) {
        // Validate input parameters
        if (streetTurnedOnto == null || turn == null || streetTurnedOnto.isEmpty() || mapPlanner.getStreet(streetTurnedOnto) == null) return false;

        Street nextStreet = mapPlanner.getStreet(streetTurnedOnto);

        if (legs.isEmpty()) {
            // If this is the first leg, add it directly
            legs.put(1, new Leg(turn, streetTurnedOnto, nextStreet.getStart(), nextStreet.getEnd()));
            return true;
        } else {
            // Check if the turn is valid for the end of the street
            if (legs.get(legs()).getStartPoint().turnType(legs.get(legs()).getEndPoint(), nextStreet.getEnd(), this.mapPlanner.getDegree()) == turn) {
                legs.put(legs() + 1, new Leg(turn, streetTurnedOnto, nextStreet.getStart(), nextStreet.getEnd()));
                return true;
            }
            // Check if the turn is valid for the start of the street
            else if (legs.get(legs()).getStartPoint().turnType(legs.get(legs()).getEndPoint(), nextStreet.getStart(), this.mapPlanner.getDegree()) == turn) {
                legs.put(legs() + 1, new Leg(turn, streetTurnedOnto, nextStreet.getEnd(), nextStreet.getStart()));
                return true;
            }
        }

        // If no valid turn was found, return false
        return false;
    }

    public String turnOnto( int legNumber ) {
        // input validations
        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber) == null) return null;
        return legs.get(legNumber).getStreetTurnedOnto();

    }

    public TurnDirection turnDirection( int legNumber ) {
        // input validations
        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber)==null) return null;
        return legs.get(legNumber).getTurn();
    }

    public int legs() {
        
        return this.legs.size();
        
    }

    /**
     * Calculates the total length of the route
     * @return The total length of the route
     */
    public Double length() {
        // Return 0 if the route is empty
        if (legs.isEmpty()) {
            return 0.0;
        }

        double totalLength = 0.0;
        int numLegs = legs.size();

        // Iterate through all legs of the route
        for (int i = 1; i <= legs(); i++) {
            Leg leg = legs.get(i);
            double legLength;

            // Get the length of the street for this leg
            String streetId = leg.getStreetTurnedOnto();
            Street street = mapPlanner.getStreet(streetId);
            legLength = street.getLength();

            // Add half length for first and last legs, full length for others
            if (i == 1 || i == numLegs) {
                totalLength += legLength / 2;
            } else {
                totalLength += legLength;
            }
        }

        return totalLength;
    }

    /**
     * Identifies and returns a list of loops in the route
     * @return List of SubRoutes representing loops
     */
    public List<SubRoute> loops() {
        List<SubRoute> loopList = new ArrayList<>();
        if (legs.size() < 2) {  // A loop needs at least 2 legs
            return loopList;
        }

        Map<Point, Integer> pointToLegMap = new HashMap<>();

        // Iterate through all legs of the route
        for (int currentLeg = 1; currentLeg <= legs.size(); currentLeg++) {
            Point currentPoint = legs.get(currentLeg).getEndPoint();

            // Check if we've seen this point before
            if (pointToLegMap.containsKey(currentPoint)) {
                int startLeg = pointToLegMap.get(currentPoint);
                SubRoute newLoop = new SubRoute(this, startLeg, currentLeg, this.mapPlanner);

                // Check if this loop is nested inside an existing loop
                boolean isNested = false;
                for (SubRoute existingLoop : loopList) {
                    if (existingLoop.getStartLeg() <= startLeg && existingLoop.getEndLeg() >= currentLeg) {
                        isNested = true;
                        break;
                    }
                }

                // Add the loop if it's not nested
                if (!isNested) {
                    loopList.add(newLoop);
                }
            } else {
                // Record this point and its leg number
                pointToLegMap.put(currentPoint, currentLeg + 1);
            }
        }
        return loopList;
    }

    /**
     * Simplifies the route by combining consecutive straight legs on the same street
     * @return A new Route with simplified legs
     */
    public Route simplify() {
        Route simplifiedRoute = new Route(this.mapPlanner);
        if (legs.isEmpty()) {
            return simplifiedRoute;
        }

        Stack<Leg> straightLegs = new Stack<>();
        String currentStreet = null;
        double accumulatedLength = 0;

        // Iterate through all legs of the route
        for (int i = 1; i <= legs(); i++) {
            Leg currentLeg = legs.get(i);
            TurnDirection currentTurn = currentLeg.getTurn();
            String streetTurnedOnto = currentLeg.getStreetTurnedOnto();
            double legLength = mapPlanner.getStreet(streetTurnedOnto).getLength();

            // Check if the leg is a straight continuation on the same street
            if (currentTurn == TurnDirection.Straight && streetTurnedOnto.equals(currentStreet)) {
                straightLegs.push(currentLeg);
                accumulatedLength += legLength;
            } else {
                // Combine all consecutive straight legs into one
                if (!straightLegs.isEmpty()) {
                    Leg firstStraight = straightLegs.firstElement();
                    Leg lastStraight = straightLegs.lastElement();
                    Leg combinedLeg = new Leg(TurnDirection.Straight, currentStreet,
                            firstStraight.getStartPoint(), lastStraight.getEndPoint());
                    combinedLeg.setAccumulatedLength(accumulatedLength);
                    simplifiedRoute.legs.put(simplifiedRoute.legs() + 1, combinedLeg);

                    straightLegs.clear();
                    accumulatedLength = 0;
                }

                // Add the current non-straight leg to the simplified route
                simplifiedRoute.legs.put(simplifiedRoute.legs() + 1, currentLeg);
                currentStreet = streetTurnedOnto;
                accumulatedLength = legLength;
            }
        }

        // Handle any remaining straight legs at the end of the route
        if (!straightLegs.isEmpty()) {
            Leg firstStraight = straightLegs.firstElement();
            Leg lastStraight = straightLegs.lastElement();
            Leg combinedLeg = new Leg(TurnDirection.Straight, currentStreet,
                    firstStraight.getStartPoint(), lastStraight.getEndPoint());
            combinedLeg.setAccumulatedLength(accumulatedLength);
            simplifiedRoute.legs.put(simplifiedRoute.legs() + 1, combinedLeg);
        }

        return simplifiedRoute;
    }
}
