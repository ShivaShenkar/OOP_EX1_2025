public class BombDisc implements Disc {
    Player owner;
    public BombDisc(Player currentPlayer) {
        owner=currentPlayer;
    }
    public BombDisc(Disc disc){
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
        return "💣";
    }
}
