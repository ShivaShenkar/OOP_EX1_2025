public class Position {
    private int row;
    private int col;



    public Position(int row, int col) {
        this.row=row;
        this.col=col;
    }
    public int row() {
        return row;
    }
    public int col() {
        return col;
    }


    public boolean equals(Position position) {
        return this.row==position.row() && this.col== position.col();
    }
}
