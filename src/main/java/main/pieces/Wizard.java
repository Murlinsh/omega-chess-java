package main.pieces;

import main.*;

import java.util.ArrayList;
import java.util.List;

public class Wizard extends Piece {
    public Wizard(Color color, Position startPosition) {
        super(color, startPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(MoveCalculator.getRookMoves(this, board));
        moves.addAll(MoveCalculator.getBishopMoves(this, board));
        return moves;
    }

    @Override
    public List<Position> getAttackingSquares(Board board) {
        return getPossibleMoves(board);
    }
}
