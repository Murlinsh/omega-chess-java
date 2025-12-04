import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getBishopMoves(this, board);
    }
}
