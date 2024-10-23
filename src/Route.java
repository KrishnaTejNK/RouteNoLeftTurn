import java.util.*;

/**
 * Define a route to travel in the map.  It's a sequence of turns and streets in the city map.
 *
 * The first leg of a route is leg 1.
 */
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
     * Grow a Route by adding one step (called a "leg") of the route at a time.  This method adds one more
     * leg to an existing route
     * @param turn -- from the current route, what kind of turn do you make onto the next leg
     * @param streetTurnedOnto -- the street id onto which the next leg of the route turns
     * @return -- true if the leg was added to the route.
     */
    public Boolean appendTurn( TurnDirection turn, String streetTurnedOnto ) {
        
        if(streetTurnedOnto==null || turn == null || streetTurnedOnto.isEmpty())return false;

        Street nextStreet = mapPlanner.getStreet(streetTurnedOnto);
        if(legs.isEmpty()){
            if(turn  == TurnDirection.Straight) {
                legs.put(1, new Leg(turn, streetTurnedOnto, nextStreet.getStart(), nextStreet.getEnd()));
                return true;
            }
        }
        else if(legs.get(legs()).getStartPoint().turnType(legs.get(legs()).getEndPoint(), nextStreet.getEnd(), this.mapPlanner.getDegree()) == turn) {
            legs.put(legs() + 1, new Leg( turn, streetTurnedOnto,nextStreet.getStart(),nextStreet.getEnd()));
            return true;
        }
        else if(legs.get(legs()).getStartPoint().turnType(legs.get(legs()).getEndPoint(), nextStreet.getStart(), this.mapPlanner.getDegree()) == turn){
            legs.put(legs() + 1, new Leg( turn, streetTurnedOnto,nextStreet.getEnd(),nextStreet.getStart()));
            return true;
        }
        return false;
    }

    /**
     * Given a route, report whether the street of the given leg number of the route.
     *
     * Leg numbers begin with 1.
     * @param legNumber -- the leg number for which we want the next street.
     * @return -- the street id of the next leg, or null if there is an error.
     */
    public String turnOnto( int legNumber ) {

        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber) == null) return null;
        return legs.get(legNumber).getStreetTurnedOnto();

    }

    /**
     * Given a route, report whether the type of turn that initiates the given leg number of the route.
     *
     * Leg numbers begin with 1.
     * @param legNumber -- the leg number for which we want the next turn.
     * @return -- the turn direction for the leg, or null if there is an error.
     */
    public TurnDirection turnDirection( int legNumber ) {
        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber)==null) return null;
        return legs.get(legNumber).getTurn();
    }

    /**
     * Report how many legs exist in the current route
     * @return -- the number of legs in this route.
     */
    public int legs() {
        
        return this.legs.size();
        
    }

    /**
     * Report the length of the current route.  Length is computed in metres by Euclidean distance.
     *
     * By assumption, the route always starts and ends at the middle of a road, so only half of the length
     * of the first and last leg roads contributes to the length of the route
     * @return -- the length of the current route.
     */
    public Double length() {
        if (legs.isEmpty()) {
            return 0.0;
        }

        double totalLength = 0.0;
        int numLegs = legs.size();

        for (int i = 1; i <= legs(); i++) {
            Leg leg = legs.get(i);
            double legLength = 0;

            // If accumulated length is not set, calculate it from the street
            String streetId = leg.getStreetTurnedOnto();
            Street street = mapPlanner.getStreet(streetId);
            legLength = street.getLength();


            if (i == 1 || i == numLegs) {
                totalLength += legLength / 2;
            } else {
                totalLength += legLength;
            }
        }

        return totalLength;
    }


    /**
     * Given a route, return all loops in the route.
     *
     * A loop in a route is a sequence of streets where we start and end at the same intersection.  A typical
     * example of a loop would be driving around the block in a city.  A loop does not need you to start and end
     * the loop going in the same direction.  It's just a point of driving through the same intersection again.
     *
     * A route may contain more than one loop.  Return the loops in order that they start along the route.
     *
     * If one loop is nested inside a larger loop then only report the larger loop.
     * @return -- a list of subroutes (starting and ending legs) of each loop.  The starting leg and the ending leg
     * share a common interesection.
     */

    public List<SubRoute> loops() {
        List<SubRoute> loopList = new ArrayList<>();
        if (legs.size() < 2) {  // A loop needs at least 2 legs
            return loopList;
        }

        for (int startLeg = 2; startLeg <= legs.size(); startLeg++) {
            Point startPoint = legs.get(startLeg).getStartPoint();
            Point currentPoint = legs.get(startLeg).getEndPoint();

            for (int endLeg = startLeg + 1; endLeg <= legs.size(); endLeg++) {
                currentPoint = legs.get(endLeg).getEndPoint();

                if (currentPoint.equals(startPoint)) {
                    // We've found a loop
                    if (endLeg - startLeg >= 1) {  // Ensure the loop has at least 2 legs
                        loopList.add(new SubRoute(this,startLeg, endLeg));
                    }
                    break;  // Stop looking for more loops starting from this leg
                }
            }
        }
        System.out.println(loopList);
        return loopList;
    }

    /**
     * Given a route, produce a new route with simplified instructions.  The simplification reports a route
     * that reports the turns in the route but does not report the points where you should keep going straight
     * along your current path.
     * @return -- the simplified route.
     */
    /**
     * Given a route, produce a new route with simplified instructions. The simplification reports a route
     * that reports the turns in the route but does not report the points where you should keep going straight
     * along your current path.
     * @return -- the simplified route.
     */
    public Route simplify() {
        Route simplifiedRoute = new Route(this.mapPlanner);
        if (legs.isEmpty()) {
            return simplifiedRoute;
        }

        Stack<Leg> straightLegs = new Stack<>();
        String currentStreet = null;
        double accumulatedLength = 0;

        for (int i = 1; i <= legs(); i++) {
            Leg currentLeg = legs.get(i);
            TurnDirection currentTurn = currentLeg.getTurn();
            String streetTurnedOnto = currentLeg.getStreetTurnedOnto();
            double legLength = mapPlanner.getStreet(streetTurnedOnto).getLength();

            if (currentTurn == TurnDirection.Straight && streetTurnedOnto.equals(currentStreet)) {
                straightLegs.push(currentLeg);
                accumulatedLength += legLength;
            } else {
                if (!straightLegs.isEmpty()) {
                    // Combine all straight legs into a single leg
                    Leg firstStraight = straightLegs.firstElement();
                    Leg lastStraight = straightLegs.lastElement();
                    Leg combinedLeg = new Leg(TurnDirection.Straight, currentStreet,
                            firstStraight.getStartPoint(), lastStraight.getEndPoint());
                    combinedLeg.setAccumulatedLength(accumulatedLength);
                    simplifiedRoute.legs.put(simplifiedRoute.legs() + 1, combinedLeg);

                    straightLegs.clear();
                    accumulatedLength = 0;
                }

                // Add the current non-straight leg
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
