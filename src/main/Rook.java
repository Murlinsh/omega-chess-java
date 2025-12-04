import java.util.List;

public class Rook extends Piece {
    public Rook(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getRookMoves(this, board);
    }
}