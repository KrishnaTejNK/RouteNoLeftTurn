public class SubRoute {

    Route walk;
    int startLeg;
    int endLeg;
    MapPlanner mapPlanner;

    public Route getWalk() {
        return walk;
    }

    public void setWalk(Route walk) {
        this.walk = walk;
    }

    public int getStartLeg() {
        return startLeg;
    }

    public void setStartLeg(int startLeg) {
        this.startLeg = startLeg;
    }

    public int getEndLeg() {
        return endLeg;
    }

    public void setEndLeg(int endLeg) {
        this.endLeg = endLeg;
    }

    /**
     * Identify a sub-part of a route.  The sub-part of the route goes from the start of the startLeg
     * to the end of the endLeg.
     *
     * The Route from which we start may contain loops.
     * @param walk -- the route from which we are identifying a subroute
     * @param startLeg -- the starting leg of the subroute
     * @param endLeg -- the ending leg of the subroute
     */
    public SubRoute( Route walk, int startLeg, int endLeg, MapPlanner mapPlanner ) {

        if(walk == null ||  mapPlanner == null || startLeg <1 ) throw new IllegalArgumentException("Invalid Parameter Passed");


        this.walk=walk;
        this.startLeg= startLeg;
        this.endLeg=endLeg;
        this.mapPlanner = mapPlanner;
    }

    /**
     * Return the leg number that starts this subroute
     * @return -- the starting leg number
     */
    public int subrouteStart() {
        return startLeg-1;
    }

    /**
     * The ending leg number for this subroute
     * @return - the leg number that ends the subroute
     */
    public int subrouteEnd() {
        return endLeg-1;
    }

    /**
     * Convert this subroute into a pure route of its own.
     * @return -- the Route that represents the subroute all on its own.
     */
    public Route extractRoute() {
        Route newRoute = new Route(mapPlanner);
        if(startLeg > endLeg) return  newRoute;
        newRoute.appendTurn(this.walk.turnDirection(startLeg), walk.turnOnto(startLeg));

        for(int i = startLeg + 1;i<=endLeg;i++){
            newRoute.appendTurn(this.walk.turnDirection(i), walk.turnOnto(i));
        }
        return newRoute;
    }
}
