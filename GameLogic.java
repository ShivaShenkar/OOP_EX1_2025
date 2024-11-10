import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private Stack<Move> lastMoves;
    private ArrayList<Position> validMoves;
    private ArrayList<Move> currentGameStatus;
    private Player player1,player2;

    public GameLogic() {
        lastMoves= new Stack<Move>();
        reset();
    }



    @Override
    public boolean locate_disc(Position position, Disc disc) {

    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        for(Move mov : currentGameStatus){
            if(mov.getPosition().equals(position))
                return mov.getDisc();
        }
        return null;
    }

    @Override
    public int getBoardSize() {
        return 64;
    }

    @Override
    public List<Position> ValidMoves() {
        return validMoves;
    }

    @Override
    public int countFlips(Position a) {
        return 0;
    }

    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 =player1;
        this.player2 =player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return false;
    }

    @Override
    public boolean isGameFinished() {
        return validMoves.isEmpty();
    }

    @Override
    public void reset() {
        clear(lastMoves);
        resetGameStatus(currentGameStatus);
        validMoves=checkForValidMoves(currentGameStatus,player1);
    }

    @Override
    public void undoLastMove() {
        if(!lastMoves.isEmpty()) {
            lastMoves.pop();
            System.out.println();
        }
        else{
            System.out.println();
        }
    }

    //New methods by me
    private ArrayList<Position> checkForValidMoves(ArrayList<Move> currentGameStatus, Player player) {
        for(int row=1;row<=8;row++){
            for(int col=1;col<=8;col++){
                if(!locate_disc())
            }
        }
    }
    private void clear(Stack<Move> lastMoves) {
        while(!lastMoves.isEmpty())
            lastMoves.pop();
    }
    private void resetGameStatus(ArrayList<Move> gameStatus) {
        gameStatus.clear();
        gameStatus.add(new Move(new Position(5,4),new SimpleDisc(player1)));
        gameStatus.add(new Move(new Position(5,5),new SimpleDisc(player2)));
        gameStatus.add(new Move(new Position(4,4),new SimpleDisc(player2)));
        gameStatus.add(new Move(new Position(4,5),new SimpleDisc(player1)));
    }
}
