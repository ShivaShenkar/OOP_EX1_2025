import java.util.*;


public class GameLogic implements PlayableLogic {
    final private int GAME_SIZE = 8;
    private Stack<Move> lastMoves; //stores the last moves played
    private ArrayList<Position> validMoves; //stores all positions that the player can make
    private Disc[][] currentGameStatus; // 2D array that shows what's going on the board
    private Player player1, player2;

    private Stack<Disc[][]> gameVersions; // stores all previous versions of the game played

    private ArrayList<Integer[]> flippableDiscs;


    //initializes all properties and resets game
    public GameLogic() {
        flippableDiscs = new ArrayList<>();
        currentGameStatus = new Disc[GAME_SIZE][GAME_SIZE];
        validMoves=new ArrayList<>();
        gameVersions= new Stack<>();
        player1 = new HumanPlayer(true);
        player2 = new HumanPlayer(false);
        reset();
    }


    //method finds if a given position is a valid move and
    // implements it if so with the appropriate disc
    @Override
    public boolean locate_disc(Position position, Disc disc) {
        for(Position vMove : validMoves){
            if(vMove.equals(position)) {
                Move newMove = new Move(position,disc);
                Disc deepCopy = null;
                switch (disc.getType()) {
                    case "💣":
                        deepCopy = new BombDisc(disc);
                        break;
                    case "⭕":
                        deepCopy = new UnflippableDisc(disc);
                        break;
                    case "⬤":
                        deepCopy= new SimpleDisc(disc);
                        break;
                    default:
                        break;
                }
                lastMoves.push(new Move(position,deepCopy));
                updateGameStatus(newMove); //updates the game board if new move is successful
                return true;
            }
        }
        return false;
    }


    //method returns Disc which is placed in given position.
    //If there's no Disc there the method returns null
    @Override
    public Disc getDiscAtPosition(Position position) {
        if(position==null)
            return null;
        return currentGameStatus[position.row()][position.col()];
    }

    //method returns the size of the game board
    @Override
    public int getBoardSize() {
        return GAME_SIZE;
    }

    //get method for validMoves
    @Override
    public List<Position> ValidMoves() {
        return validMoves;
    }

    //method calculates the amount of flips that will occur
    //if a Disc will be positioned at a
    @Override
    public int countFlips(Position a) {
        flippableDiscs = new ArrayList<>();
        Player attacker = (isFirstPlayerTurn()) ? player1 : player2;
        int[][] directions = {{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        for(int[] arr : directions){
            flippableDiscs.addAll(addFlipsByDirection(a, attacker, arr));
        }
        flippableDiscs=filterDuplicates(flippableDiscs);


//        if(!flippableDiscs.isEmpty()) {
//            System.out.println("Position: (" + a.row() + "," + a.col() + "), Set: ");
//            for (Integer[] arr : flippableDiscs)
//                System.out.println("(" + arr[0] + "," + arr[1] + ")");
//        }
        return flippableDiscs.size();
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

    //method checks if it's the first player's turn or the second
    @Override
    public boolean isFirstPlayerTurn() {
        if(lastMoves.isEmpty())
            return true;
        return !lastMoves.peek().getDisc().getOwner().isPlayerOne;
    }

    //method checks if the game is finished
    @Override
    public boolean isGameFinished() {
        if(validMoves.isEmpty()){
            int n1 = getNumOfDiscs(player1),n2 = getNumOfDiscs(player2);
            if(n1>n2)
                player1.addWin();
            if(n2>n1)
                player2.addWin();
            return true;
        }
        return false;
    }



    //method resets the GameLogic properties and resets the board
    @Override
    public void reset() {
        lastMoves = new Stack<>();
        gameVersions = new Stack<>();
        resetGameStatus(currentGameStatus);
        validMoves = checkForValidMoves();
        player2.reset_bombs_and_unflippedable();
        player1.reset_bombs_and_unflippedable();
    }

    //method undoes the last move and shows the board as of the last move wasn't played
    @Override
    public void undoLastMove() {
        if (!lastMoves.isEmpty()) {
            Move last = lastMoves.pop();
            Player lastPlayer = last.getDisc().getOwner();
            if(last.getDisc().getType().equals("💣"))
                lastPlayer.number_of_bombs++;
            else if(last.getDisc().getType().equals("⭕"))
                lastPlayer.number_of_unflippedable++;
            System.out.println("Player :"+lastPlayer.toString());
            System.out.println("Bombs: "+lastPlayer.getNumber_of_bombs()+". Unflippable: "+lastPlayer.getNumber_of_unflippedable());
            currentGameStatus = gameVersions.pop();
            validMoves = checkForValidMoves();
        }
    }

    //method clear the game board from discs
    private void clear(Disc[][] board) {
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                board[i][j] = null;
            }
        }
    }

    //Method resets the game board by implementing the starting discs
    private void resetGameStatus(Disc[][] gameStatus) {
        clear(gameStatus);
        gameStatus[3][3] = new SimpleDisc(player1);
        gameStatus[4][4] = new SimpleDisc(player1);
        gameStatus[3][4] = new SimpleDisc(player2);
        gameStatus[4][3] = new SimpleDisc(player2);

    }

    //Method finds the valid moves of the player which it's his turn
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


    //method flips the appropriate discs after playing a move and updates it to
    //the board
    private void updateGameStatus(Move move){
        Position pos = move.getPosition();
        Disc disc = move.getDisc();
        Player attacker = move.getDisc().getOwner();
        //gameVersions stores the current board status since it's going to change
        //I made a clone() method for Disc[][] because the builtin Java clone method doesn't work
        gameVersions.push(clone(currentGameStatus));
        int x,y;
        //array stores every available direction
        int[][] directions = {{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        currentGameStatus[pos.row()][pos.col()] = disc;
        if(disc.getType().equals("💣"))
            attacker.reduce_bomb();
        else if(disc.getType().equals("⭕"))
            attacker.reduce_unflippedable();
        for(int[] arr : directions){
            //if method finds flippable discs at a certain direction
            // it will go over that direction and flip these discs
            if(countFlipsByDirection(pos,attacker,arr)>0) {
                int hor = arr[0], ver = arr[1];
                x=pos.row()+arr[0];y=pos.col()+arr[1];
                while ((x >= 0 && x < getBoardSize()) && (y >= 0 & y < getBoardSize())) {
                    disc = currentGameStatus[x][y];
                    if (disc == null)
                        break;
                    if(!disc.getOwner().equals(attacker)) {
                        if (disc.getType().equals("💣")) {
                            flipBombDisc(new Position(x, y), attacker);
                        }
                        if (!disc.getType().equals("⭕")) {
                            disc.setOwner(attacker);
                        }
                    }
                    x += hor;
                    y += ver;
                }
            }

        }
        //after updating the board, the turn is over
        // and the program checks for valid moves for the next player
        validMoves=checkForValidMoves();
    }

    //method flips surrounding discs of a bomb disc
    private void flipBombDisc(Position pos,Player attacker){
        int[][] directions ={{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        Disc temp;
        currentGameStatus[pos.row()][pos.col()] = new SimpleDisc(attacker);
        for(int[] arr:directions){
            int x=pos.row()+arr[0],y=pos.col()+arr[1];
            if(x>-1&&x<getBoardSize()&&y>-1&&y<getBoardSize())
                temp=currentGameStatus[x][y];
            else
                temp=null;
            if(temp!=null&&!temp.getType().equals("⭕")) {
                if(temp.getType().equals("💣")&&!temp.getOwner().equals(attacker)) {
                    temp.setOwner(attacker);
                    flipBombDisc(new Position(x, y), attacker);
                }
                temp.setOwner(attacker);
            }
        }
        currentGameStatus[pos.row()][pos.col()] =new BombDisc(attacker);
    }

    //method finds the amount of flippable discs of a certain direction
    //from a specific position
    private int countFlipsByDirection(Position pos,Player attacker,int[] dir){
        //method assumes that the direction is presented by an array with 2 values
        //first value for rows and second for columns
        if(pos==null||dir==null||dir.length!=2)
            return 0;
        Disc tempDisc;
        int hor=dir[0],ver=dir[1],x=pos.row(),y=pos.col();
        x+=hor;y+=ver;
        int flips=0;
        //boolean check the color of the last disc visited at the loop
        //false means the same as of the owner, true otherwise.
        boolean lastColor=false;
        //loop goes at the given direction and checks for flippable discs
        while ((x>=0&&x<getBoardSize())&&(y>=0&y<getBoardSize())){
            tempDisc = currentGameStatus[x][y];
            if(tempDisc==null) {
                //if the loop faces a position at the direction which doesn't have a disc
                //it ends the loop and checks the last disc color
                //if the color is not the same as of the player's, the discs aren't flippable
                if(lastColor)
                    flips=0;
                break;
            }
            else if(!tempDisc.getOwner().equals(attacker)&&!tempDisc.getType().equals("⭕")) {
                flips++;
                lastColor=true;
            }
            else if(tempDisc.getOwner().equals(attacker)) {
                lastColor=false;
                break;
            }
            else
                lastColor=true;
            x+=hor;y+=ver;
        }
        //if loop is out of bounds it means discs are unflippable
        if(x<0||x>=getBoardSize()||y<0||y>=getBoardSize())
            flips=0;
        return flips;
    }
    //method clones the game's board
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
    private void addBombFlips(Integer[] pos,ArrayList<Integer[]> res){
        int[][] directions ={{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};
        Disc start=currentGameStatus[pos[0]][pos[1]],temp;
        currentGameStatus[pos[0]][pos[1]] = new SimpleDisc(start);
        for(int[] arr:directions){
            int x=pos[0]+arr[0],y=pos[1]+arr[1];
            if(x>-1&&x<getBoardSize()&&y>-1&&y<getBoardSize())
                temp=currentGameStatus[x][y];
            else
                temp=null;
            if(temp!=null&&temp.getOwner().equals(start.getOwner())&&!temp.getType().equals("⭕")) {
                Integer[] tempPos = {x,y};
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

    private ArrayList<Integer[]> addFlipsByDirection(Position pos,Player attacker,int[] dir){
        ArrayList<Integer[]> result = new ArrayList<>();
        if(pos==null||dir==null||dir.length!=2)
            return null;
        if(pos.row()<0||pos.row()>GAME_SIZE||pos.col()<0||pos.col()>GAME_SIZE)
            return null;

        Disc tempDisc;
        int hor=dir[0],ver=dir[1],x=pos.row(),y=pos.col();
        x+=hor;y+=ver;
        boolean lastColor=false;
        while ((x>=0&&x<getBoardSize())&&(y>=0&y<getBoardSize())){
            tempDisc = currentGameStatus[x][y];
            if(tempDisc==null) {
                if(lastColor)
                    result = new ArrayList<>();
                break;
            }
            else if(!tempDisc.getOwner().equals(attacker)&&!tempDisc.getType().equals("⭕")) {
                Integer[] tempPos = {x,y};
                result.add(tempPos);
                if(tempDisc.getType().equals("💣")){
                    addBombFlips(tempPos,result);
                }
                lastColor=true;
            }
            else if(tempDisc.getOwner().equals(attacker)) {
                lastColor=false;
                break;
            }
            else
                lastColor=true;
            x+=hor;y+=ver;
        }
        if(x<0||x>=getBoardSize()||y<0||y>=getBoardSize())
            result = new ArrayList<>();
        return result;
    }
    private ArrayList<Integer[]> filterDuplicates(ArrayList<Integer[]> flippableDiscs) {
        ArrayList<Integer[]> result = new ArrayList<>();
        boolean flag=false;
        for(Integer[] arr1:flippableDiscs){
            for(Integer[] arr2:result){
                if(arr1[0].equals(arr2[0])&&arr1[1].equals(arr2[1])){
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

