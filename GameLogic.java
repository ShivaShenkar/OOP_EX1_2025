/**
 * Class manages the logics and rules of the Reversi game
 */
import java.util.*;

public class GameLogic implements PlayableLogic {
    static final private int GAME_SIZE = 8;
    private Stack<Move> lastMoves; //stores the last moves played
    private ArrayList<Position> validMoves; //stores all positions that the player can make
    private Disc[][] currentGameStatus; // 2D array that shows what's going on the board
    private Player player1, player2;

    private Stack<Disc[][]> gameVersions; // stores all previous versions of the game played

    private ArrayList<int[]>[][] flippableDiscs; // Data structure stores all of flippable discs for every available position at the board


    /**
     * C'tor of class, resets all properties.
     */
    public GameLogic() {
        flippableDiscs =new ArrayList[GAME_SIZE][GAME_SIZE];
        currentGameStatus = new Disc[GAME_SIZE][GAME_SIZE];
        validMoves=new ArrayList<>();
        gameVersions= new Stack<>();
        player1 = new HumanPlayer(true);
        player2 = new HumanPlayer(false);
        reset();
    }


    /**
     * method finds if a given position is a valid move and
     * implements it if so with the appropriate disc
     * @param position of the move
     * @param disc of the move
     * @return true if the move is valid and successful, false otherwise.
     */

    @Override
    public boolean locate_disc(Position position, Disc disc) {
        Player attacker = disc.getOwner();
        for(Position vMove : validMoves){
            if(vMove.equals(position)) {
                Move newMove = new Move(position,disc);
                Disc deepCopy = null;
                switch (disc.getType()) {
                    case "💣":
                        if(attacker.getNumber_of_bombs()<=0)
                            return false;
                        deepCopy = new BombDisc(disc);
                        break;
                    case "⭕":
                        if(attacker.getNumber_of_unflippedable()<=0)
                            return false;
                        deepCopy = new UnflippableDisc(disc);
                        break;
                    case "⬤":
                        deepCopy= new SimpleDisc(disc);
                        break;
                    default:
                        break;
                }
                int num = (disc.getOwner().isPlayerOne) ? 1 :2;
                System.out.println("Player "+num+" placed a "+disc.getType()+" in "+ position.toString());
                lastMoves.push(new Move(position,deepCopy));
                updateGameStatus(newMove); //updates the game board if new move is successful
                return true;
            }
        }
        return false;
    }

    /**
     method returns Disc which is placed in the given position.
     If there's no Disc there the method returns null
     * @param position of the disc
     * @return the disc located at board at position.
     */
    @Override
    public Disc getDiscAtPosition(Position position) {
        if(position==null)
            return null;
        return currentGameStatus[position.row()][position.col()];
    }

    /**
     * @return the size of the game board
     */
    @Override
    public int getBoardSize() {
        return GAME_SIZE;
    }

    /**
     * @return validMoves of the game
     */
    @Override
    public List<Position> ValidMoves() {
        return validMoves;
    }

    /**
     * method calculates the amount of flips that will occur
     * if a Disc will be positioned at a
     * @param a position which is being calculated
     * @return size of flippableDiscs at position a
     */

    @Override
    public int countFlips(Position a) {
        ArrayList<int[]> res = new ArrayList<>();
        Player attacker = (isFirstPlayerTurn()) ? player1 : player2;
        int[][] directions = {{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        for(int[] arr : directions){
            res.addAll(addFlipsByDirection(a, attacker, arr));
        }
        //there might be duplicate positions at the arrayList,
        //so we need to filter them
        res=filterDuplicates(res);
        flippableDiscs[a.row()][a.col()]=res;
        return res.size();
    }


    //get methods for players
    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    //set method for players
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * method checks if it's the first player's turn or the second
     * @return true if it's the first player's turn, false otherwise.
     */
    @Override
    public boolean isFirstPlayerTurn() {
        if(lastMoves.isEmpty())
            return true;
        return !lastMoves.peek().disc().getOwner().isPlayerOne;
    }

    //method checks if the game is finished and prints a victory message
    //according to the game stats
    @Override
    public boolean isGameFinished() {
        if(validMoves.isEmpty()){
            int n1 = getNumOfDiscs(player1),n2 = getNumOfDiscs(player2);
            if(n1>n2) {
                System.out.println("Player 1 wins with "+n1+" discs! Player 2 had "+n2+" discs.");
                player1.addWin();
            }
            if(n2>n1) {
                System.out.println("Player 2 wins with "+n2+" discs! Player 1 had "+n1+" discs.");
                player2.addWin();
            }
            return true;
        }
        return false;
    }

    /**method resets the GameLogic properties and resets the board**/
    @Override
    public void reset() {
        lastMoves = new Stack<>();
        gameVersions = new Stack<>();
        resetGameStatus(currentGameStatus);
        validMoves = checkForValidMoves();
        player2.reset_bombs_and_unflippedable();
        player1.reset_bombs_and_unflippedable();
        flippableDiscs= new ArrayList[GAME_SIZE][GAME_SIZE];
    }

    /**method undoes the last move, prints the game status and presents
     * the board as of the last move wasn't played**/
    @Override
    public void undoLastMove() {
        System.out.println("Undoing last move :");
        if (!lastMoves.isEmpty()) {
            Move last = lastMoves.pop();
            System.out.println("\tUndo: removing "+last.disc().getType()+" from ("+last.position().row()+", "+last.position().col()+")");
            Player lastPlayer = last.disc().getOwner();
            if(last.disc().getType().equals("💣"))
                lastPlayer.number_of_bombs++;
            else if(last.disc().getType().equals("⭕"))
                lastPlayer.number_of_unflippedable++;
            printFlippedDiscs(true);
            System.out.println();
            currentGameStatus = gameVersions.pop();
            validMoves = checkForValidMoves();
        }
        else
            System.out.println("\tNo previous move available to undo.");
    }

    /**method prints flipped discs with matching messages according to the correct operation
     * if op=true it means the operation is: undo
     * if op=false it means the operation is: a new move has performed
     * @param op the boolean that represents the operation
     */
    private void printFlippedDiscs(boolean op) {
        Disc temp1,temp2;
        Disc[][] lastVersion = gameVersions.peek();
        int n = (isFirstPlayerTurn()) ? 2: 1;
        for(int i=0;i<GAME_SIZE;i++){
            for(int j=0;j<GAME_SIZE;j++){
                temp1 = currentGameStatus[i][j];
                temp2 = lastVersion[i][j];
                if(temp2!=null){
                    if(!temp2.getOwner().equals(temp1.getOwner()))
                        if(op)
                            System.out.println("\tUndo: flipping back "+temp1.getType()+" in ("+i+", "+j+")");
                        else
                            System.out.println("Player "+n+" flipped the "+temp1.getType()+" in ("+i+", "+j+")");
                }
            }
        }
        System.out.println();
    }

    /**method clear the game board from discs
     * @param board represent the game board;
     */
    private void clear(Disc[][] board) {
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                board[i][j] = null;
            }
        }
    }

    /**Method resets the game board by implementing the starting discs
     * @param gameStatus represents the game board
     */
    private void resetGameStatus(Disc[][] gameStatus) {
        clear(gameStatus);
        gameStatus[3][3] = new SimpleDisc(player1);
        gameStatus[4][4] = new SimpleDisc(player1);
        gameStatus[3][4] = new SimpleDisc(player2);
        gameStatus[4][3] = new SimpleDisc(player2);

    }

    /**Method finds the valid moves of the player which it's his turn
     * @return ArrayList of all valid moves
    */
    private ArrayList<Position> checkForValidMoves(){
        Position tempPos;
        ArrayList<Position> result = new ArrayList<>();
        for(int i =0;i<getBoardSize();i++){
            for(int j=0;j<getBoardSize();j++){
                tempPos = new Position(i,j);
                if(getDiscAtPosition(tempPos)!=null)
                    continue;
                if(countFlips(tempPos)>0)
                    result.add(tempPos);
            }
        }
        return result;
    }


    /**method implements newest move to the board,
    * flips the appropriate discs after playing the move and updates it to the board
    * @param move represent the new move that was played
    */
    private void updateGameStatus(Move move){
        Position pos = move.position();
        Disc disc = move.disc();
        Player attacker = disc.getOwner();
        //gameVersions stores the current board status since it's going to change
        //I made a clone() method for Disc[][] because the builtin Java clone method doesn't work
        gameVersions.push(clone(currentGameStatus));
        currentGameStatus[pos.row()][pos.col()] = disc;
        if(disc.getType().equals("💣"))
            attacker.reduce_bomb();
        else if(disc.getType().equals("⭕"))
            attacker.reduce_unflippedable();
        //updating board
        for(int[] posArr : flippableDiscs[pos.row()][pos.col()]){
            currentGameStatus[posArr[0]][posArr[1]].setOwner(attacker);
        }
        printFlippedDiscs(false);
        //after updating the board, the turn is over
        // and the program checks for valid moves for the next player
        validMoves=checkForValidMoves();
    }


    /**method clones the game's board
     * builtin java method doesn't work
     * @param gameStatus represents the game board
     * @return a cloned version of gameStatus
     */
     private Disc[][] clone(Disc[][] gameStatus){
        Disc[][] result = new Disc[gameStatus.length][gameStatus[0].length];
        Disc temp;
        for(int i=0;i<result.length;i++){
            for(int j=0;j<result[0].length;j++){
                temp = gameStatus[i][j];
                if(temp!=null) {
                    switch (temp.getType()) {
                        case "💣":
                            result[i][j] = new BombDisc(temp);
                            break;
                        case "⭕":
                            result[i][j] = new UnflippableDisc(temp);
                            break;
                        case "⬤":
                            result[i][j] = new SimpleDisc(temp);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return result;
    }

    /**method checks for flippable discs of the attacking player at a certain position
     *  and a certain direction.The method stores all of flippable discs in an ArrayList
     *  @param pos the position in which the program checks for flippable discs
     *  @param dir an int array with 2 values which represent the direction
     *  (horizontal,vertical,diagonal) the program searches for flippable discs
     *  @param attacker the player which it's his current turn
     *  @return an ArrayList<int[]> that have the positions of the flippable discs
     *  */
    private ArrayList<int[]> addFlipsByDirection(Position pos,Player attacker,int[] dir){
        ArrayList<int[]> result = new ArrayList<>();
        if(pos==null||dir==null||dir.length!=2)
            return null;
        if(pos.row()<0||pos.row()>GAME_SIZE||pos.col()<0||pos.col()>GAME_SIZE)
            return null;

        Disc tempDisc;
        int hor=dir[0],ver=dir[1],x=pos.row(),y=pos.col();
        x+=hor;y+=ver;
        //lastColor represents the color of the last disc visited
        //false means same as of the attacker,true otherwise
        boolean lastColor=false;
        while ((x>=0&&x<getBoardSize())&&(y>=0&y<getBoardSize())){
            tempDisc = currentGameStatus[x][y];
            //if the loop reached to a null it means the sequence of discs has ended
            //and the program cant check for more flippable discs
            if(tempDisc==null) {
                //if the sequence has ended with a disc of an opposite color
                //from the attacker, the discs aren't flippable
                if(lastColor)
                    result = new ArrayList<>();
                break;
            }
            else if(!tempDisc.getOwner().equals(attacker)) {
                if(!tempDisc.getType().equals("⭕")) {
                    int[] tempPos = {x, y};
                    result.add(tempPos);

                    //if the loop reached to a bomb discs, its surrounding discs might be flippable
                    if (tempDisc.getType().equals("💣")) {
                        addBombFlips(tempPos, result);
                    }
                }
                lastColor=true;
            }
            //if the loop reached to a disc with the same color
            //as of the attacker,it shouldn't search for more flippable discs
            else
                break;
            x+=hor;y+=ver;
        }

        //if the loop reached the boundaries of the board
        // it means the discs at the direction are unflippable
        if(x<0||x>=getBoardSize()||y<0||y>=getBoardSize())
            result = new ArrayList<>();
        return result;
    }

    /**method is related to the previous method, it stores to the ArrayList res
     * flippable discs from a flipped bomb disc.
     *  @param pos the position of the bomb disc that could be flipped
     *  @param res an arrayList that contains flippable discs from the previous method */
    private void addBombFlips(int[] pos,ArrayList<int[]> res){
        int[][] directions ={{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        Disc start=currentGameStatus[pos[0]][pos[1]],temp;
        //temperately changing the disc at pos from bomb to simple
        //in order to avoid stack overflow.
        currentGameStatus[pos[0]][pos[1]] = new SimpleDisc(start);
        //checking for surrounding discs
        for(int[] arr:directions){
            int x=pos[0]+arr[0],y=pos[1]+arr[1];
            if(x>-1&&x<getBoardSize()&&y>-1&&y<getBoardSize())
                temp=currentGameStatus[x][y];
            else
                temp=null;
            if(temp!=null&&temp.getOwner().equals(start.getOwner())&&!temp.getType().equals("⭕")) {
                int[] tempPos = {x,y};
                //if we found another bomb with the opposite color as of the attacker (from previous method)
                //we do recursion
                if(temp.getType().equals("💣")) {
                    res.add(tempPos);
                    addBombFlips(tempPos, res);
                }
                else
                    res.add(tempPos);
            }
        }
        currentGameStatus[pos[0]][pos[1]] = new BombDisc(start);
    }

    /**method checks for duplicated items in an ArrayList<int[]> and erase them
     *  @param flippableDiscs the array that should be filtered
     *  @return the filtered ArrayList*/
    private ArrayList<int[]> filterDuplicates(ArrayList<int[]> flippableDiscs) {
        ArrayList<int[]> result = new ArrayList<>();
        boolean flag=false;
        for(int[] arr1:flippableDiscs){
            for(int[] arr2:result){
                if(arr1[0]==arr2[0]&&arr1[1]==arr2[1]){
                    flag=true;
                    break;
                }
            }
            if(!flag)
                result.add(arr1);
            flag=false;
        }
        return result;
    }

    /**method returns the number of discs at the board of the selected player
     *  @param player the selected player
     *  @return number of discs he owns at the board*/
    private int getNumOfDiscs(Player player) {
        int count=0;
        for(int i=0;i<GAME_SIZE;i++){
            for(int j=0;j<GAME_SIZE;j++){
                if(currentGameStatus[i][j]!=null){
                    if(currentGameStatus[i][j].getOwner().equals(player))
                        count++;
                }
            }
        }
        return count;
    }

}

