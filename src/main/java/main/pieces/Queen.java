package main.pieces;

import main.Board;
import main.Color;
import main.MoveCalculator;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(MoveCalculator.getRookMoves(this, board));
        moves.addAll(MoveCalculator.getBishopMoves(this, board));
        return moves;
    }
}
