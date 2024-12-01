//class of the unflippable disc in game
public class UnflippableDisc implements Disc {
    private Player owner;   //owner represents the player which owns that disc
    public UnflippableDisc(Player currentPlayer) {

        owner=currentPlayer;
    }
    public UnflippableDisc(Disc disc){
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
        return "⭕";
    }
}
