import java.util.ArrayList;
import java.util.List;

public class RandomAI extends AIPlayer {
    Player player;

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    //this method is responsible for the random AI's move choosing algorithm. it receives
    // the current game status for each player . choosing a random move out of the random moves
    // and also choosing a random disc type depending on their quantity (at some point the bomb discs
    // and the unflappable discs are not available. )
    //the method returns the move is a disc type.
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null;
        }
        int rand = (int) (Math.random() * validMoves.size());
        Position move = validMoves.get(rand);
        if (gameStatus.isFirstPlayerTurn()) {
            player = gameStatus.getFirstPlayer();
        } else {
            player = gameStatus.getSecondPlayer();
        }
        ArrayList<Integer> types = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            types.add(i);
        }
        Disc disc = chooseDisc(player, types);
        return new Move(move, disc);
    }

    // this is a private method that helps to choose a random disc type according to the game rules.
    //after a random number is chosen (that represent a disc type) , the method checks if the disc type
    // is available and then returns it . else it will choose a different disc.
    private Disc chooseDisc(Player player, ArrayList<Integer> discs) {
        while (true) {
            int rand = (int) (Math.random() * discs.size());
            int discType = discs.get(rand);
            if (discType == 1 && player.getNumber_of_unflippedable() > 0) {
                return new UnflippableDisc(player);
            }
            if (discType == 2 && player.getNumber_of_bombs() > 0) {
                return new BombDisc(player);
            }
            if (rand != 0 && (player.number_of_unflippedable == 0 || player.number_of_bombs == 0)) {
                return new SimpleDisc(player);
            }
        }
    }
}