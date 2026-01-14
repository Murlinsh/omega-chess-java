package main.pieces;
import main.*;

import java.util.List;

public class King extends Piece {
    private boolean hasMoved = false;
    public King(Color color, Position startPosition) {
        super(color, startPosition);
        this.hasMoved = false;
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getAllKingMoves(this, board);
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
