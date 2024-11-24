import java.util.ArrayList;
import java.util.List;

public class RandomAI extends AIPlayer {
    Player player;

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    //this method is responsible for the random AI's move choosing algorithm
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

    private Disc chooseDisc(Player player, ArrayList<Integer> discs) {
        while (true) {
            int rand = (int) (Math.random() * discs.size());
            int discType = discs.get(rand);
            if (discType == 1 && player.getNumber_of_unflippedable() > 0) {
                player.reduce_unflippedable();
                return new UnflippableDisc(player);
            }
            if (discType == 2 && player.getNumber_of_bombs() > 0) {
                player.reduce_bomb();
                return new BombDisc(player);
            }
            if (rand != 0 && (player.number_of_unflippedable == 0 || player.number_of_bombs == 0)) {
                return new SimpleDisc(player);
            }
        }
    }
}