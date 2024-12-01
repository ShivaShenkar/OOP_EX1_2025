import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * this method's purpose is to choose the move that will flip the most discs for the opponent.
     *it gets a game status and calling one of its method , count flips . it shows the amount
     *of flips for every valid position . using the comparator the method chooses the "best" move .
     *if there are more than one best more it chooses the most right move and so on.
     * @param gameStatus represent the current status of the game
     * @return GreedyAI's move
     * */
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

