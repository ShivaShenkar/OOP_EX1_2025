import java.util.ArrayList;
import java.util.List;

public class RandomAI extends AIPlayer {
    Player player;
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        GameLogic gameLogic= new GameLogic();
        List<Position> validMoves = gameStatus.ValidMoves();
        int rnd = (int) Math.floor(Math.random()*validMoves.size());
        return new Move(validMoves.get(rnd));
    }
}
