public class UnflippableDisc implements Disc {
    private Player owner;
    public UnflippableDisc(Player currentPlayer) {
        owner=currentPlayer;
    }
    public UnflippableDisc(Disc disc){
        owner = disc.getOwner();
    }

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
