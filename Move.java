//class represents an in-game move which consists the disc played with
// and the position in which the discs was played
public class Move {
    private Position position; //The move's position
    private Disc disc;  //The move's disc

    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
    }

    public Position position() {
        return position;
    }

    public Disc disc() {
        return disc;
    }


}
