package main;
import java.util.List;

public class Rook extends Piece {
    private boolean hasMoved;
    public Rook(Color color, Position startPosition) {
        super(color, startPosition);
        this.hasMoved = false;
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getRookMoves(this, board);
    }

    // Метод для обновления состояния после хода
    public void markAsMoved() {
        hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void resetMoved() {
        hasMoved = false;
    }
}