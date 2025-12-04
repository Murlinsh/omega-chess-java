package main;
import java.util.List;

public class King extends Piece {
    public King(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getKingMoves(this, board);
    }
}
