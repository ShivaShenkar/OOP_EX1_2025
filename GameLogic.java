import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    final private int GAME_SIZE = 8;
    private Stack<Move> lastMoves;
    private ArrayList<Position> validMoves;

    //changed currentGameStatus to Disc[][]
    private Disc[][] currentGameStatus;
    private Player player1, player2;

    public GameLogic() {
        currentGameStatus = new Disc[GAME_SIZE][GAME_SIZE];
        validMoves=new ArrayList<>();
        player1 = new HumanPlayer(true);
        player2 = new HumanPlayer(false);
        reset();

    }


    //TODO: implement method
    @Override
    public boolean locate_disc(Position position, Disc disc) {
        for(Position vMove : validMoves){
            if(vMove.equals(position)) {
                Move newMove = new Move(position,disc);
                lastMoves.push(newMove);
                //change current
                return true;
            }
        }
        return false;
    }



    @Override
    public Disc getDiscAtPosition(Position position) {
        if(position==null)
            return null;
        return currentGameStatus[position.row()][position.col()];
    }

    @Override
    public int getBoardSize() {
        return GAME_SIZE;
    }

    @Override
    public List<Position> ValidMoves() {
        return validMoves;
    }

    //TODO: haven't used yet
    //Haven't finished plus there is repeating code
    //SHAYOOOOOOOOOOM
    @Override
    public int countFlips(Position a) {
        int counter=0,flagCounter=0;
        Disc temp;
        Player attacker = (isFirstPlayerTurn()) ? player1 : player2;
        boolean flag=false;

        //check for flips on top
        for(int i=a.row()-1;i>=0;i--){
            temp = currentGameStatus[i][a.col()];
            //if there is no disc
            if(temp==null)
                break;
            // if the disc's color is the opposite and flippable
            else if(!temp.getOwner().equals(attacker)&&!temp.getType().equals("⭕"))
                flagCounter++;

            //if the disc has the same color, exit loop
            else{
                flag=true;
                break;
            }
        }
        if(flag){
            counter+=flagCounter;
            flagCounter=0;
            flag=false;
        }

        //Do the same for all directions:
        //TODO: Shorten repeating code

        //check for flips on bottom
        for(int i=a.row()+1;i<GAME_SIZE;i++){
            temp = currentGameStatus[i][a.col()];
            //if there is no disc
            if(temp==null)
                break;
                // if the disc's color is the opposite and flippable
            else if(!temp.getOwner().equals(attacker)&&!temp.getType().equals("⭕"))
                flagCounter++;

                //if the disc has the same color, exit loop
            else{
                flag=true;
                break;
            }
        }
        if(flag){
            counter+=flagCounter;
            flagCounter=0;
            flag=false;
        }


        //check for flips on left
        for(int i=a.col()-1;i>=0;i--){
            temp = currentGameStatus[a.row()][i];
            //if there is no disc
            if(temp==null)
                break;
                // if the disc's color is the opposite and flippable
            else if(!temp.getOwner().equals(attacker)&&!temp.getType().equals("⭕"))
                flagCounter++;

                //if the disc has the same color, exit loop
            else{
                flag=true;
                break;
            }
        }
        if(flag){
            counter+=flagCounter;
            flagCounter=0;
            flag=false;
        }

        //check for flips on right
        for(int i=a.col()+1;i<GAME_SIZE;i++){
            temp = currentGameStatus[a.row()][i];
            //if there is no disc
            if(temp==null)
                break;
                // if the disc's color is the opposite and flippable
            else if(!temp.getOwner().equals(attacker)&&!temp.getType().equals("⭕"))
                flagCounter++;

                //if the disc has the same color, exit loop
            else{
                flag=true;
                break;
            }
        }
        if(flag){
            counter+=flagCounter;
            flagCounter=0;
            flag=false;
        }
        return 0;

        //check for diagonal

        //
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
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        if(lastMoves.isEmpty())
            return true;
        return lastMoves.peek().getDisc().getOwner().isPlayerOne;
    }

    @Override
    public boolean isGameFinished() {
        return validMoves.isEmpty();
    }

    @Override
    public void reset() {
        lastMoves = new Stack<>();
        resetGameStatus(currentGameStatus);
        //validMoves = checkForValidMoves(currentGameStatus, player1);
    }

    @Override
    public void undoLastMove() {
        if (!lastMoves.isEmpty()) {
            lastMoves.pop();
            System.out.println();
        } else {
            System.out.println();
        }
    }


    private void clear(Stack<Move> lastMoves) {
        while(!lastMoves.isEmpty())
            lastMoves.pop();
    }
    private void clear(Disc[][] board) {
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                board[i][j] = null;
            }
        }
    }
    //Method resets the game status
    private void resetGameStatus(Disc[][] gameStatus) {
        clear(gameStatus);
        gameStatus[3][3] = new SimpleDisc(player1);
        gameStatus[4][4] = new SimpleDisc(player1);
        gameStatus[3][4] = new SimpleDisc(player2);
        gameStatus[4][3] = new SimpleDisc(player2);
    }



    /**Tried to make function updateGameStatus which updates currentGameStatus after a move is made
    It got complicated and I haven't finished, if you want to work on it try then (Shayom)**/


    //I made a new method which updates the game status when a new move is played
 /**   private void updateGameStatus(Move newMove) {
        //checks for potential flips of discs to all directions :

        //checks for potential flips on the new move's row
        checkForFlips(newMove,true);

        //checks for potential flips on the new move's column
        checkForFlips(newMove,false);

    }
    private void checkForFlips(Move move, boolean row){
        Position pos = move.getPosition();
        Player owner = move.getDisc().getOwner();

        int start = (row) ? pos.row(): pos.col();
        Position temp1=null,temp2=null;
        for(int i=start+1,j=start-1;i<getBoardSize()||j>=0;i++,j--){
            if(i<getBoardSize())
                temp1 = (row)? new Position(i,pos.col()):new Position(pos.row(),i);
            if(j>=0)
                temp2 = (row)? new Position(j,pos.col()):new Position(pos.row(),j);
            if(getDiscAtPosition(temp1)==null)
                i=getBoardSize();
            else if (getDiscAtPosition(temp1).getOwner().equals(owner)) {
                flipDiscs(pos,temp1,false);
                i=getBoardSize();
            }
            if(getDiscAtPosition(temp2)==null)
                j=-1;
            else if (getDiscAtPosition(temp2).getOwner().equals(owner)) {
                flipDiscs(pos,temp1,false);
                j=-1;
            }
        }
    }
    private void flipDiscs(Position start, Position end,boolean diagonal) {
        if(diagonal)
            flipDiscsDiagonal(start,end);
        if(start.col()<end.col()&&start.row()<end.row()){

        }
        else if(start.col()>end.col()&&start.row()>end.row()){

        }

    }

    //flips discs diagonally
    private void flipDiscsDiagonal(Position start, Position end) {
        int i = start.row(),j=start.col();
        boolean b1=false,b2=false;
        //top diagonal
        if(start.row()> end.row()){
            i--;
            b1=i>end.row();
            //top left diagonal
            if(start.col()>end.col()){
                j--;
                b2=j>end.col();
            }
            //top right diagonal
            else{
                j++;
                b2 = j< end.col();
            }
        }
        //bottom diagonal
        else{
            i++;
            b1 = i<end.row();
            //bottom left diagonal
            if(start.col()>end.col()){
                j--;
                b2=j>end.col();
            }
            //bottom right diagonal
            else{
                j++;
                b2=j<end.col();
            }
        }
        Disc flipDisc;
        Player newOwner;
        for(i=i,j=j;b1&&b2;i=(i> start.row())?i+1:i-1,j=(j> start.col())?j+1:j-1){
            flipDisc=currentGameStatus[i][j];
            newOwner = (flipDisc.getOwner().isPlayerOne) ? player2 : player1;
            if(!flipDisc.getType().equals("⭕"))
                flipDisc.setOwner(newOwner);
            findB1B2(start,end,i,j,b1,b2);
        }
    }

    private void findB1B2(Position start, Position end, int i, int j,boolean b1,boolean b2) {
        if(start.row()> end.row()){
            b1=i>end.row();
            if(start.col()>end.col())
                b2 = j > end.col();
            else
                b2 = j < end.col();
        }
        else{
            b1=i<end.row();
            if(start.col()>end.col())
                b2 = j > end.col();
            else
                b2 = j < end.col();
        }
    }**/
}

