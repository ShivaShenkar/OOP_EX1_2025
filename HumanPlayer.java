//class represents a Human person playing the game
public class HumanPlayer extends Player {
    public HumanPlayer(boolean b) {
        super(b);
    }

    @Override
    boolean isHuman() {
        return true;
    }
}
