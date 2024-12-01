//class of the simple disc in game
public class SimpleDisc implements Disc{
    private Player owner;   //owner represents the player which owns that disc
    public SimpleDisc(Player currentPlayer) {

        owner = currentPlayer;
    }
    public SimpleDisc(Disc disc){
        owner = disc.getOwner();
    }

    //getters and setters
    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
        owner = player;
    }

    @Override
    public String getType() {
        return "⬤";
    }
}
