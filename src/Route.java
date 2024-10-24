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

    public Boolean appendTurn( TurnDirection turn, String streetTurnedOnto ) {

        if(streetTurnedOnto==null || turn == null || streetTurnedOnto.isEmpty() || mapPlanner.getStreet(streetTurnedOnto) == null)return false;

        Street nextStreet = mapPlanner.getStreet(streetTurnedOnto);
        if(legs.isEmpty()){

                legs.put(1, new Leg(turn, streetTurnedOnto, nextStreet.getStart(), nextStreet.getEnd()));
                return true;

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

    public String turnOnto( int legNumber ) {

        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber) == null) return null;
        return legs.get(legNumber).getStreetTurnedOnto();

    }


    public TurnDirection turnDirection( int legNumber ) {
        if(legNumber == 0 || legs.isEmpty() || legs.get(legNumber)==null) return null;
        return legs.get(legNumber).getTurn();
    }


    public int legs() {
        
        return this.legs.size();
        
    }


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



    public List<SubRoute> loops() {
        List<SubRoute> loopList = new ArrayList<>();
        if (legs.size() < 2) {  // A loop needs at least 2 legs
            return loopList;
        }
        Map<Point, Integer> pointToLegMap = new HashMap<>();
        for (int currentLeg = 1; currentLeg <= legs.size(); currentLeg++) {
            Point currentPoint = legs.get(currentLeg).getEndPoint();
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
                if (!isNested) {
                    loopList.add(newLoop);
                }
            } else {
                pointToLegMap.put(currentPoint, currentLeg + 1);
            }
        }
        return loopList;
    }


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
