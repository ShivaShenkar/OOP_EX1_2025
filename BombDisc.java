//class of the bomb disc in game
public class BombDisc implements Disc {
    Player owner; //owner represents the player which owns that disc
    public BombDisc(Player currentPlayer) {
        owner = currentPlayer;
    }
    public BombDisc(Disc disc){
        owner = disc.getOwner();
    }

    //getters and setters
    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        owner=player;
    }

    @Override
    public String getType() {
        return "💣";
    }
}
