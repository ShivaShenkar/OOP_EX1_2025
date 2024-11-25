public class Move {
    private Position position;
    private Disc disc;

    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
    }
//    public Move(Position position){
//        this.position=position;
//    }

    public Position getPosition() {
        return position;
    }

    public Disc getDisc() {
        return disc;
    }

    public Position position(){
        return position;
    }
    public Disc disc(){
        return disc;
    }
}
