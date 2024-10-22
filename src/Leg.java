public class Leg {
    TurnDirection turn;
    String streetTurnedOnto;

    public Leg(TurnDirection turn, String streetTurnedOnto) {
        this.turn = turn;
        this.streetTurnedOnto = streetTurnedOnto;
    }

    public TurnDirection getTurn() {
        return turn;
    }

    public void setTurn(TurnDirection turn) {
        this.turn = turn;
    }

    public String getStreetTurnedOnto() {
        return streetTurnedOnto;
    }

    public void setStreetTurnedOnto(String streetTurnedOnto) {
        this.streetTurnedOnto = streetTurnedOnto;
    }
}
