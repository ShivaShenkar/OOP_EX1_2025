public class UnflippableDisc implements Disc {
    private Player owner;
    public UnflippableDisc(Player currentPlayer) {
        owner=currentPlayer;
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
        return "Unflippable Disc";
    }
}
