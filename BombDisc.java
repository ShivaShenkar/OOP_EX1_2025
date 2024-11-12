public class BombDisc implements Disc {
    Player owner;
    public BombDisc(Player currentPlayer) {
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
        return "💣";
    }
}
