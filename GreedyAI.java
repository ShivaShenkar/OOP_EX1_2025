import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {
    Player player;

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    //TODO: Make GreedyAI
    //Idan work on it first
    //SHAYOM
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> valids = gameStatus.ValidMoves();
        if (valids.isEmpty()) {
            return null;
        }
        Comparator<Position> bestMoveCompare = (o1, o2) -> {
            int countFlips1 = gameStatus.countFlips(o1);
            int countFlips2 = gameStatus.countFlips(o2);
            if (countFlips1 != countFlips2) {
                return Integer.compare(countFlips1, countFlips2);
            } else if (o1.col() != o2.col()) {
                return Integer.compare(o1.col(), o2.col());

            }
            return Integer.compare(o1.row(), o2.row());
        };
        Position chosenMove = valids.stream().max(bestMoveCompare).orElse(null);
        return new Move(chosenMove , new SimpleDisc(this));
    }

}

