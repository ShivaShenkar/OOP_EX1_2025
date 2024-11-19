public class SimpleDisc implements Disc{

    private Player owner;
    public SimpleDisc(Player currentPlayer) {
        owner = currentPlayer;
    }
    public SimpleDisc(Disc disc){
        owner = disc.getOwner();
    }

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
