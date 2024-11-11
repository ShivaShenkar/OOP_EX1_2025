import java.util.ArrayList;
import java.util.List;

public class RandomAI extends AIPlayer {
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        int rnd = (int) Math.floor(Math.random()*validMoves.size());
        Move nxtMove = new Move(validMoves.get(rnd));
    }
}
