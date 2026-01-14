package main.pieces;
import main.*;

import java.util.List;

public class Knight extends Piece {
    public Knight(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return MoveCalculator.getKnightMoves(this, board);
    }
}
