import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getPawnMoves(this, board);
    }

    // Метод для обновления состояния после хода
    public void markAsMoved() {
        hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
}