
public class HumanPlayer extends Player {
    public HumanPlayer(boolean b) {
        super(b);
        System.out.println("shayom");
    }

    @Override
    boolean isHuman() {
        return true;
    }
}
